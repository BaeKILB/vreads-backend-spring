package com.vreads.backend.handler;

import org.springframework.stereotype.Component;

// 회원 유효성 체크 enum

public enum MemValidation {
	OK("OK")
	,MEM_NULL("Member is null")
	,MEM_ID("Member id is empty or the length of the string does not match")
	,MEM_NAME("Member name is empty or the length of the string does not match")
	,MEM_PASSWD("Member password is empty or the length of the string does not match")
	,MEM_EMAIL("Member email is empty or the length of the string does not match")
	,MEM_NICKNAME("Member nickname is empty or the length of the string does not match")
	;
	
	private final String label;
	MemValidation(String label) {
		 
        this.label = label;
    }

    public String label() {
        return label;
    }
}
