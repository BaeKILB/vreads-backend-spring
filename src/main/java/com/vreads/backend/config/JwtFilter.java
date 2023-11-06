package com.vreads.backend.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vreads.backend.HomeController;
import com.vreads.backend.service.UserInfoService;
import com.vreads.backend.utils.JwtUtil;
import com.vreads.backend.vo.MemberVO;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

// spring security 관련 코드
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

	// UserDetailsService 를 상속받아 만들어진 클래스 가져오기
	// 여기서는 PrincipalDetailsService 라고 되어있지만
	// 일반적으로 UserService 라고 지음
//	private final PrincipalDetailsService userService;

	private final String secretKey;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// secretKey 를 Key 객체로 변환 해줘야함

//		Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
		Cookie[] cookies = request.getCookies();

		// 헤더에서 인증 코드 꺼내기
		final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorization == null || !authorization.startsWith("Bearer ")) {
			logger.error("authorization 이 없습니다");
			filterChain.doFilter(request, response);
			return;
		}
		System.out.println(authorization);

		// 헤더에서 받아온 값에서 토큰 꺼내기
		String token = authorization.split(" ")[1];

		// 헤더에서 받아온 토큰 기간이 만료되었을때
		// 리프레시 토큰을 확인해 다시 토큰 받아오기
		String newToken = "";

		// 토큰 유효기간 체크
		boolean checkExpried = JwtUtil.isExpired(token, secretKey);
		if (checkExpried) {
			System.out.println("cookies");
			System.out.println(cookies);
			// 쿠키에 리프레시토큰이 있다면 토큰 재발급
			// 쿠키 확인
			if (cookies != null) {

				// 쿠키 안의 토큰 받아올 변수
				String cookieToken = "";

				// 쿠키는 배열로 받아오기 때문에 배열 반복문 돌려서 토큰 확인
				for (Cookie cookie : cookies) {
					if (cookie.getName() == "refreshToken") {
						cookieToken = cookie.getValue();
						break;
					}
				}

				// 리프레시 토큰이 기간 다되었는지 확인
				if (JwtUtil.isExpired(cookieToken, secretKey)) {
					logger.error("재발급 토큰 유효기간이 만료 되었습니다!");
					filterChain.doFilter(request, response);
					return;
				}
				// 리프레시 토큰 기간 유효하면 토큰 다시 만들어 전달
				else {
					// 리프레시 토큰의 유저 받아오기
					String refreshUserId = JwtUtil.getUserId(cookieToken, secretKey);
					// 리프레시 토큰 안의 값이 이상하면 리턴
					if(refreshUserId == null || refreshUserId.length() <= 2) {
						logger.error("재발급 토큰 안의 데이터에 문제가 있습니다!");
						filterChain.doFilter(request, response);
						return;
					}
					// 토큰 재발급 절차

					// 유효시간 지정
					Long expiredMs = 60L * 60 * 1000;

					MemberVO member = new MemberVO();
					member.setMem_id(refreshUserId);
					member.setMem_email(refreshUserId);
					newToken = JwtUtil.createJwt(member, secretKey, expiredMs);

				}
			}
			// 쿠키 받아오지 못했을때
			else {
				logger.error("재발급 토큰을 받아오지 못했습니다!");
				filterChain.doFilter(request, response);
				return;
			}
		}
		// 토큰 안의 id 받아오기
		String userId = "";
		// 유효기간 다되었으면 newToken값으로 받기
		if(checkExpried) {			
			userId = JwtUtil.getUserId(newToken, secretKey);
		}
		else {			
			userId = JwtUtil.getUserId(token, secretKey);
		}

		// 서버에 쓰일 토큰 데이터 담긴 객체
		Map<String, String> authMap = new HashMap<String, String>();

		authMap.put("userId", userId);
		authMap.put("newToken", newToken);

		// 토큰값 잠시 넣어서 쓰도록 해주기
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(authMap, null,
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
		// request에 있는 detail 넣어주기
		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authToken);
		filterChain.doFilter(request, response);
	}

}
