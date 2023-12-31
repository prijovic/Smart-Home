package com.bsep.smart.home.services.jwt;

import com.bsep.smart.home.configProperties.CustomProperties;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JwtGenerateToken {
	private final CustomProperties customProperties;

	public String execute(final String email, final long expirationMilliseconds) {
		return Jwts.builder()
			.setSubject(email)
			.setExpiration(new Date(System.currentTimeMillis() + expirationMilliseconds))
			.setIssuedAt(new Date())
			.signWith(SignatureAlgorithm.HS512, customProperties.getJwtSecret().getBytes())
			.compact();
	}
}
