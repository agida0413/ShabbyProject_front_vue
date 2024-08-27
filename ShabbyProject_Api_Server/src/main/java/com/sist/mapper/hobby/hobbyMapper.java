package com.sist.mapper.hobby;

import org.apache.ibatis.annotations.Mapper;
import java.util.*;
import com.sist.dto.hobby.HobbyDTO;
import com.sist.dto.hobby.RequestHobbyDTO;

@Mapper
public interface hobbyMapper {
//전체 관심사 조회	
public HobbyDTO allHobby();
//특정 관심사 조회
public List<HobbyDTO> findHobby(RequestHobbyDTO dto);
//닉네임 기반 관심사 리스트
public List<HobbyDTO> findHobbyByNickname(String nickname);
}
