package com.vreads.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.vreads.backend.config.PrincipalDetails;
import com.vreads.backend.mapper.MemberMapper;
import com.vreads.backend.vo.MemberVO;

@Service
public class PrincipalOauth2Service extends DefaultOAuth2UserService {
	
	private MemberVO member;
	
	@Autowired
	private MemberMapper mapper;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// TODO Auto-generated method stub
		OAuth2User oAuth2User = super.loadUser(userRequest);
		
		// 서비스 가져오기

		String provider = userRequest.getClientRegistration().getRegistrationId();
		String providerId = oAuth2User.getAttribute("sub");
        String role = "ROLE_USER";
		System.out.println(oAuth2User);
		MemberVO member = mapper.findMemberById("test");
		if(member == null) {
			member.setMem_id("test");
			member.setRole(role);
		}
		else {
			 System.out.println(provider +" 로그인을 이미 한 적이 있습니다.");
		}
		return new PrincipalDetails(member,oAuth2User.getAttributes());
	}
	
}
