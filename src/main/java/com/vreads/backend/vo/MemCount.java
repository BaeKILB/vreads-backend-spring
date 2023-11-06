package com.vreads.backend.vo;

import java.sql.Timestamp;

import lombok.Data;

/*
CREATE TABLE
mem_count(
	mc_idx int auto_increment
	, mem_idx int
	, mc_vreads int default 0
	, mc_follows int default 0
	, mc_followers int default 0
	, mc_subtag int default 0
	, mc_last_refresh datetime default now()
	, primary key (mc_idx)
	,FOREIGN KEY (mem_idx) REFERENCES mem_info(mem_idx) on  DELETE cascade
);

 * */


@Data
public class MemCount {

	private int	mc_idx ;
	private int	mem_idx ;
	private int	mc_vreads ;
	private int	mc_follows ;
	private int	mc_followers ;
	private int	mc_subtag ;
	private Timestamp mc_last_refresh;

}
