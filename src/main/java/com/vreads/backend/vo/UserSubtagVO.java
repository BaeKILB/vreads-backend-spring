package com.vreads.backend.vo;

import java.sql.Date;

import lombok.Data;

/*
 
create table user_subtag (
	ustag_idx int primary key auto_increment,
	mem_idx int not null,
	ustag_name varchar(40) not null,
	ustag_vreads_count int default 1,
	ustag_update_date datetime default now(),
	FOREIGN KEY (mem_idx) REFERENCES mem_info(mem_idx) on  DELETE cascade
);
 * */

@Data
public class UserSubtagVO {
	private int ustag_idx;
	private int mem_idx;
	private String ustag_name;
	private int ustag_vreads_count;
	private Date ustag_update_date;
}
