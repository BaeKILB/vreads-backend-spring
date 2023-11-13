package com.vreads.backend.controller;

import java.security.Principal;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vreads.backend.config.PrincipalDetails;
import com.vreads.backend.handler.MemValidation;
import com.vreads.backend.handler.MemberHandler;
import com.vreads.backend.service.MemberService;
import com.vreads.backend.service.UserInfoService;
import com.vreads.backend.utils.JwtUtil;
import com.vreads.backend.vo.MemberVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173, https://vreads-app.web.app/") // CORS 허용을 위한 url 추가
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=UTF-8")
public class AuthApiController {

	private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);
	
    // 유효시간 지정
    private Long expiredMs = 60L * 60 * 1000;
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberHandler memHandler;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/api/auth/checkToken")
	public String checkToken(Principal principal) {
		JSONObject jo = new JSONObject();
		
		Map<String,String> getName = memHandler.splitPrincipal(principal.getName());
		
		String newToken = getName.get("newToken");
		String userId = getName.get("userId");
		
		jo.put("state", "true");
		jo.put("error", "");
		jo.put("newToken", newToken);
		jo.put("userId", userId);
		return jo.toString();
	}
	
	@GetMapping("/api/auth/testToken")
	public String testToken(Principal principal) {
		JSONObject jo = new JSONObject();
		
		Map<String,String> getName = memHandler.splitPrincipal(principal.getName());
		
		String newToken = getName.get("newToken");
		String userId = getName.get("userId");
		
		MemberVO member = new MemberVO();
		member.setMem_id(userId);
		member.setMem_email(userId);
		Long refreshExpiredMs = 24L* 60 * 60 * 1000;
		String mkToken = userInfoService.makeJwt(member,refreshExpiredMs);
		System.out.println(mkToken);
		jo.put("state", "true");
		jo.put("error", "");
		jo.put("newToken", mkToken);
		jo.put("userId", userId);
		return jo.toString();
	}
	
	// 구글 로그인 콜백
    @GetMapping("/login/oauth2/code/{registrationId}")
    public String googleLogin(@RequestParam String code
    		, @PathVariable String registrationId
//    		, HttpSession httpSession
    		,HttpServletResponse response
    		) {
    	
    	//토큰을 쿠키로 보내기
//    	Cookie cookie =null;
    	
		JSONObject jo = new JSONObject();
		
		System.out.println("test 0");
    	
    	// 코드하고 registrationId : 로그인 api 제공사 받아오기
    	try {
    		
    		MemberVO member = userInfoService.socialLogin(code, registrationId);
    		System.out.println("test 0");
    		// 로그인 처리
//    		PrincipalDetails user = new PrincipalDetails(member);
//    		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//    		SecurityContextHolder.getContext().setAuthentication(authentication);
//    		httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    			
    		//jwt 방식
    		String resultJwt = userInfoService.makeJwt(member,expiredMs);
    		jo.put("state", "true");
    		jo.put("error", "");
    		jo.put("token", resultJwt);
    		jo.put("userPhoto", member.getMem_profileImageUrl());
    		jo.put("uid", member.getMem_idx());
    		
    		// 쿠키로 refresh 토큰 만들기
    		Long refreshExpiredMs = 24L* 60 * 60 * 1000;
    		//jwt 만들기
    		String refreshToken = userInfoService.makeJwt(member,refreshExpiredMs);
    		
    		// 쿠키보내기
    		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
    			    .maxAge(24 * 60 * 60)
    			    .path("/")
//    			    .domain(vreadsDomain)
    			    .secure(true) // https 환경에서만 동작하는지 체크
    			    .sameSite("None") // 다른사이트에서도 쿠키 전송 가능한지 여부
    			    .httpOnly(true) // 브라우저에서 직접 쿠키 접근 못하게 막기
    			    .build();
    		response.setHeader("Set-Cookie", cookie.toString()); 
    		
    	}
    	catch(Exception e) {
    		logger.error("something error : googleLogin");
    		logger.error(e.getMessage());

        	jo.put("state", "false");
    		jo.put("error", "something error : googleLogin");
    		jo.put("token", "");
    	}
		return jo.toString();
    }
	
//	일반 회원가입
	@ResponseBody
	@PostMapping("/login/api/CreateUserPro")
//	@RequestMapping(
//			value = "/login/api/CreateUserPro",
//			method = RequestMethod.POST,
//			produces = "application/json; charset=UTF-8"
//			)	
	public String signup(
			HttpServletRequest request
			,@RequestBody Map<String,String> map
//			, HttpSession httpSession
    		,HttpServletResponse response) {

		MemberVO checkAlreadyLogin = null;

		
		JSONObject joResult = new JSONObject();
		MemberVO newMem = null;
		String errorTemp = "회원가입 진행중 오류가 일어났습니다";
		boolean isError = false;
		
//		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			PrincipalDetails mPrincipalDetails = (PrincipalDetails) auth.getPrincipal();
//			System.out.println("pass on");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.out.println("error on ");
//		}
		
		// 만약 로그인 되어 있으면 막기
		// 쿠키의 리프레시토큰 으로 체크...
		
		// 쿠키 안의 토큰 받아올 변수
		String cookieToken = "";
		
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {			
			for (Cookie cookie : cookies) {
				if (cookie.getName() == "refreshToken") {
					cookieToken = cookie.getValue();
					break;
				}
			}
			
			if(cookieToken != null && cookieToken != "") {
				
				joResult.put("state", "false");
				joResult.put("error", "문제가 발생했습니다! : 이미 로그인 중 입니다! 로그아웃을 하시거나 브라우저를 종료후 다시 실행해주세요!");
				return joResult.toString();
			}
		}
		
		
		try {
			newMem = new MemberVO();
			newMem.setMem_id(map.get("email"));
			newMem.setMem_passwd(map.get("passwd"));
			newMem.setMem_email(map.get("email"));
			newMem.setMem_name(map.get("name"));
			newMem.setMem_nickname(map.get("name"));
			newMem.setMem_status("1"); // 1 은 일반 로그인
		}
		catch(Exception e) {
			logger.error("error CreateUserPro");
			logger.error(e.getMessage());
			joResult.put("state", "false");
			joResult.put("error", "회원 정보 등록중 문제가 발생했습니다");
			return joResult.toString();
		}
		
		
		// ============= 유효성 체크
		
		MemValidation memv = memHandler.checkRegistValidation(newMem);
		
	    if (memv != MemValidation.OK) {
//	    	throw new CustomValidationException("이미 사용 중인 아이디입니다.", null);
	    	errorTemp = "문제가 발생했습니다! : " + memv.label();
	    	isError=true;
	    }
	    // 아이디 중복 유효성 검사
		if (memberService.isMemberIdDuplicated(newMem.getMem_id())) {
			errorTemp = "문제가 발생했습니다! : 이미 사용중인 아이디 입니다";
	    	isError=true;
	    }

		// 생년월일 유효성 검사
//		Date mem_birthday = newMem.getMem_birthday();
//		if (mem_birthday == null) {
//			throw new CustomValidationException("생년월일을 입력해주세요.", null);
//		}
		
	    // 휴대폰 번호 유효성 검사
//		if (memberService.isMemberPhoneDuplicated(newMem.getMem_mtel())) {
//			throw new CustomValidationException("이미 사용 중인 번호입니다.", null);
//		}  
			
		// 이메일 유효성 검사
//		if (memberService.isMemberEmailDuplicated(newMem.getMem_email())) {
//			throw new CustomValidationException("이미 사용 중인 이메일입니다.", null);
//		}
		
	    // 회원가입 유효성 검사 - 유효성 검사 에러 난 애들 한 곳에 모아서(bindingResult에 의해) 처리 errorMap에 담긴 메세지는 @Vaildation 에 의해서 자동으로 적절한게 간다.
		if (isError) {
			logger.error("CreateUserPro : " + errorTemp);
			joResult.put("state", "false");
			joResult.put("error", errorTemp);
	    } else {
	    	try {	    		
	    		
	    		memberService.registMember(newMem);
	    		MemberVO memberEntity = memberService.selectMember(newMem.getMem_id()); // 신규 회원 가입 후 mem_idx를 포함한 회원 정보를 조회
	    		
	    		//jwt 방식
	    		String resultJwt = userInfoService.makeJwt(memberEntity,expiredMs);
	    		System.out.println(resultJwt);
	    		joResult.put("state", "true");
	    		joResult.put("error", "");
	    		joResult.put("token", resultJwt);
	    		joResult.put("userPhoto", memberEntity.getMem_profileImageUrl());
	    		joResult.put("uid", memberEntity.getMem_idx());
	    		
	    		// 쿠키로 refresh 토큰 만들기
	    		expiredMs = 24L* 60 * 60 * 1000;
	    		//jwt 만들기
	    		String refreshToken = userInfoService.makeJwt(newMem,expiredMs);
	    		System.out.println(refreshToken);
	    		
	    		// 쿠키보내기
	    		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
	    			    .maxAge(24 * 60 * 60)
	    			    .path("/")
//	    			    .domain(vreadsDomain)
	    			    .secure(true) // https 환경에서만 동작하는지 체크
	    			    .sameSite("None") // 다른사이트에서도 쿠키 전송 가능한지 여부
	    			    .httpOnly(true) // 브라우저에서 직접 쿠키 접근 못하게 막기
	    			    .build();
	    		response.setHeader("Set-Cookie", cookie.toString()); 
	    		
	    		
	    	}		
	    	catch(Exception e) {
				logger.error("error CreateUserPro");
				logger.error(e.getMessage());
				joResult.put("state", "false");
				joResult.put("error", errorTemp);
			}
	    }
		
		return joResult.toString();

	}
	
	
//	일반 로그인
	@ResponseBody
	@RequestMapping("/login/api/LoginPro")	
	public String loginPro(			HttpServletRequest request
			,@RequestBody Map<String,String> map
//			, HttpSession httpSession
    		,HttpServletResponse response) {
		System.out.println(map);
		
		
		JSONObject joResult = new JSONObject();
		MemberVO newMem = null;
		String errorTemp = "로그인 진행중 오류가 일어났습니다";
		boolean isError = false;
		
		
		// 만약 로그인 되어 있으면 막기
		// 쿠키의 리프레시토큰 으로 체크...
		
		// 쿠키 안의 토큰 받아올 변수
		String cookieToken = "";
		
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null) {				
			for (Cookie cookie : cookies) {
				if (cookie.getName() == "refreshToken") {
					cookieToken = cookie.getValue();
					break;
				}
			}
			
			if(cookieToken != null && cookieToken != "") {
				
				joResult.put("state", "false");
				joResult.put("error", "문제가 발생했습니다! : 이미 로그인 중 입니다! 로그아웃을 하시거나 브라우저를 종료후 다시 실행해주세요!");
				return joResult.toString();
			}
		}
		
		// 현재 로그인 할때 email 을 아이디로 사용하고 있음
		try {
			newMem = new MemberVO();
			newMem.setMem_id(map.get("email"));
			newMem.setMem_passwd(map.get("passwd"));
			newMem.setMem_email(map.get("email"));
			newMem.setMem_status("1"); // 1 은 일반 로그인
		}
		catch(Exception e) {
			logger.error("error LoginPro");
			logger.error(e.getMessage());
			joResult.put("state", "false");
			joResult.put("error", "로그인 진행중 문제가 발생했습니다");
			return joResult.toString();
		}
		
		
		// ============= 유효성 체크
		
	    // 아이디 중복 유효성 검사
		if (!memberService.isMemberIdDuplicated(newMem.getMem_id())) {
			errorTemp = "문제가 발생했습니다! : 현재 존재하지 않는 아이디 입니다";
	    	isError=true;
	    }
		MemberVO memberEntity = memberService.selectMember(newMem.getMem_id()); // 신규 회원 가입 후 mem_idx를 포함한 회원 정보를 조회  
		// 비밀번호 체크

		if(!bCryptPasswordEncoder.matches(map.get("passwd"), memberEntity.getMem_passwd())) {
			errorTemp = "문제가 발생했습니다! : 비밀번호가 맞지 않습니다";
	    	isError=true;
		}
	    // 로그인 유효성 검사 
		if (isError) {
			logger.error("LoginPro : " + errorTemp);
			joResult.put("state", "false");
			joResult.put("error", errorTemp);
	    } else {
	    	try {	    		
	    	
	    		//jwt 방식
	    		String resultJwt = userInfoService.makeJwt(memberEntity,expiredMs);
	    		System.out.println(resultJwt);
	    		joResult.put("state", "true");
	    		joResult.put("error", "");
	    		joResult.put("token", resultJwt);
	    		joResult.put("userPhoto", memberEntity.getMem_profileImageUrl());
	    		joResult.put("uid", memberEntity.getMem_idx());
	    		
	    		// 쿠키로 refresh 토큰 만들기
	    		expiredMs = 24L* 60 * 60 * 1000;
	    		//jwt 만들기
	    		String refreshToken = userInfoService.makeJwt(newMem,expiredMs);
	    		System.out.println(refreshToken);
	    		
	    		// 쿠키보내기
	    		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
	    			    .maxAge(24 * 60 * 60)
	    			    .path("/")
//	    			    .domain(vreadsDomain)
	    			    .secure(true) // https 환경에서만 동작하는지 체크
	    			    .sameSite("None") // 다른사이트에서도 쿠키 전송 가능한지 여부
	    			    .httpOnly(true) // 브라우저에서 직접 쿠키 접근 못하게 막기
	    			    .build();
	    		response.setHeader("Set-Cookie", cookie.toString()); 
	    		
	    		
	    	}		
	    	catch(Exception e) {
				logger.error("error LoginPro");
				logger.error(e.getMessage());
				joResult.put("state", "false");
				joResult.put("error", errorTemp);
			}
	    }
		
		return joResult.toString();

	}

	
	// 로그아웃
	@ResponseBody
	@RequestMapping("/login/api/LogoutPro")	
	public String logoutPro(HttpServletResponse response) {
		JSONObject jo = new JSONObject();
		
		try {
    		// 로그아웃을 위한 빈 쿠키보내기
    		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
    			    .maxAge(0)
    			    .path("/")
//    			    .domain(vreadsDomain)
    			    .secure(true) // https 환경에서만 동작하는지 체크
    			    .sameSite("None") // 다른사이트에서도 쿠키 전송 가능한지 여부
    			    .httpOnly(true) // 브라우저에서 직접 쿠키 접근 못하게 막기
    			    .build();
    		response.setHeader("Set-Cookie", cookie.toString()); 
    		
    		jo.put("state","true");
    		jo.put("error","");
		}
		catch(Exception e) {
			jo.put("state","false");
			jo.put("error","로그아웃 중 문제가 발생하였습니다!");
		}
		return jo.toString();
	}
		
}
