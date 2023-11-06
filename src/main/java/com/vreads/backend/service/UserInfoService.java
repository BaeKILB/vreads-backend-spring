package com.vreads.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.firebase.auth.FirebaseAuth;
import com.vreads.backend.controller.AuthApiController;
import com.vreads.backend.mapper.MemberMapper;
import com.vreads.backend.utils.JwtUtil;
import com.vreads.backend.vo.MemberVO;

@Service
public class UserInfoService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserInfoService.class);
	
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMapper memMapper;

	// env 의 일종인 application.yml 을 받아오는 객체
	// src/main/resources 에 넣음
//    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();
    
//    public UserInfoService(Environment env) {
//    	this.env = env;
//    }
    

    // ============ 구글 소셜 로그인에 사용
    @Value("${google_client-id}")
    private String clientId;
    @Value("${google_client-secret}")
    private String clientSecret;
    @Value("${google_redirect-uri}")
    private String redirectUri;
    @Value("${google_token-uri}")
    private String tokenUri;
    @Value("${google_resource-uri}")
    private String resourceUri;


    // jwt 키 받아오기
	@Value("${jwt_secret}")
	String jwtsecret;
    
    // jwt 키 보내기
    public String makeJwt(MemberVO member,Long expiredMs) {
    	return JwtUtil.createJwt(member,jwtsecret, expiredMs);
    }
    
    
    // ============ 구글 소셜 로그인시 동작
    public MemberVO socialLogin(String code, String registrationId) {

        String accessToken = getAccessToken(code, registrationId);
        JsonNode userResourceNode = getUserResource(accessToken, registrationId);

        UUID garbagePassword = UUID.randomUUID();
        
		MemberVO googleMember = new MemberVO();
		googleMember.setMem_id(userResourceNode.get("email").asText());
		googleMember.setMem_passwd(garbagePassword.toString());
		googleMember.setMem_name(userResourceNode.get("given_name").asText());
		googleMember.setMem_nickname(userResourceNode.get("name").asText());
		googleMember.setMem_email(userResourceNode.get("email").asText());
		googleMember.setMem_status("2"); // 2는 OAuth2 로그인 멤버 상태

        System.out.println(userResourceNode);
        
        // db 에서 아이디 검색
        MemberVO member = memMapper.findMemberById(googleMember.getMem_id());
        
        //===== 소셜 로그인 시 email 로 아이디 있는지 체크하여 이미 아이디가 있으면 로그인
        if(member == null) {
        	memberService.registMember(googleMember);
        	member = memberService.selectMember(googleMember.getMem_id()); // 신규 회원 가입 후 mem_idx를 포함한 회원 정보를 조회
        }
        // ==== 위에서 만든 뒤 혹은 이미 있는 아이디면 자동 회원 가입 후 로그인 처리
        return member;
        
    }

    private String getAccessToken(String authorizationCode, String registrationId) {
    	
    	// Environment 사용법
    	// application.yml 안의 키값을 검색해서그 안의 값을 반환해줌
    	// ex) "oauth2." + registrationId + ".client-id" 로 되어있을때
    	// 		registrationId 의 값이 google 이면 oauth2 안의 google 안의 client-id 키값의 값을 불러옴
    	//		없으면 null 반환
//        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
//        String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
//        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
//        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

    	// 일반적인 appdata.properties 사용
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params,headers);
	

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    private JsonNode getUserResource(String accessToken, String registrationId) {
//        String resourceUri = env.getProperty("oauth2."+registrationId+".resource-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }


	

}
