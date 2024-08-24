package com.sist.service.hobby;

import org.springframework.http.ResponseEntity;

import com.sist.dto.api.ResponseDTO;
import com.sist.dto.hobby.HobbyDTO;
import com.sist.dto.hobby.ResponseHobbyDTO;


public interface HobbyService {
	//전체 관심사 조회
	public ResponseEntity<ResponseDTO<HobbyDTO>> allHobby();
	//특정 관심사 조회
	public ResponseEntity<ResponseDTO<ResponseHobbyDTO>> findHobby(String keyword,int page);
}
