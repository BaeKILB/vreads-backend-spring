package com.vreads.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;



@CrossOrigin(origins = "http://localhost:5173, https://vreads-app.web.app/") // CORS 허용을 위한 url 추가
@RestController
public class VreadsFileApiController {
	
	@Value("${backUrl}")
	String backUrl;
	@Value("${uploadDir}")
	String uploadDir;
	@Value("${backUrlFolder}")
	String backUrlFolder;
	@Value("${localUploadDir}")
	String localUploadDir;
	
	// vreads 에 사용될 각종 파일들의 전송 처리를 담당
	@GetMapping("/resources/upload/vreads/{year}/{month}/{day}/{filename}")
	public ResponseEntity<byte[]> display(@PathVariable("year")String year,
						@PathVariable("month")String month,
						@PathVariable("day")String day,
						@PathVariable("filename")String filename
						) {
		
		//파일이 저장된 경로
		String savename = localUploadDir +"/"+ year+"/"+month+"/"+day+"/"+filename;
		File file = new File(savename);
		
		//저장된 이미지파일의 이진데이터 형식을 구함
		byte[] result=null;//1. data
		ResponseEntity<byte[]> entity=null;
		
		try {
	    	result = FileCopyUtils.copyToByteArray(file);
			
			//2. header
			HttpHeaders header = new HttpHeaders();
			header.add("Content-type",Files.probeContentType(file.toPath())); //파일의 컨텐츠타입을 직접 구해서 header에 저장
				
			//3. 응답본문
			entity = new ResponseEntity<>(result,header,HttpStatus.OK);//데이터, 헤더, 상태값
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return entity;
	}
}
