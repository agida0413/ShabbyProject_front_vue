package com.sist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.common.exception.BadRequestException;
import com.sist.common.util.PathVariableValidation;
import com.sist.dto.api.ResponseDTO;
import com.sist.dto.member.MemberDTO;
import com.sist.dto.setting.ChangeNickNameDTO;
import com.sist.dto.setting.ChangePasswordDTO;
import com.sist.dto.setting.ChangePhoneDTO;
import com.sist.service.member.ChangeInfoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/setting")
@RequiredArgsConstructor
public class SettingController {

	private final ChangeInfoService changeInfoService;
	
	
	//회원정보(공개/비공개 여부 )를 가져오는 api
	@GetMapping
	public ResponseEntity<ResponseDTO<MemberDTO>> getInfo(){
		
		return changeInfoService.getLockedInfo();
	}
	
	//회원탈퇴 api
	//서비스에서 검증 
	@DeleteMapping
	public ResponseEntity<ResponseDTO<Void>> memberDelete(@RequestBody MemberDTO dto){
		//검증
		if (!PathVariableValidation.nameValSevice(dto.getName())
				|| !PathVariableValidation.emailValService(dto.getEmail())) {
			throw new BadRequestException("유효하지 않은 입력입니다.");
		}
		return changeInfoService.deleteMember(dto);
	}
	
	//닉네임 변경하는 api
	@PutMapping("/nickChange")
	public  ResponseEntity<ResponseDTO<Void>> nicknameChange(@RequestBody @Valid ChangeNickNameDTO dto,HttpServletResponse response,HttpServletRequest request){
		return changeInfoService.nickNameUpdate(dto,response,request);
	}
	
	//비밀번호 변경 api
	@PutMapping("/pwdChange")
	public  ResponseEntity<ResponseDTO<Void>> pwdChange(@Valid ChangePasswordDTO dto){ // formdata 
	
	
		return changeInfoService.passwordUpdate(dto);
	}
	
	//핸드폰 번호 변경 api
	@PutMapping("/phoneChange")
	public ResponseEntity<ResponseDTO<Void>> phoneChange(@RequestBody @Valid ChangePhoneDTO dto){
		
		return changeInfoService.phoneChange(dto);
		
	}
	
	@PutMapping("/lockStateChange")
	//서비스에서 검증
	public ResponseEntity<ResponseDTO<MemberDTO>> lockStateChange(@RequestBody MemberDTO dto){
		
		//검증
		if (!dto.getLocked().equals("LOCKED") && !dto.getLocked().equals("PUBLICID")) {
			throw new BadRequestException("유효하지 않은 입력입니다.");
		}
		return changeInfoService.updateLockedState(dto);
		
	}
	
	
}
