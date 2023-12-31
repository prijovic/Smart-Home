package com.bsep.smart.home.controller;

import com.bsep.smart.home.dto.request.auth.LoginRequest;
import com.bsep.smart.home.dto.request.registration.RegistrationRequest;
import com.bsep.smart.home.dto.response.AuthTokenResponse;
import com.bsep.smart.home.dto.response.UserResponse;
import com.bsep.smart.home.services.auth.GetSelf;
import com.bsep.smart.home.services.auth.LogInUser;
import com.bsep.smart.home.services.auth.LoginDetailsExist;
import com.bsep.smart.home.services.auth.LogoutUser;
import com.bsep.smart.home.services.mail.ActivateEmail;
import com.bsep.smart.home.services.registration.RegisterNewUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import javax.validation.Valid;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LogInUser loginUser;
    private final GetSelf getSelf;
    private final RegisterNewUser registerNewUser;
    private final ActivateEmail activateEmail;
    private final LogoutUser logoutUser;
    private final LoginDetailsExist loginDetailsExist;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody final LoginRequest loginRequest) throws CertificateNotYetValidException, UnrecoverableKeyException, CertificateExpiredException, KeyStoreException, NoSuchAlgorithmException, AccountLockedException {
        return loginUser.execute(loginRequest);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login-details-exist")
    public int loginDetailsExist(@Valid @RequestBody final LoginRequest loginRequest) {
        return loginDetailsExist.execute(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public void logout() {
        logoutUser.execute();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/self")
    public UserResponse getSelf() {
        return getSelf.execute();
    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegistrationRequest registrationRequest) throws KeyStoreException {
        registerNewUser.execute(registrationRequest);
    }

    @PutMapping("/activateEmail/{token}")
    public void activateEmail(@PathVariable("token") final String token) {
        activateEmail.execute(token);
    }

}
