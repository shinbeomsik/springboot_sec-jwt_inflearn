package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화, preAuthorize과postAuthorize어노테이션을 함께 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private PrincipalOauth2UserService PrincipalDetailsService;
	
	@Bean // 해당 메서드의 리턴되는 오브젝트를 Ioc로 등록
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests()
			.antMatchers("/user/**").authenticated() //인증만 되면 들어갈수 있는 주소!
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			.anyRequest().permitAll() // 저 위에있는 권한 외에는  모두  \\permitAll() 은 모든 사용자의 접근을 허용
			.and()  // 만약 접근 권한이 없다면 로그인페이지로 돌아간다.
			.formLogin()//
			.loginPage("/loginForm")// 여기까지가 로그인페이지로 돌아가게하는방법
					// 	└이 페이지에 와서 로그인을 한다면 ===> 1번
			// 만약 loginForm.html에 있는  name="username" 을 다른거로 바꾸고 싶다면
			// .usernameParameter("바꾼 이름") 으로 설정하면 된다.
			.loginProcessingUrl("/login") //login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인진행
			.defaultSuccessUrl("/") //이 특정 페이지로 보내줌
			.and()
			.oauth2Login()
			.loginPage("/loginForm") //구글 로그인이 완료된 뒤의 후처리가 필요함 {Tip. 코드X,(엑세스토큰+ 사용자프로필정보 O)}
			// 1.코드받기(인증됬다{로그인됬다}) -> 2.엑세스토큰(권환을 얻는다) --
			// -> 3.사용자 프로필정보를 가져옴 -> 4-1.그 정보를 토대로 회원가입을 자동으로 진행시킴
			//  4-2.만약 쇼핑몰 같이 추가적인 정보가 필요할경우  추가적인 회원가입정보 필요 
			.userInfoEndpoint()
			.userService(PrincipalDetailsService);
	}

}
