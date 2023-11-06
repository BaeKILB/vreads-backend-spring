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

// 주의 !!
// 현재 vreads 에서는 mem_id 를 email로 입력하도록 제한해서 사용중
// 이점 유의

/*
 * CREATE TABLE
	mem_info(
	mem_idx int AUTO_INCREMENT COMMENT '회원 번호'
	, mem_id varchar(100) NOT NULL UNIQUE COMMENT '회원 아이디'
	, mem_name varchar(30) COMMENT '회원 이름'
	, mem_nickname varchar(30) UNIQUE COMMENT '회원 닉네임'
	, mem_passwd varchar(100) NOT NULL COMMENT '회원 비밀번호'
	, mem_address varchar(4000) COMMENT '주소'
	, mem_birthday date  COMMENT '생년월일'
	, mem_interest varchar(45) COMMENT '관심사'
	, mem_email varchar(45) NOT NULL UNIQUE COMMENT '이메일'
	, mem_mtel varchar(20) UNIQUE COMMENT '휴대폰 번호'
	, role varchar(30) COMMENT '멤버 권한'
	, mem_bio varchar(200) COMMENT '회원 프로필 자기소개'
	, mem_account_auth varchar(1)  COMMENT '계좌인증여부'
	, mem_status varchar(1)  COMMENT '회원 상태'
	, mem_rank varchar(45) COMMENT '회원 등급'
	, mem_profileImageUrl varchar(200)  COMMENT '회원 프로필 사진경로' 
	, mem_sign_date datetime COMMENT '회원 가입일'
	, PRIMARY KEY(mem_idx)
	) COMMENT '회원 테이블';

 * */

@Data
public class MemberVO {
	
	// 회원 항목 추가
	private int mem_idx;
	
//	@ValidNonConflictId // 아이디 중복 검사
//    @Size(min = 2, max = 20, message = "아이디는 2자 이상 20자 이내로 입력해주세요.")
    @NotBlank(message = "아이디를 입력해 주세요")
	private String mem_id;
    
//	@NotBlank
    @Size(min = 2, max = 20, message = "이름은 2자 이상 10자 이내로 입력해주세요")
	private String mem_name;
    
    @Size(min = 2, max = 20, message = "별명은 2자 이상 10자 이내로 입력해주세요")
    private String mem_nickname;
    
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$" ,message = "비밀번호는 영문, 숫자, 특수문자 포함 8~20글자 이상 입력해주세요.")
    @NotBlank(message = "비밀번호를 입력해주세요.")
	private String mem_passwd;
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,20}$" ,message = "비밀번호는 영문, 숫자, 특수문자 포함 8~20글자 이상 입력해주세요.")
    @ValidNewPasswd
    private String newPasswd1;
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
//	@NotBlank
	@Pattern(regexp = "^01[016789]-\\d{3,4}-\\d{3,4}$", message = "유효한 핸드폰 번호를 입력 해 주세요.")
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
