package com.chandan.userauthservice.controllers;

import com.chandan.userauthservice.dtos.*;
import com.chandan.userauthservice.models.User;
import com.chandan.userauthservice.services.AuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {

        try {
            User user =  authService.signUp(signupRequestDto.getEmail(), signupRequestDto.getPassword());
            return new ResponseEntity<>(getUserDto(user), HttpStatus.OK);
        }catch (Exception exception){
            //return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            throw exception;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {

           try {

               Pair<User, MultiValueMap<String,String>> bodyWithHeaders = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
               return new ResponseEntity<>(getUserDto(bodyWithHeaders.a), bodyWithHeaders.b, HttpStatus.OK);
           }catch (Exception exception){
               return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
           }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
        return new ResponseEntity<>(authService.validateToken(validateTokenRequestDto.getToken(), validateTokenRequestDto.getId()), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        return null;
    }

    private UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        //userDto.setRoleSet(user.getRoleSet());
        return userDto;
    }
}
