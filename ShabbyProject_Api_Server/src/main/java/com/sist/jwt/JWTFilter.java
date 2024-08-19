package com.sist.jwt;

import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sist.dto.MemberDTO;
import com.sist.service.member.security.CustomUserDetails;
import com.sist.vo.MemberVO;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// TODO Auto-generated method stub
    	
    	 String requestURI = request.getRequestURI();
    	
    	  return requestURI.equals("/api/reissue"); //재발급시에는 필터를 수행하지않음
	}


	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
				
    	// 헤더에서 access키에 담긴 토큰을 꺼냄
    	String accessToken = request.getHeader("access");

    	// 토큰이 없다면 다음 필터로 넘김
    	if (accessToken == null) {
    	
    	    filterChain.doFilter(request, response);

    	    return;
    	}

    	// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
    	try {
    	    jwtUtil.isExpired(accessToken);
    	} catch (ExpiredJwtException e) {
    	
    	 
    	    PrintWriter writer = response.getWriter();
    	    writer.print("access token expired");
    	 
    
	  
  	    

    	    response.setStatus(HttpServletResponse.SC_GONE);//410
    	    return;
    	}

    	// 토큰이 access인지 확인 (발급시 페이로드에 명시)
    	String category = jwtUtil.getCategory(accessToken);

    	if (!category.equals("access")) {

    	
    	    PrintWriter writer = response.getWriter();
    	    writer.print("invalid access token");

    	    //response status code
    	    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    	    return;
    	}

    	// username, role 값을 획득
    	String username = jwtUtil.getUsername(accessToken);
    	String role = jwtUtil.getRole(accessToken);
    	int idNum = jwtUtil.getIdNum(accessToken);//고유번호
    	
    	MemberDTO dto = new MemberDTO();
    	dto.setEmail(username);
    	dto.setRole(role);
    	dto.setIdNum(idNum);
    	CustomUserDetails customUserDetails = new CustomUserDetails(dto);

    	Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    	SecurityContextHolder.getContext().setAuthentication(authToken);

    	filterChain.doFilter(request, response);
    }
}