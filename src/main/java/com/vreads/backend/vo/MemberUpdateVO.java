package com.vreads.backend.vo;

import java.sql.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.vreads.backend.valid.ValidNewPasswd;
import com.vreads.backend.valid.ValidNonConflictId;

import lombok.Data;

@Data
public class MemberUpdateVO {
	
	// 회원 항목 추가
	private int mem_idx;
	
//    @Size(min = 2, max = 20, message = "아이디는 2자 이상 20자 이내로 입력해주세요.")
    @NotBlank(message = "아이디를 입력해 주세요")
	private String mem_id;
    
	private String mem_name;
    
    @Size(min = 2, max = 10, message = "별명은 2자 이상 10자 이내로 입력해주세요")
    private String mem_nickname;
//	-------------------------------------------------
//	@NotBlank(message = "주소를 입력해 주세요")
	private String mem_address;
	private String sample6_address;//하고
	private String sample6_detailAddress;
	private String sample6_extraAddress;
	private String sample6_postcode;//하고
//	-------------------------------------------------
	@Email(message = "유효한 이메일 주소를 입력해주세요.")
	@NotBlank
	private String mem_email;
//	-------------------------------------------------
	private Date mem_birthday;
	private	String mem_bir1;
	private	String mem_bir2;
	private	String mem_bir3;
//	-------------------------------------------------
	private String mem_interest;
//	-------------------------------------------------
	@Pattern(regexp = "^(01[016789]-\\d{3,4}-\\d{3,4}|\\s*)$", message = "유효한 핸드폰 번호를 입력 해 주세요.")
	private String mem_mtel;
//	@NotBlank
	@Size(max = 3) 
	private String phone1;//ㅇ
//	@NotBlank
	@Size(max = 4)
	private String phone2;//ㅇ
//	@NotBlank
	@Size(max = 4)
	private String phone3;//ㅇ
//	-------------------------------------------------
	private String mem_bio; // 회원 프로필 자기소개 
	private String role; // 시큐리티 권한
	private String mem_account_auth; // 계좌 인증 여부
	private String mem_status; // 회원 상태 ex) 1 활동 2 탈퇴 
	private String mem_rank; // 회원 등급


	private String mem_profileImageUrl; // 프로파일 사진
	private Date mem_sign_date; //now()
	
	private MultipartFile file;
	private String image_path; 	//파일 경로 저장
}
