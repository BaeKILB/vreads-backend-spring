package com.vreads.backend.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import com.vreads.backend.vo.MemberVO;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	public static String getUserId(String token, String secretKey) {
		// signWith 에 키 넣을때 Key 변환 해줘야함
		Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		try {	
		// 받아온 토큰을 키로 복호화 후 .get("키값", 앞의 데이터 형태.class(현재는 String.class)) 로 받기
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("id", String.class);
		}
		catch(ExpiredJwtException e) {
			return null;
		}	
	}

	public static boolean isExpired(String token, String secretKey) {
		// signWith 에 키 넣을때 Key 변환 해줘야함
		Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		try {			
			// 받아온 토큰을 키로 복호화 후 유효 기간 확인
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration()
					.before(new Date());
		}
		catch(ExpiredJwtException e) {
			return true; // true가 복호화 키 유효기간이 끝난것 임으로 유효기간 오류시 true 처리
		}
	}

	// 로그인 시 jwt 만들어주기
	public static String createJwt(MemberVO member, String secretKey, Long expriendMs) {
		// signWith 에 키 넣을때 Key 변환 해줘야함
		Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

		// jwt 담을 Claims 객체 만들기(map의 일종)
		Claims claims = Jwts.claims();
		claims.put("id", member.getMem_id());


		// jwt 키 내보내기
		return Jwts.builder().setClaims(claims) // 정보를 담은 claims 넣고
				.setIssuedAt(new Date(System.currentTimeMillis())) // 현재 만든 시간 지정
				.setExpiration(new Date(System.currentTimeMillis() + expriendMs)) // jwt 유효시간 지정후
				.signWith(key, SignatureAlgorithm.HS256) // 시크릿 키 넣고
				.compact() // 만들기
		;

	}
}
