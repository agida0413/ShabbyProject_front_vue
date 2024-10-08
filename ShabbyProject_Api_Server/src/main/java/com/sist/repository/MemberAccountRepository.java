package com.sist.repository;

import com.sist.dto.member.EmailAuthDTO;
import com.sist.dto.member.MemberDTO;



public interface MemberAccountRepository {
		// 이메일 기반 회원정보 찾기
		public MemberDTO findByUserEmail(String email);
		//고유번호 기반 회원정보 찾기
		public MemberDTO findByIdNum(int idNum);
		//회원 자기소개를 고유번호를 통한 셀렉트
		public MemberDTO introduceByUserIdNum(int idNum);
		// 닉네임 기반 회원정보 찾기
		public MemberDTO findByUserNickname(String nickname);
		// 휴대폰번호 기반 회원정보 찾기
		public MemberDTO findByUserPhone(String phone);	
		// 파라미터로 멤버vo 받아 데이터베이스에 저장 
		public void join (MemberDTO dto);	
		//회원가입시 이메일 인증코드 전송, 데이터저장 
		public void emailAuthSave(EmailAuthDTO dto);
		//저장된 인정코드 검증 
		public EmailAuthDTO emailAuthGetValidation(String email);		
		//인증상태 y 로 업데이트
		public void emailAuthClear(int emailAuthNum);	
		//아이디 찾기 
		public MemberDTO findEmail(MemberDTO dto);
		//임시패스워드발급 = > 데이터베이스 저장 
		public void tempPasswordUpdate(MemberDTO dto);
		//공개.비공개 상태획득
		public String getLock(String nickname);
		//회원가입전 최종 validate
		public EmailAuthDTO emailAuthBeforeJoin(String email);
}
