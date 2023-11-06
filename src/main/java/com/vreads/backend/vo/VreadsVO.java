package com.vreads.backend.vo;



import java.sql.Date;

import lombok.Data;

/*
 create table vreads (
	vreads_idx  BIGINT primary key auto_increment,
	mem_idx int not null,
	vd_vtTitle VARCHAR(250) not null,
	vd_vtDetail VARCHAR(3000) ,
	vd_media_1 VARCHAR(200)  ,
	vd_media_2 VARCHAR(200)  ,
	vd_media_3 VARCHAR(200)  ,
	vd_subtag VARCHAR(40),
	vd_comment_idx BIGINT,
	vd_createDate datetime default now(),
	vd_modifyDate datetime ,
	FOREIGN KEY (mem_idx) REFERENCES mem_info(mem_idx) on  DELETE cascade
);
 * */

@Data
public class VreadsVO {

	private Long vreads_idx;
	private int mem_idx;
	private String vd_vtTitle;
	private String vd_vtDetail;
	private String vd_media_1;
	private String vd_media_2;
	private String vd_media_3;
	private String vd_subtag;
	private Long vd_comment_idx;
	private Date vd_createDate;
	private Date vd_modifyDate;
	
	// join 으로 가지고오는 추가 데이터 넣기
	private String mem_nickname;
	private String mem_profileImageUrl;
	
	
}
