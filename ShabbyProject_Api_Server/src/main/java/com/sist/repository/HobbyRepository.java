package com.sist.repository;

import com.sist.dto.hobby.HobbyDTO;
import com.sist.dto.hobby.SearchHobbyDTO;

import java.util.*;

public interface HobbyRepository {

	
	//특정 관심사 조회
	public List<HobbyDTO> findHobby(SearchHobbyDTO dto);
	//닉네임 기반 관심사 리스트
	public List<HobbyDTO> findHobbyByNickname(String nickname);
}
