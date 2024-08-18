package com.sist.service.member.security;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RefreshService {
	public	void deleteRefresh(String refresh);
	public void addRefreshEntity(int idNum, String refresh, Long expiredMs);
	public Boolean	isExist(String refresh);
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);
	public Cookie createCookie(String key, String value);
	
}
