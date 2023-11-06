package com.vreads.backend.vo;

import java.sql.Date;

import lombok.Data;

/*
 * CREATE TABLE
mem_count(
	mc_idx int AUTO_INCREMENT 
	, mc_vreads int default 0
	, mc_follows int default 0
	, mc_followers int default 0
	, mc_subtag int default 0
	, mc_last_refresh datetime default now()
)
 * */

@Data
public class MemCountVO {
	private int mc_idx;
	private int mc_vreads;
	private int mc_follows;
	private int mc_followers;
	private int mc_subtag;
	private Date mc_last_refresh;
	
}
