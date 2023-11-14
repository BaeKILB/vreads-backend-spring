package com.vreads.backend.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vreads.backend.mapper.VreadsMapper;
import com.vreads.backend.vo.MemberVO;
import com.vreads.backend.vo.VreadsVO;

@Service
public class VreadsService {
	
	@Autowired
	private VreadsMapper vdMapper;
	
	@Autowired
	private MemberService memService;
	
	// 한번 요청시 가져올 limit 값
	public final int START_COUNT_INIT = 0;
	public final int SET_PAGE_LIST_LIMIT_INIT = 15;
	
	public boolean addVread(VreadsVO vread) {
		int result = vdMapper.insertVread(vread);
		if(result > 0)
			return true;
		return false;
	}
	
	public boolean updateVreadService(VreadsVO vread) {
		int result = vdMapper.updateVread(vread);
		if(result > 0)
			return true;
		return false;
	}
	
	public boolean removeVread(Long vreads_idx) {
		int result = vdMapper.deleteVread(vreads_idx);
		if(result > 0)
			return true;
		return false;
	}
	
	public List<VreadsVO> getAllVreads(
			int startCount, 
			int setPageListLimit){
		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		return vdMapper.selectAllVreads(timeNow, startCount, setPageListLimit);
	}
	
	//특정 유저 Vreads 불러오기
	/*
	 * <!-- serchType -->
	<!-- 0 = 유저uid 로만 검색 -->
	<!-- 1 = 제목검색 -->
	<!-- 2 = 제목과 타이틀 검색 -->
	<!-- 3 = 서브태그 검색 -->
	 * */
	public List<VreadsVO> getUserVreads(
			int uid,
			String keyword, 
			int searchType,
			int startCount, 
			int setPageListLimit){
		String fixKeyword = keyword;
		if(searchType != 3) {
			fixKeyword = "%" + keyword + "%";
		}
		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		return vdMapper.selectUserVreads(uid,fixKeyword,searchType, timeNow, startCount, setPageListLimit);
	}

	//search type 과 keyword 에 맞춰서 Vreads 불러오기
	/*
	 * 	<!-- serchType -->
	<!-- 1 = 제목검색 -->
	<!-- 2 = 제목과 타이틀 검색 -->
	<!-- 3 = 서브태그 검색 -->
	 * */
	public List<VreadsVO> getVreadsSearch(
			String keyword, 
			int searchType,
			int startCount, 
			int setPageListLimit){
		String fixKeyword = keyword;
		if(searchType != 3) {
			fixKeyword = "%" + keyword + "%";
		}
		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		return vdMapper.selectSearchVreads(fixKeyword,searchType, timeNow, startCount, setPageListLimit);
	}
	
	// 특정 idx vread 불러오기
	public VreadsVO getVreadIdx(Long vreadIdx) {
		return vdMapper.selectVreadDetail(vreadIdx);
	}
	
	//search type 과 keyword 에 맞춰서 Subtag 이름과 해당 subtag 의 갯수 불러오기
	/*
	 * 유의사항
	 * 
	 * 0 번의 경우 searchDate 보다 최근의 정보를 불러옴
	 * 
	 * 0 번 사용시 userIdx 널 스트링으로
	 * 
	 * 	<!-- serchType -->
	<!-- 0 = 전체 subtag 인기순 정렬 검색-->
	<!-- 1 = userIdx 로 특정 유저의 vread subtag 검색-->
	 * */
	public List<Map<String,String>> getSubtagList(
			int uid,
			int searchType,
			int startCount, 
			int setPageListLimit
			){
		// 0번에 사용될 날짜값 계산
		// 30일 차이로 고정
		Timestamp timeNow = new Timestamp(System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L));
		return vdMapper.selectSubtagList(uid, searchType, timeNow, startCount, setPageListLimit);
	}
}
