package com.vreads.backend.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*") // CORS 허용을 위한 url 추가
@RestController
@RequestMapping("/api/vread")
@RequiredArgsConstructor
public class VreadsApiController {
	
	@PostMapping("/test")
	//Authentication : 앞에 doFilterInternal 에서 jwt 로 바꿔서 
	public String test(@RequestBody Map<String, String> map, Authentication auth) {
		System.out.println(map);
		System.out.println(auth);
		JSONObject jo = new JSONObject();
		jo.put("state", "true");
		jo.put("result", auth.toString());
		
		return jo.toString();
	}
}
