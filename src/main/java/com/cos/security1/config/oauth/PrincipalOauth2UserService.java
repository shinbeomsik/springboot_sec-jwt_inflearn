package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리 되는 함수
	// 함수 종료시 @AuthenticationPrincipal 가 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("getClientRegistration :" + userRequest.getClientRegistration()); 
		// RegistrationId로 어떤 OAuth로 로그인 했는지 확인 가능
		System.out.println("getAccessToken :" + userRequest.getAccessToken().getTokenValue());
		
		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글 로그인 버튼 클릭 ==> 구글 로그인창보임 ==> 로그인 완료 ==> code를 리턴(OAuth-client라이브러리 가 받음) ==> AccessToken 요청
		// userRequest 정보 ==> 회원 프로필을 받아야함 (이떄 사용되는 함수가 loadUser 함수를 사용해서) ==> 구글로부터 회원 프로필 받아옴
		System.out.println("getAttributes :" + oauth2User.getAttributes());
		
		
		//이걸로 회원가입을 강제로 진행해볼 예정
		String provider = userRequest.getClientRegistration().getClientId();//google
		String providerId = oauth2User.getAttribute("sub");
		String username = provider + "_" + providerId; //google_(sub) 이름이 겹칠 이유가 없다.
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oauth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity ==null) {
			System.out.println("구글 로그인이 최초입니다.");
			userEntity = User.builder()
						.username(username)
						.password(password)
						.email(email)
						.role(role)
						.provider(provider)
						.providerId(providerId)
						.build();
			userRepository.save(userEntity);
		}else {
			System.out.println("구글 로그인을 이미 한적이 있습니다. 당신은 자동회원 가입이 되어있습니다.");
		}
		
		return new PrincipalDetails(userEntity ,oauth2User.getAttributes());
		
		//우리 데이터베이스에는   username = "google_102628901668706912526"
		//             	 password = "암호화(겟인데어)"
		//               email =  "sbsdkssud@gmail.com"
		//               role = "ROLE_USER"
		//               provider = "google"
		//               providerId = "102628901668706912526" 이렇게 저장할 예정
	}
}
