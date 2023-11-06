package com.vreads.backend.mapper;

import java.util.Map;

import javax.validation.Valid;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.vreads.backend.vo.MemberUpdatePasswdVO;
import com.vreads.backend.vo.MemberUpdateVO;
import com.vreads.backend.vo.MemberVO;

@Mapper
public interface MemberMapper {
   
	// 카카오 로그인 회원가입 아이디 중복 찾기
	MemberVO findMemberByMemId(String mem_id);
	
	// 카카오 로그인 회원 존재시 카카오정보로 업데이트
	void updateMemberWithKakao(MemberVO kakaoMember);
	
	
	// 회원가입
	int insertMember(MemberVO member);
	
    // MemberControllor 백엔드단 아이디 중복 검사
	int isMemberIdDuplicated(String mem_id);
    // MemberControllor 백엔드단 폰 중복 검사
	int isMemberPhoneDuplicated(String mem_mtel);
	// MemberControllor 백엔드단 이메일 중복 검사
	int isMemberEmailDuplicated(String mem_email);
	// signup.js 프론트단 아이디 중복 검사
	int selectIdCheck(Map<String, String> map);
	
	
	// 시큐리티에 mem_id 셀렉해주기
    MemberVO findMemberById(String mem_id);
    
    // 회원 정보 수정
	int updateMember(MemberUpdateVO member);
	
	// 회원정보 수정 새로운 값 셀렉
	MemberVO selectMember(String mem_id);
	
	Object findMemberById(int mem_idx);
	
	// 회원 비밀번호 정보 수정 
	int updateMemberPasswd(@Param("member") @Valid MemberUpdatePasswdVO member, @Param("newPasswd") String newPasswd,
			@Param("newPasswd1") String newPasswd1);
	
	// 회원 비밀번호 정보 수정 새로운 값 셀렉
	MemberUpdatePasswdVO selectMemberPasswd(String mem_id);

	//폰번호 중복확인
	int phoneCheck(String phone);
//---------------------- 회원가입 / 회원정보수정--------------------
	//아이디찾기
	String getId(MemberVO member);
	
	//비번찾기
	String isExistUser(MemberVO member);
	
	String selectSendEmail(MemberVO member);
	
	
	//임시비밀번호 설정
	int changePw(MemberVO member);
	
// ------------------ 마이페이지 / 프로필 관리 -------------------
	Map<String, Object> selectProfileMember(int sId);
	
	// 마이페이지 프로필 변경
	int updateProfile(MemberVO member);
//---------------------- 아이디/비밀번호 찾기 - find.jsp--------------------

	// 소셜 프로필 가져올 memIdx
	MemberVO findMemberByMemIdx(int mem_idx);
	
	// 소셜 소토리 용
	MemberVO findMemberByIdx(int mem_idx);
	
	// 회원탈퇴 : (상태 ROLE_REST) 로 업데이트
	int deleteMember(@Param("sId") int sId);

	

	

	
	
}
