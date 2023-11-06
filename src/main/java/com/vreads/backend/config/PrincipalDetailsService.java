package com.vreads.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vreads.backend.handler.CustomValidationException;
import com.vreads.backend.mapper.MemberMapper;
import com.vreads.backend.vo.MemberVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService 
implements UserDetailsService 
{

    @Autowired
    private MemberMapper memberMapper;
    
    // 패스워드는 알아서 체킹하니깐 신경 ㄴ
    // 리턴이 잘되면 자동으로 세션을 만듬
    @Override
    public UserDetails loadUserByUsername(String mem_id) throws UsernameNotFoundException {
        MemberVO member = memberMapper.findMemberById(mem_id);
        System.out.println("로그인 아이디 오니 ? " + mem_id);
        if (member == null) {
            throw new CustomValidationException("회원 정보를 찾을 수 없습니다.", null);
        }

        if (member.getRole().equals("ROLE_REST")) {
            throw new CustomValidationException("이 계정은 현재 비활성화 상태입니다.", null);
        }
        
        return new PrincipalDetails(member);
    }
}