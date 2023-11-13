package com.vreads.backend.controller;

import java.nio.charset.Charset;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vreads.backend.handler.MemberHandler;
import com.vreads.backend.service.MemberService;
import com.vreads.backend.service.VreadsService;
import com.vreads.backend.vo.MemberVO;
import com.vreads.backend.vo.ResEntityDto;
import com.vreads.backend.vo.VreadsVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173, https://vreads-app.web.app/") // CORS 허용을 위한 url 추가
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=UTF-8")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MemberService memService;

	@Autowired
	private VreadsService vdService;

	@Autowired
	private MemberHandler memHandler;

	@PostMapping(value = "/api/userInfo", produces = "application/json; charset=UTF-8")
	public ResponseEntity<ResEntityDto> getUserInfo(Principal principal, @RequestBody Map<String, String> map) {
		Map<String, String> authMap = new HashMap<String, String>();

		// ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();

		if (principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		// 토큰에서 받아온 정보
		authMap = memHandler.splitPrincipal(principal.getName());

		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");

		// 위에서 가져온 아이디로 MemberVO 가져오기
		MemberVO authMember = memService.findMember(userId);
		String uidStr = map.get("uidStr");
		int uid = -1;
		if (uidStr == null || uidStr.equals("")) {
			uid = authMember.getMem_idx();
		} else {
			try {
				uid = Integer.parseInt(uidStr);
			} catch (Exception e) {
				logger.error(e.getMessage());
				resDto.state = "false";
				resDto.error = "잘못된 회원 번호입니다!";
				return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
			}
		}
		MemberVO member = memService.getProfileMember(uid);

		MemberVO resultMem = null;

		resultMem = new MemberVO();
	
		resultMem.setMem_idx(member.getMem_idx());
		resultMem.setMem_id(member.getMem_id());
		resultMem.setMem_profileImageUrl(member.getMem_profileImageUrl());
		resultMem.setMem_bio(member.getMem_bio());
		resultMem.setMem_name(member.getMem_name());
		resultMem.setMem_nickname(member.getMem_nickname());

		resDto.state = "true";
		resDto.data = resultMem;
		return new ResponseEntity<>(resDto, header, HttpStatus.SC_OK);
	}

	// 이미지까지 받아야함으로 form-data 방식으로 받기
	
	// param의 uid 는
	// localstorage
	// 안의 내용과 토큰의
	// 계정가 일치하는지 확인용
	@PostMapping("/api/userInfo/profileUpdate")
	public ResponseEntity<ResEntityDto> updateUserInfo(Principal principal, @RequestParam String uidStr 
			, @RequestParam String mem_nickname, @RequestParam String mem_bio, @RequestParam(value = "file" , required = false) MultipartFile file) {
		Map<String, String> authMap = new HashMap<String, String>();

		// ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();

		if (principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		// 토큰에서 받아온 정보
		authMap = memHandler.splitPrincipal(principal.getName());

		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");

		// 위에서 가져온 아이디로 MemberVO 불러오기
		MemberVO authMember = memService.findMember(userId);

		// localstorage 에서 가져온 uid 와 토큰의 uid 가 같은지 체크
		int uid = 1;
		try {
			uid = Integer.parseInt(uidStr);
		} catch (Exception e) {
			logger.error(e.getMessage());
			resDto.state = "false";
			resDto.error = "인증 중 오류가 발생했습니다";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		if (authMember.getMem_idx() != uid) {
			logger.error("인증 정보가 일치하지 않습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 일치하지 않습니다!";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		if (mem_nickname != null && !mem_nickname.equals(""))
			authMember.setMem_nickname(mem_nickname);
		if (mem_bio != null && !mem_bio.equals(""))
			authMember.setMem_bio(mem_bio);

		// 이미지 있으면 이미지 넣은 뒤 프로필 업데이트에
		if (file != null) {
			authMember.setFile(file);
			try {
				memService.profileUpload(authMember, true);
			} catch (Exception e) {
				logger.error(e.getMessage());
				resDto.state = "false";
				resDto.error = "프로필 이미지 수정이 실패하였습니다!";
				return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
			}
		} else {
			memService.profileUpload(authMember, false);
		}
		// 유저 정보 다시 불러오기
		authMember = memService.findMember(userId);
		MemberVO resultMem = new MemberVO();
		resultMem.setMem_idx(authMember.getMem_idx());
		resultMem.setMem_id(authMember.getMem_id());
		resultMem.setMem_profileImageUrl(authMember.getMem_profileImageUrl());
		resultMem.setMem_bio(authMember.getMem_bio());
		resultMem.setMem_name(authMember.getMem_name());
		resultMem.setMem_nickname(authMember.getMem_nickname());

		resDto.state = "true";
		resDto.data = resultMem;
		resDto.msg = "owner";
		return new ResponseEntity<>(resDto, header, HttpStatus.SC_OK);
	}

	// 유저 리스트 검색
	@PostMapping("/api/userInfo/getUserList")
	public ResponseEntity<ResEntityDto> getUserList(Principal principal, @RequestBody Map<String, String> map) {
		// ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		Map<String, String> authMap = new HashMap<String, String>();

		if (principal == null) {
			logger.error("인증 정보가 없습니다!");
			resDto.state = "false";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		authMap = memHandler.splitPrincipal(principal.getName());

		String authUserId = authMap.get("userId");
		String newToken = authMap.get("newToken");

		// db 검색시 이용할 값 셋팅
		String keyword = map.get("keyword");
		String searchDateStr = map.get("searchDate");
		
		int startCount = vdService.START_COUNT_INIT;
		int setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		
		Timestamp searchDate = null;
		try {
			startCount = Integer.parseInt(map.get("startCount"));
			setPageListLimit = Integer.parseInt(map.get("setPageListLimit"));
			Long dateTime = Long.parseLong(searchDateStr);
			searchDate =  new Timestamp(dateTime);

		} catch (Exception e) {
			logger.error(e.getMessage());
			searchDate =  new Timestamp(System.currentTimeMillis());
			startCount = vdService.START_COUNT_INIT;
			setPageListLimit = vdService.SET_PAGE_LIST_LIMIT_INIT;
		}

		// vread 가져오기
		List<MemberVO> memberList = new ArrayList<MemberVO>();


		try {
			memberList = memService.getUserSearch(keyword, searchDate, startCount, setPageListLimit);
			
		} catch (Exception e) {
			logger.error("유저 검색중 문제가 발생했습니다!");
			resDto.state = "false";
			resDto.error = "유저 검색중 문제가 발생했습니다!";
			return new ResponseEntity<>(resDto, header, HttpStatus.SC_FORBIDDEN);
		}

		resDto.state = "true";
		resDto.data = memberList;
		return new ResponseEntity<>(resDto, header, HttpStatus.SC_OK);
	}

}
