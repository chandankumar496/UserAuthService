package com.chandan.userauthservice.springsecurity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class SpringSecurity {

    @Bean
   public SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors().disable();
        httpSecurity.csrf().disable();
           httpSecurity.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
        return httpSecurity.build();
    }

    @Bean
    public SecretKey getSecretKey() {
      MacAlgorithm algorithm = Jwts.SIG.HS256;
        return algorithm.key().build();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
