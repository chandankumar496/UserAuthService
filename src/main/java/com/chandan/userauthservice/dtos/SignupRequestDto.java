package com.chandan.userauthservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

@Getter
@Setter
public class SignupRequestDto {

    private String email;
    private String password;
}
