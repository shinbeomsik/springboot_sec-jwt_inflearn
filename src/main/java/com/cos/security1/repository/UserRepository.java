package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//CRUD 함수를 JpaRepository 가 가지고있음
//@Repository 라는 어노테이션이 없어도 가능 ====> 상속을 받았기 때문에
public interface UserRepository extends JpaRepository<User, Integer> {
  
	//findBy규칙 ==>  Username 문법
	//select * from user where username =?
	public User findByUsername(String username); 
	   
	//궁금한게 있으면 Jpa Query mehtods 검색
	
	
	//select * from user where Email = ?  이런식으로 호출된다 
	//public User findByEmail();
	
}
