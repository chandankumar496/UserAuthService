package com.chandan.userauthservice.services;

import com.chandan.userauthservice.clients.KafkaProducerClient;
import com.chandan.userauthservice.dtos.EmailDto;
import com.chandan.userauthservice.models.Session;
import com.chandan.userauthservice.models.SessionState;
import com.chandan.userauthservice.models.User;
import com.chandan.userauthservice.repositories.SessionRepository;
import com.chandan.userauthservice.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

     @Autowired
     private UserRepository userRepository;

     @Autowired
     private SessionRepository sessionRepository;

     @Autowired
     private BCryptPasswordEncoder bCryptPasswordEncoder;

     @Autowired
     private SecretKey secretKey;

     @Autowired
     private KafkaProducerClient kafkaProducerClient;

     @Autowired
     private ObjectMapper objectMapper;

     public User signUp(String email, String password) {

        Optional<User> optionalUser = userRepository.findUserByEmail(email);
        if (optionalUser.isPresent()) {
             return optionalUser.get();
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);

        // Sending Message in Kafka
         EmailDto emailDto = new EmailDto();
         emailDto.setTo(email);
         emailDto.setFrom("sampletest040496@gmail.com");
         emailDto.setSubject("User Registration");
         emailDto.setBody("Have a good Learning Experiences");

         try {
                kafkaProducerClient.sendMessage("sendEmail", objectMapper.writeValueAsString(emailDto));
                return user;
         }catch (JsonProcessingException jsonProcessingException){
               throw new RuntimeException(jsonProcessingException);
         }

     }

     public Pair<User, MultiValueMap<String,String>> login(String email, String password){

         Optional<User> optionalUser = userRepository.findUserByEmail(email);
         if(optionalUser.isEmpty())
             return null;

         if(!bCryptPasswordEncoder.matches(password, optionalUser.get().getPassword())){
             return null;
         }

         Map<String,Object> claims = new HashMap<String,Object>();
         claims.put("email", optionalUser.get().getEmail());
         claims.put("roles", optionalUser.get().getRoleSet());
         long currentTimeMillis= System.currentTimeMillis();
         claims.put("iat",currentTimeMillis);
         claims.put("exp",currentTimeMillis+100000000L);

        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        Session session = new Session();
        session.setUser(optionalUser.get());
        session.setToken(token);
        session.setSessionStatus(SessionState.ACTIVE);
        sessionRepository.save(session);

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, token);
        return new Pair<User, MultiValueMap<String, String>>(optionalUser.get(), headers);
     }


     public Boolean validateToken(String token, Long userId) {

       Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, userId);
       if(optionalSession.isEmpty()){
           System.out.println("User and token not found");
           return false;
       }

      JwtParser jwtParser =  Jwts.parser().verifyWith(secretKey).build();
      Claims claims = jwtParser.parseSignedClaims(token).getPayload();
       long tokenExpiry = (long)claims.get("exp");
      long  currentTimeMillis = System.currentTimeMillis();

      if(tokenExpiry < currentTimeMillis){
          System.out.println(tokenExpiry);
          System.out.println(currentTimeMillis);
          System.out.println("Token Expired");
          return false;
      }

      Optional<User> optionalUser = userRepository.findUserById(userId);
      String userEmail = optionalUser.get().getEmail();
       if (!userEmail.equals(claims.get("email"))){
           System.out.println(userEmail);
           System.out.println(claims.get("email"));
           System.out.println("Emails didn't match");
           return false;
       }

      return true;
     }

}
