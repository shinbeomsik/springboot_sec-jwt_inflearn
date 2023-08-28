package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller // View 를 리턴하겠다.
public class IndexController {
	
	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(
			Authentication authentication,
			@AuthenticationPrincipal UserDetails userDetails) {//DI(의존성주입)
			//@AuthenticationPrincipal 세션정보에 접근 가능
			//  1번==@AuthenticationPrincipal PrincipalDetails userDetails 이렇게 변견하면 
		System.out.println("/test/login=============");
		PrincipalDetails principalDetails = (PrincipalDetails)authentication.getPrincipal();   
		// └ UserDetails 로 바꿔줄수 있는데 이유는 PrincipalDetails을 implements을 받았기 때문에
		System.out.println("authentication :" + principalDetails.getUser());
		
		System.out.println("userDetails :" + userDetails.getUsername());
	  // 1번 ==System.out.println("userDetails :" + userDetails.getUser());
		return "세션 정보 확인하기";
		
		//큰 전체 세션안에 시큐리티 세션이 있다.
		//스프링 시큐리티는 자기만의 세션을 가지고있다
		//시큐리티 세션에 들어갈수있는 타입은 Authentication객체뿐 
		// Authentication 안에                                UserDetails(일반 로그인) 과   OAuth2User(OAuth로그인) 들어갈수있다
		// @AuthenticationPrincipal(UserDetails) userDetails┛           ┗@AuthenticationPrincipal OAuth2User oauth
		//                         (PrincipalDetails)
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) {//DI(의존성주입)
		System.out.println("/test/oauth/login=============");
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();   
		System.out.println("authentication :" + oauth2User.getAttributes());
		System.out.println("oauth2user :" + oauth.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	
	
	
	@GetMapping({"","/"})
	public String index() {
		// 머스테치 기본폴더 src/main/resoutces/
		// 뷰리졸버 설정 : templates(prefix).mustache(surfix) 생략가능!!
		return "index"; //src/main/resources/templates/index.mustache 기본설정되있음  config패키지에서 변경해줘야됨 html을 사용하려면
	}
	
	//OAuth 로그인을 해도 PrincipalDetails
	//일반 로그은을 해도 PrincipalDetails
	
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("PrincipalDetails:" + principalDetails.getUser());
		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링시큐리티 해당주소를 낚아챈다 --> SecurityConfig 파일 생성후 작동안함
	@GetMapping("/loginForm")
	public  String login() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public  String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public  String join(User user) {
		System.out.println(user);
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user); // 이렇게 하더라도 회원가입을 잘됨 
								   // 비밀번호가 1234일경우 시큐리티로 로그인안됨  WHY:패스워드가 암호화가 안되어있기 때문에
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN") //<== 이거는 특정 메서드에 걸고싶을떄
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') OR hasRole('ROLE_ADMIN')") //<== 이거는 특정 메서드에 실행되기 직전에 실행 , 여러개를 걸고싶을떄
	//@PostAuthorize 메서드가 끝나고나서 실행
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}
	
}
