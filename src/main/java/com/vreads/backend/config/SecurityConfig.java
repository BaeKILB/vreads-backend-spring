package com.vreads.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;


@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
//@Value 를 썼는데 받아오라는 @Value는 안받고 ${jwt_secret} 그대로 반환함
// 찾다가 바로 다이렉트로 소스를 넣는 방법이있어 사용함
@PropertySource("classpath:/config/appdata.properties") 
public class SecurityConfig {

	// UserDetailsService 를 상속받아 만들어진 클래스 가져오기
	// 여기서는 PrincipalDetailsService 라고 되어있지만
	// 일반적으로 UserService 라고 지음
//	private final PrincipalDetailsService userService;
//	
	@Value("${jwt_secret}")
	private String secretKey;
	
    
	@Bean
	public BCryptPasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}
	
    @Bean
    public UserDetailsService userDetailsService() {
        return new PrincipalDetailsService();
    }
	

    
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
//		http.csrf().disable(); // 이제 csrf 토큰 검사 안함
//		http.authorizeRequests()
//		.antMatchers("/login/api/CreateUserPro","/login/api/LoginPro").permitAll()
//			.antMatchers("/mypage/**","/social/**","/api/**").authenticated() // 인증(로그인)한 유저만 갈 수 있는 페이지
//			.antMatchers("/Admi**", "/Inquiry**").hasRole("ADMIN") // "/AdminMain" 페이지는 ROLE_ADMIN 권한이 있는 사용자만 접근 가능
//			.anyRequest().permitAll()
////	    .and()
////	    .formLogin() // Form 로그인 방식
////	    	// 파라미터 설정
////		    .usernameParameter("mem_id") // 시큐리티 설정된 username 을 mem_id 로 바꿈
////		    .passwordParameter("mem_passwd") // 시큐리티 설정된 password 을 mem_passwd 로 바꿈
//////		    .loginPage("/login") // GET 방식 : 로그인 페이지는 매핑을 "/login" 이다 라고 지정 
////		    .loginProcessingUrl("/login/api") // POST 로 요청된 login(내가 지정한 매핑주소) -> 스프링 시큐리티가 로그인 프로세스 진행
////		    .defaultSuccessUrl("/", true)
//	    // 로그아웃
//			.and()
//	    .logout()
//	    .logoutUrl("/auth/api/logout")
//	    .invalidateHttpSession(true).deleteCookies("JSESSIONID")
//	    // oauth 로그인
////	    .and()
////	    .oauth2Login()
////	    .loginPage("/login")
////	    .userInfoEndpoint()
////	    .userService(principalOauth2UserService);
//	    .and()
//	    .exceptionHandling()
//	     .accessDeniedPage("/"); // 팅길때 메인페이지로 이동
//	    return http.build();
//	}
	
    // CORS 관련 코드
    @Bean
    public CorsConfigurationSource corsConf() {
    	CorsConfiguration config = new CorsConfiguration();
    	
    	// 다른 url 사이트에서 쿠키, json 보내기 위해 허용해주는 설정
    	config.addAllowedOrigin("http://localhost:5173");
    	config.addAllowedHeader("*");
    	config.addAllowedMethod("*");
    	config.setAllowCredentials(true); // 이걸 추가해야 쿠키 담기 가능
    	
    	// 위의 설정으로 소스 만들어주기
    	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    	source.registerCorsConfiguration("/**", config);
    	return source;
    }
    
    
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		return http
		.httpBasic().disable()
		.csrf().disable()
		// 위에서 만든 cors 설정을 내보내기
		.cors().configurationSource(corsConf())
		.and()
		.authorizeRequests()
		.antMatchers("/login/api/CreateUserPro","/login/api/LoginPro","login/api/LogoutPro" ,"/login/oauth2/code/google").permitAll()
		.antMatchers("/api/**").authenticated()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.addFilterBefore(new JwtFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
		.build(); 
		
	}
}
