package com.sist.service.member.setting;



import org.springframework.http.HttpStatus;import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sist.common.exception.BadRequestException;
import com.sist.common.utill.SimpleCodeGet;
import com.sist.dto.MemberDTO;
import com.sist.repository.member.MemberAccountRepository;
import com.sist.repository.member.MemberSettingRepository;
import com.sist.vo.MemberVO;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ChangeInfoServiceImpl implements ChangeInfoService{

	private final MemberAccountRepository memberAccountRepository;
	private final MemberSettingRepository memberSettingRepository;
	private final SimpleCodeGet simpleCodeGet;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//회원 정보 리턴
	//2024.08.19 수정 = > 회원정보 전체를 넘기면 위험할 수 있음 ., 필요한 정보만 넘기게
	@Override
	public ResponseEntity<?> getLockedInfo() {//회원 공개여부 데이터
		// TODO Auto-generated method stub
		
		int idNum=simpleCodeGet.getIdNum();
		
			MemberDTO dto =	memberAccountRepository.findByIdNum(idNum);
	
		
		if(dto==null) {
		throw new BadRequestException("비정상적인 접근입니다.");//사용자 정의400에러 발생
		}
		
		String locked= dto.getLocked();
		return new ResponseEntity<>(locked,HttpStatus.OK);//401;
	}

	//선행조건 : 닉네임중복검사 통과 = >비밀번호 검증=>닉네임변경
	@Transactional
	@Override
	public ResponseEntity<?> nickNameUpdate(MemberDTO dto) {
		// TODO Auto-generated method stub
		int idNum = simpleCodeGet.getIdNum();//고유 아이디넘버 갖고오기 
		
		
		MemberDTO findDto = memberAccountRepository.findByIdNum(idNum);//회원정보 갖고오기 
		
		if(!bCryptPasswordEncoder.matches(dto.getPassword(), findDto.getPassword())) {
			//비밀번호가 일치하지않을시 
			
			throw new BadRequestException("비밀번호가 일치 하지않습니다.");//사용자 정의400에러 발생
		}
		

		
		dto.setIdNum(idNum);
	
		memberSettingRepository.updateNickName(dto);//최종적으로 vo에 닉네임과 ,고유번호 전달하여 데이터베이스 업데이트 
	
	
		
		return new ResponseEntity<>("OK",HttpStatus.OK);//성공 리턴
	}

	
	
	
	//패스워드 변경 
	@Override
	public ResponseEntity<?> passwordUpdate(String password,String newPassword) {
		// TODO Auto-generated method stub
		int idNum=simpleCodeGet.getIdNum();//고유번호를 가져옴 
	
		MemberDTO findDto= memberAccountRepository.findByIdNum(idNum); // 데이터베이스에서 일치하는 회원정보 
		
		
		//입력받은 패스워드와 일치하지않다면 , 
		if(!bCryptPasswordEncoder.matches(password, findDto.getPassword())) {
			throw new BadRequestException("비밀번호가 일치 하지않습니다.");//사용자 정의400에러 발생
		}
		
		if(bCryptPasswordEncoder.matches(newPassword,findDto.getPassword())) {//바꾸려는 패스워드와 기존패스워드가 일치할경우 
			throw new BadRequestException("기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");//사용자 정의400에러 발생 
		}
		newPassword= bCryptPasswordEncoder.encode(newPassword);// 새로운 패스워드 암호화 
		
		MemberDTO dto = new MemberDTO();//dto에 데이터베이스 업데이트 할 값 세팅
		dto.setIdNum(idNum);
		dto.setPassword(newPassword);
		
		memberSettingRepository.updatePassword(dto); //데이터 베이스 비밀번호 업데이트
		
		
		
		return new ResponseEntity<>("OK",HttpStatus.OK);
	}

	
	//핸드폰 번호 변경 ( 비밀번호 검증= > 동일한 휴대폰번호인지 검증 = > 이미 있는 핸드폰번호 인지 검증 )
	@Transactional
	@Override
	public ResponseEntity<?> phoneChange(MemberDTO dto) {
		// TODO Auto-generated method stub
		
		int idNum=simpleCodeGet.getIdNum(); //회원 고유번호
		 
		MemberDTO findDto = memberAccountRepository.findByIdNum(idNum);//데이터 베이스에서 고유번호 기반 정보 같고옴 
		
		if(!bCryptPasswordEncoder.matches(dto.getPassword(), findDto.getPassword())) {//입력받은 비밀번호와 ,데이터베이스 패스워드가 다를시 
			
			throw new BadRequestException("비밀번호가 일치 하지않습니다.");//사용자 정의400에러 발생
		}
		MemberDTO findSamePhoeUser= memberAccountRepository.findByUserPhone(dto.getPhone());
		if (!(findSamePhoeUser==null)) {
			//입력한 핸드폰 번호와 전체회원의 핸드폰 번호중 동일한 핸드폰있는 회원목록을 갖고옴 
		
			//그 회원이 존재한다면 
			throw new BadRequestException("이미 존재하는 번호입니다.");//사용자 정의400에러 발생
		}
		
		
		if(bCryptPasswordEncoder.matches(dto.getPhone(), findDto.getPhone())) {//입력받은 핸드폰 번호와 현재 핸드폰 번호가 같을 시 
			
			throw new BadRequestException("기존 휴대폰번호와 동일한 번호로 수정할 수 없습니다.");//사용자 정의400에러 발생
		}
		
		
		dto.setIdNum(idNum); //dto에 회원고유번호를 담고 
		String phone =bCryptPasswordEncoder.encode(dto.getPhone());//입력받은 휴대폰번호를 암호화 하여 스트링에 담고 
		dto.setPhone(phone); //dto에 세팅한다음 
		memberSettingRepository.updatePhone(dto); // 데이터베이스에 휴대폰 번호 업데이트 
		
		 return new ResponseEntity<>("OK",HttpStatus.OK);
	}
	
	
	
	
	
	

}
