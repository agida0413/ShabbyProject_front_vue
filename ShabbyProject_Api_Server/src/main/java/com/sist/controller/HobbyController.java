package com.sist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.common.exception.BadRequestException;
import com.sist.common.exception.InternerException;
import com.sist.common.util.PathVariableValidation;
import com.sist.dto.api.ResponseDTO;
import com.sist.dto.hobby.HobbyDTO;
import com.sist.dto.hobby.SearchHobbyListDTO;
import com.sist.service.primary.HobbyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hobby")
public class HobbyController {
	
	private final HobbyService hobbyService;
		
	
	
	//keyword, page 기반으로 관심사를 조회하는 api
	@GetMapping("/{page}")
	public  ResponseEntity<ResponseDTO<SearchHobbyListDTO>> findHobby(@PathVariable int page,@RequestParam String keyword){
		
		if(!PathVariableValidation.pageValidation(page)
			||!PathVariableValidation.keyWordValService(keyword)	) {
			throw new InternerException("유효하지 않은 입력입니다.","validation 실패");
		}
		
		return hobbyService.findHobby(keyword,page);
	}
}
