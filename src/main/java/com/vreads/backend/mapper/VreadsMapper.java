package com.vreads.backend.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.vreads.backend.vo.VreadsVO;

@Mapper
public interface VreadsMapper {
	
	// Vread 게시하기
	int insertVread(VreadsVO vread);
	
	
	//특정 유저 Vreads 불러오기
	/*
	 * <!-- serchType -->
	<!-- 1 = 제목검색 -->
	<!-- 2 = 제목과 타이틀 검색 -->
	<!-- 3 = 서브태그 검색 -->
	 * */
	List<VreadsVO> selectUserVreads(
			@Param("userIdx") int userIdx, 
			@Param("keyword") String keyword, 
			@Param("searchType") int searchType,
			@Param("searchDate") Timestamp searchDate,
			@Param("startCount") int startCount, 
			@Param("setPageListLimit") int setPageListLimit);

	//특정 Vreads 하나만 불러오기
	VreadsVO selectVreadDetail(@Param("vreadsIdx") Long vreadsIdx);

	//Vreads 조건 없이 불러오기 
	List<VreadsVO> selectAllVreads(
			@Param("searchDate") Timestamp searchDate,
			@Param("startCount") int startCount,
			@Param("setPageListLimit") int setPageListLimit);

	//search type 과 keyword 에 맞춰서 Vreads 불러오기
	/*
	 * 	<!-- serchType -->
	<!-- 1 = 제목검색 -->
	<!-- 2 = 제목과 타이틀 검색 -->
	<!-- 3 = 서브태그 검색 -->
	 * */
	List<VreadsVO> selectSearchVreads(
			@Param("keyword") String keyword, 
			@Param("searchType") int searchType,
			@Param("searchDate") Timestamp searchDate,
			@Param("startCount") int startCount, 
			@Param("setPageListLimit") int setPageListLimit);

}
