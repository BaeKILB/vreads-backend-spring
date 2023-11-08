package com.vreads.backend.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vreads.backend.vo.MemberVO;

@Component
public class MemberHandler {

	public final int MEM_MIN_ID_LENGTH = 3;
	public final int MEM_MIN_PASSWD_LENGTH = 5;
	public final int MEM_MIN_NAME_LENGTH = 1;
	public final int MEM_MIN_NICKNAME_LENGTH = 2;
	public final int MEM_MIN_EMAIL_LENGTH = 6;

	public final int MEM_MAX_ID_LENGTH = 45;
	// 패스워드 길이가 솔팅되어 들어옴으로 60자 이상으로
	public final int MEM_MAX_PASSWD_LENGTH = 80;
	public final int MEM_MAX_NAME_LENGTH = 15;
	public final int MEM_MAX_NICKNAME_LENGTH = 15;
	public final int MEM_MAX_EMAIL_LENGTH = 45;

	// MemValidation 라고 따로 만든 enum 을 리턴
	public MemValidation checkRegistValidation(MemberVO member) {
		if (member == null) {
			return MemValidation.MEM_NULL;
		} else if (member.getMem_id() == null || member.getMem_id().equals("")
				|| member.getMem_id().length() < MEM_MIN_ID_LENGTH || member.getMem_id().length() > MEM_MAX_ID_LENGTH) {
			return MemValidation.MEM_ID;
		} else if (member.getMem_passwd() == null || member.getMem_passwd().equals("")
				|| member.getMem_passwd().length() < MEM_MIN_PASSWD_LENGTH
				|| member.getMem_passwd().length() > MEM_MAX_PASSWD_LENGTH) {
			return MemValidation.MEM_PASSWD;
		} else if (member.getMem_name() == null || member.getMem_name().equals("")
				|| member.getMem_name().length() < MEM_MIN_NAME_LENGTH
				|| member.getMem_name().length() > MEM_MAX_NAME_LENGTH) {
			return MemValidation.MEM_NAME;
		} else if (member.getMem_nickname() == null || member.getMem_nickname().equals("")
				|| member.getMem_nickname().length() < MEM_MIN_NICKNAME_LENGTH
				|| member.getMem_nickname().length() > MEM_MAX_NICKNAME_LENGTH) {
			return MemValidation.MEM_NICKNAME;
		} else if (member.getMem_email() == null || member.getMem_email().equals("")
				|| member.getMem_email().length() < MEM_MIN_EMAIL_LENGTH
				|| member.getMem_email().length() > MEM_MAX_EMAIL_LENGTH) {
			return MemValidation.MEM_EMAIL;
		}

		return MemValidation.OK;
	}

	// MemValidation 라고 따로 만든 enum 을 리턴
	public MemValidation checkValidation(int checkVal, MemberVO member) {
		if (member == null) {
			return MemValidation.MEM_NULL;
		}
		switch (checkVal) {
		case 1:
			if (member.getMem_id() == null || member.getMem_id().equals("")
					|| member.getMem_id().length() < MEM_MIN_ID_LENGTH
					|| member.getMem_id().length() > MEM_MAX_ID_LENGTH) {
				return MemValidation.MEM_ID;
			}
			break;
		case 2:
			if (member.getMem_passwd() == null || member.getMem_passwd().equals("")
					|| member.getMem_passwd().length() < MEM_MIN_PASSWD_LENGTH
					|| member.getMem_passwd().length() > MEM_MAX_PASSWD_LENGTH) {
				return MemValidation.MEM_PASSWD;
			}
			break;
		case 3:
			if (member.getMem_name() == null || member.getMem_name().equals("")
					|| member.getMem_name().length() < MEM_MIN_NAME_LENGTH
					|| member.getMem_name().length() > MEM_MAX_NAME_LENGTH) {
				return MemValidation.MEM_NAME;
			}
			break;
		case 4:
			if (member.getMem_nickname() == null || member.getMem_nickname().equals("")
					|| member.getMem_nickname().length() < MEM_MIN_NICKNAME_LENGTH
					|| member.getMem_nickname().length() > MEM_MAX_NICKNAME_LENGTH) {
				return MemValidation.MEM_NICKNAME;
			}
			break;
		case 5:
			if (member.getMem_email() == null || member.getMem_email().equals("")
					|| member.getMem_email().length() < MEM_MIN_EMAIL_LENGTH
					|| member.getMem_email().length() > MEM_MAX_EMAIL_LENGTH) {
				return MemValidation.MEM_EMAIL;
			}
			break;

		default:
			break;
		}

		return MemValidation.OK;
	}
	
	// JwtFilter 에서 반환되는 principal.getName() 값을 바꿔주기
	public Map<String,String> splitPrincipal(String getName) {
		String[] splitStr = getName.replace("{", "").replace("}", "").split(",");
		Map<String,String> resultMap = new HashMap<String, String>();
		
		resultMap.put("newToken" , splitStr[0].trim().replace("newToken=","").trim());
		resultMap.put("userId" , splitStr[1].trim().replace("userId=","").trim());
		
		return resultMap;
	}

}
