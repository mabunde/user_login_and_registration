package com.soltech.user_login.registration;


import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class RegistrationRequest {

    private final String firstName;
    private final String lastName;
    private final String password;
    private final String email;

}
