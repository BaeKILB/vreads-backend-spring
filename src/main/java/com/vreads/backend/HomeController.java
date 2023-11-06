package com.vreads.backend;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vreads.backend.config.PrincipalDetails;
import com.vreads.backend.vo.MemberVO;



/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		MemberVO member = null;
		// 로그인 되어있는지 확인하기
		try {			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			PrincipalDetails mPrincipalDetails = (PrincipalDetails) auth.getPrincipal();
			member = mPrincipalDetails.getMember();
		}
		catch(Exception e) {
			// 로그인 안되어있으면 로그인 화면으로 되돌려 보내기
			System.out.println("로그인 안됨 !!");
		}
		if(member == null) {
			System.out.println("로그인 안됨 !!");			
		}
		else {
			System.out.println("로그인 OK");			
			System.out.println(member);
		}
		
		return "";
	}
	
}
