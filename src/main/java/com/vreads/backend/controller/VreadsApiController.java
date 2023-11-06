package com.vreads.backend.controller;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vreads.backend.handler.MemberHandler;
import com.vreads.backend.service.VreadsService;
import com.vreads.backend.vo.ResEntityDto;
import com.vreads.backend.vo.VreadsVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173") // CORS 허용을 위한 url 추가
@RestController
@RequiredArgsConstructor
@RequestMapping(produces = "application/json; charset=UTF-8")
public class VreadsApiController {
	
	@Autowired
	private MemberHandler memHandler;
	
	@Autowired
	private VreadsService vdService;
	
	@PostMapping("/api/vread/test")
	public ResponseEntity<ResEntityDto> test(Principal principal,@RequestBody Map<String,String> map) {
		//ResponseEntity 를 위한 헤더 설정
		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
		
		// ResponseEntity 로 값을 내보내기위한 전용 Dto
		ResEntityDto resDto = new ResEntityDto();
		List<VreadsVO> vreads = null;
		Map<String,String> jo = new HashMap<String,String>();		
		Map<String, String> authMap = new HashMap<String, String>();
		
		if(principal == null) {
			jo.put("state", "false");
			jo.put("error", "인증 정보가 없습니다!");
			jo.put("result", "");
			resDto.state = "false";
			resDto.data = jo;
			resDto.msg = "";
			resDto.error = "인증 정보가 없습니다!";
			return new ResponseEntity<>(resDto,header,HttpStatus.SC_FORBIDDEN);	
		}
		
		authMap = memHandler.splitPrincipal(principal.getName());
		
		String userId = authMap.get("userId");
		String newToken = authMap.get("newToken");
		
		// vread 가져오기
		
		vreads = vdService.getAllVreads();
		
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		listMap.add(map);
		listMap.add(map);
		listMap.add(map);
		listMap.add(map);
		
		String mapStr = "";
		for(Map<String,String> m : listMap) {
			mapStr += m.toString();
		}
		
		jo.put("state", "true");
		jo.put("error", "");
		jo.put("result", "테스트 아이디 " + userId);
		jo.put("data",mapStr );
		resDto.state = "true";
		resDto.data = vreads;
		resDto.msg = "테스트";
		resDto.error = "";
		return new ResponseEntity<>(resDto,header,HttpStatus.SC_OK);	
	}
}
