package com.vreads.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vreads.backend.config.PrincipalDetails;
import com.vreads.backend.handler.CustomValidationException;
import com.vreads.backend.mapper.FollowMapper;
import com.vreads.backend.mapper.MemberMapper;
import com.vreads.backend.vo.MemberProfileDto;
import com.vreads.backend.vo.MemberUpdatePasswdVO;
import com.vreads.backend.vo.MemberUpdateVO;
import com.vreads.backend.vo.MemberVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
	
    @Autowired
    private MemberMapper memberMapper;
    
    @Autowired
    private FollowMapper followMapper;
    
    @Value("${userDir}")
    private String userDir;
    @Value("${backUrlFolder}")
    private String backUrlFolder;
    @Value("${backUrl}")
    private String backUrl;
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    // 카카오 로그인 회원가입
    @Transactional(readOnly = true)
	public MemberVO selectMember(String mem_id) {
		
    	MemberVO member = memberMapper.findMemberByMemId(mem_id);
//    	if(member == null) {
//    	  model.attruasd(msg) 처리해보자
//    	}
    	return member;
	}
    
    // 카카오 로그인 회원가입 존재시 리턴
	@Transactional
	public MemberVO updateAndReturnMemberWithKakao(MemberVO kakaoMember) {
	    memberMapper.updateMemberWithKakao(kakaoMember);
	    return memberMapper.findMemberById(kakaoMember.getMem_id());
	}

	// 회원 아이디로 찾기
	@Transactional
	public MemberVO findMember(String userId) {
		return memberMapper.findMemberById(userId);
	}
    
    
    // 회원가입
    @Transactional
    public MemberVO registMember(MemberVO member) {
        // 회원가입 진행
        String rawPassword = member.getMem_passwd();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        member.setMem_passwd(encPassword); // 암호화 비번
        member.setRole("ROLE_USER"); // 멤버 권한 디폴트
        member.setMem_account_auth("N"); // 계좌 디폴트
//        member.setMem_status("1"); // 멤버 상태 디폴트
        member.setMem_bio("반갑습니다."); // 멤버 상태 디폴트
        member.setMem_profileImageUrl(""); // 
//        System.out.println(member.getMem_mtel());
//        System.out.println(member.getMem_address());
//        System.out.println(member.getMem_birthday());
        System.out.println("member 값들(service) : "+ member);

        memberMapper.insertMember(member);
        
        return member;
    }
    // -----------------백엔드단 중복체크 -----------------------------
	// 아이디 중복 체크
    public boolean isMemberIdDuplicated(String mem_id) {
	    return memberMapper.isMemberIdDuplicated(mem_id) > 0;
	}
    // 폰 중복 체크
	public boolean isMemberPhoneDuplicated(String mem_mtel) {
		return memberMapper.isMemberPhoneDuplicated(mem_mtel) > 0;
	}
	public boolean isMemberEmailDuplicated(String mem_email) {
		return memberMapper.isMemberEmailDuplicated(mem_email) > 0;
	}

	// 프론트단 아이디 중복체크
	public int memIdCheck(Map<String, String> map) {
		return memberMapper.selectIdCheck(map);
	}
	
	// 회원 정보 수정
	@Transactional
//	public MemberVO updateMemberInfo(int mem_idx, MemberVO member) {
//		
//		MemberVO memberEntity = memberMapper.updateMember(mem_idx, member);
//		
//        String rawPassword = member.getMem_passwd();
//        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
//        
//        memberEntity.setMem_passwd(encPassword);
//        memberEntity.setMem_nickname(member.getMem_nickname());
//        memberEntity.setMem_birthday(member.getMem_birthday());
//        memberEntity.setMem_address(member.getMem_address());
//        
//		return memberEntity;
//	}
	
	// 회원 정보 수정
	public int ModifyMember(MemberUpdateVO member) {
		return memberMapper.updateMember(member);
	}
	
	// 회원 수정 새로운 정보 담기
	public MemberVO loadMemberData(String mem_id) {
		return memberMapper.selectMember(mem_id);
	}
	
	// 회원 수정 새로운 정보 담기
	public MemberUpdatePasswdVO loadMemberPassWdData(String mem_id) {
		return memberMapper.selectMemberPasswd(mem_id);
	}
	
	// 회원 비밀번호 정보 수정
	public int ModifyMemberPasswd(@Valid MemberUpdatePasswdVO member, String newPasswd, @RequestParam String newPasswd1) {
		return memberMapper.updateMemberPasswd(member,newPasswd, newPasswd1);
	}
	
	
	//핸드폰 중복확인
	public int phoneCheck(String phone) {
		int cnt = memberMapper.phoneCheck(phone);
		System.out.println("cnt: " + cnt);
		return cnt;
	}
	
	//아이디 찾기 - find.jsp
	public String getId(MemberVO member) {
		System.out.println("아디찾기");
		return memberMapper.getId(member);
	}
	
	// 일치하는 회원 레코드 있는지 확인(비밀번호 찾기 과정 1) - find.jsp
	public String isExistUser(MemberVO member) {
		return memberMapper.isExistUser(member);
	}
	
	// 비밀번호 찾기 
	public String selectEmail(MemberVO member) {
		return memberMapper.selectSendEmail(member);
	}
	
	// 비밀번호 찾기
	public int changePw(MemberVO member) {
		return memberMapper.changePw(member);
	}
	
	// --------------- 마이페이지 ---------------
	// 프로필 관리
	public MemberVO getProfileMember(int sId) {
		return memberMapper.selectProfileMember(sId);
	}
	
	// 프로필 사진 변경 및 정보 변경
	/*
	 * uid 나 id 로 member 정보 찾은 뒤 변경할 부분만 변경 해 주기
	 * 
	 * 이미지 변경 시 밖에서 MemberVO 객체의 getFile 에 이미지 파일 넣기
	 * */
	public boolean profileUpload(MemberVO member, boolean isPhotoUpdate) {
		
		
		String saveDir = "/" + backUrlFolder + userDir;
		String subDir = ""; // 서브디렉토리(날짜 구분)
		
		try {
	           Date date = new Date();
	           SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	           subDir = sdf.format(date);
	           saveDir +=  subDir;
	           Path path = Paths.get(saveDir);
	           Files.createDirectories(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		// MemberVO 에 만들어둔 이미지 객체 보내기
		MultipartFile mFile = member.getFile();
		String fileName = "";
		// 포토 업데이트 때만 동작
		if(isPhotoUpdate || mFile != null) {			
			// 파일명 중복방지 UUID
			String uuid = UUID.randomUUID().toString();
			// 이미지 명 초기화
			member.setMem_profileImageUrl("");
			// 위의 UUID 를 붙여 중복 방지
			fileName = uuid.substring(0, 8) + "_" + mFile.getOriginalFilename();
			
			// MemberVO 에 들어갈 경로 셋팅
			if(!mFile.getOriginalFilename().equals("")) {
				member.setMem_profileImageUrl(backUrl + saveDir + "/" + fileName);
			} 
			
			System.out.println("실제 업로드 파일명1 : " + member.getMem_profileImageUrl());	
		}

		int updateCount = memberMapper.updateProfile(member);
		
		//이미지 업로드
		if(updateCount > 0) { // 성공
			try {
				if(isPhotoUpdate && !mFile.getOriginalFilename().equals("")) {
					mFile.transferTo(new File(saveDir, fileName));
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else { // 실패
			throw new CustomValidationException("오류가 발생했습니다.", null);
		}
		
		return true;
		
	
	}
	
	// 유저 명 검색
	public List<MemberVO> getUserSearch(
			String keyword,
			Timestamp searchDate,
			int startCount, 
			int setPageListLimit){

		Timestamp timeNow = new Timestamp(System.currentTimeMillis());
		if(searchDate != null) {
			timeNow = searchDate;
		}
		String fixKeyword = "%" + keyword + "%";
		return memberMapper.selectSearchMember(fixKeyword, timeNow, startCount, setPageListLimit);
	}
	
	
// ------------------------소셜 프로필--------------------------------
//	// 프로필 사진 나오기 (로그인 한 사람이 아니라 각 회원 mem_idx 에 해당하는 프로필이 떠야함)
//	public void MemberProfile(int MemIdx) { // MemId = "/social/{mem_idx}" 여기의 {mem_idx} 요녀석 받아서 처리 할 거임
//		// SELECT * FROM social_posts WHRER MemId = :MemId; 이렇게 써보자
////		MemberVO memberEntity = memberMapper.findMemberByIdx(MemIdx);
//	}
	public MemberProfileDto memberProfile(int mem_idx, int sId) { // MemId = "/social/{mem_idx}" 여기의 {mem_idx} 요녀석 받아서 처리 할 거임
		MemberProfileDto dto = new MemberProfileDto();
		
		MemberVO memberEntity = memberMapper.findMemberByMemIdx(mem_idx);
		
		dto.setMember(memberEntity);
		dto.setPageOwnerState(mem_idx == sId);
		
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sId", sId);
        paramMap.put("mem_idx", mem_idx);
		System.out.println("현재 프로필 페이지 sId 는 뭐냐 ? " + sId);
		System.out.println("현재 프로필 페이지 mem_idx 는 뭐냐 ? " + mem_idx);
        int followState = followMapper.mfollowState(paramMap);
        int followCount = followMapper.mfollowCount(mem_idx);
		
		dto.setFollowState(followState == 1);
		dto.setFollowCount(followCount);
		System.out.println("해당 프로필페이지 유저와 팔로우 했니? : " + followState);
		System.out.println("팔로우 수 : " +dto.getFollowCount());
		
		return dto;
	}
	
//	스토리에 들고갈 프로필을 위한 셀렉 아이디
	public MemberVO getMemberByIdx(int mem_idx) {
        return memberMapper.findMemberByIdx(mem_idx);
	}
	
	// 회원탈퇴 : (상태 ROLE_REST) 로 업데이트
	public int deleteMember(int sId) {
		return memberMapper.deleteMember(sId);
	}


	





	
	// ------------------ 0809 배경인 추가 ---------------------
	// 계좌 인증 여부 확인 체크
	public String getAccountAuth(int mem_idx) {
		return (memberMapper.findMemberByIdx(mem_idx)).getMem_account_auth();
	
	}
	

	// 해당 idx 팔로우수만 가져오기
	public int countFollow(int mem_idx) {
		return followMapper.mfollowCount(mem_idx);
	}

	// 카카오 로그인 회원가입 있으면 업데이트
//	@Transactional
//	public void updateMemberWithKakao(MemberVO kakaoMember) {
//	    memberMapper.updateMemberWithKakao(kakaoMember);
//	}




	//아이디 중복체크 mapper 접근
//	public int idCheck(String id) {
//		int cnt = memberMapper.idCheck(id);
//		System.out.println("cnt: " + cnt);
//		return cnt;
//	}
}
