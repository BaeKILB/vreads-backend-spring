package com.vreads.backend.handler;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;


// jwt 방식으로 토큰 받아온 뒤 사용자 인증 동작 하는 클래스
// jwt 토큰을 복호화 한 뒤 firebase auth 를 이용해 인증 여부 확인 후 로그인 등의 절차 진행
public class FirebaseTokenFilter extends OncePerRequestFilter {
	private UserDetailsService userDetailsService; // 사용자 정보 가져오는 서비스
	private FirebaseAuth firebaseAuth; // firebase 의 인증 관련 체크
	
	// 클래스 초기화
    public FirebaseTokenFilter(UserDetailsService userDetailsService, FirebaseAuth firebaseAuth) {
        this.userDetailsService = userDetailsService;
        this.firebaseAuth = firebaseAuth;
    }
	
    
    // 필터 동작
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// request 에서 토큰 받아오기
		// 토큰 받아 디코드한 토큰이 담길 객체
		FirebaseToken decodedToken;
		// Authorization 로 되어있는 헤더 받아오기
		String header = request.getHeader("Authorization");
		
		// 헤더에 Bearer 가 있는지 확인 없으면 나가기
		if(header == null || ! header.startsWith("Bearer ")) {
			setUnauthorizedResponse(response,"INVALID_HEADER"); // response 에 INVALID_HEADER 코드 담기
			return;
		}
		
		// 복호화 안된 토큰 넣기
		String token = header.substring(7);
		try {
			// 위의 FirebaseToken 객체에 복호화 된 토큰 넣기
			decodedToken = firebaseAuth.verifyIdToken(token);
		}
		catch(FirebaseAuthException e) {
			setUnauthorizedResponse(response, "INVALID_TOKEN");
            return;
		}
		
		// decode 된 토큰에서 uid 를 가져온 뒤
		// user 을 가져와서 SecurityContext에 저장하기
		try {
			// security 의 유저정보에 uid 넣기
			UserDetails securityUser = userDetailsService.loadUserByUsername(decodedToken.getUid());
			
			UsernamePasswordAuthenticationToken authentication 
			= new UsernamePasswordAuthenticationToken(securityUser, null,securityUser.getAuthorities());
			
			// SecurityContext에 저장하기
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		catch(NoSuchElementException e) {
			setUnauthorizedResponse(response, "USER_NOT_FOUND");
            return;
		}
		filterChain.doFilter(request, response);
	}
	
	// 반환해줄 response 에 정보 담기
	private void setUnauthorizedResponse(HttpServletResponse response, String code) throws IOException{
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\""+code+"\"}");
		}
	
}
