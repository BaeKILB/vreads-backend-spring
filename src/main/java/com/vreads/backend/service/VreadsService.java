package com.vreads.backend.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vreads.backend.mapper.VreadsMapper;
import com.vreads.backend.vo.VreadsVO;

@Service
public class VreadsService {
	
	@Autowired
	private VreadsMapper vdMapper;
	
	// 한번 요청시 가져올 limit 값
	int startCountInit = 0;
	int setPageListLimitInit = 15;
	
	public List<VreadsVO> getAllVreads(){
		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		return vdMapper.selectAllVreads(timeNow, startCountInit, setPageListLimitInit);
	}
	
}
