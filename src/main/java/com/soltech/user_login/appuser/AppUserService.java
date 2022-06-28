package com.soltech.user_login.appuser;

import com.soltech.user_login.registration.token.ConfirmationToken;
import com.soltech.user_login.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private final static String USER_NOT_FOUND_MSG="user with the email %s not found";
    private final AppuserRepository appuserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appuserRepository.findByEmail(email).
                orElseThrow(()-> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG)));
    }
    public String signupUser(AppUser appUser){
        boolean userExists = appuserRepository.findByEmail(appUser.getEmail())
                .isPresent();
        if(userExists){
            throw new IllegalStateException("Email already taken");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);
        appuserRepository.save(appUser);
        // send confirmation token

        String token=UUID.randomUUID().toString();
        ConfirmationToken confirmationToken=new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                appUser
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        //send email

        return token;
    }
    public int enableAppUser(String email) {
        return appuserRepository.enableAppUser(email);
    }
}
