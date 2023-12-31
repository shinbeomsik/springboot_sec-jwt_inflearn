package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인이 진행이 완료가 되면 시큐리티  seesion을 만들어준다. (Security ContextHolder)
//들어갈수 있는 오브젝트가 정해져있다 ==> Authentication 타입의 객체만
// 이 안에는 User 정보가 있어야 됨.
//User 오브젝트의 타입 ==> UserDetails 타입 객체

//Security Seesion ==> Authentication ==> UserDetails

@Data
public class PrincipalDetails implements UserDetails ,OAuth2User {

	private User user;// 콤포지션
	private Map<String ,Object> attributes;
	
	
	//일반 로그인할때
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	//OAuth 로그인 할때 
	public PrincipalDetails(User user, Map<String,Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	//해당 User의 권한을 리턴하는것!!
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword(); //user 객체의 해시화된 비밀번호를 반환
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		//우리 사이트에서 1년동안 로그인을 안한다면 휴면 계정으로 전환하기로함
		//현재시간 - 로그인시간 ==> 1년을 초과하면 false 로 리턴
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}
	
}
