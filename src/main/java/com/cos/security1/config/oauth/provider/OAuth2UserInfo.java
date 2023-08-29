package com.cos.security1.config.oauth.provider;

public interface OAuth2UserInfo {
	String getproviderId();
	String getprovider();
	String getEmail();
	String getName();

}
