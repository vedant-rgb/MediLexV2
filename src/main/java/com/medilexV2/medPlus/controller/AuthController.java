package com.medilexV2.medPlus.controller;

import com.medilexV2.medPlus.dto.LoginDTO;
import com.medilexV2.medPlus.dto.LoginResponseDTO;
import com.medilexV2.medPlus.dto.ResetPasswordDTO;
import com.medilexV2.medPlus.dto.SignUpRequest;
import com.medilexV2.medPlus.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        LoginResponseDTO loginResponse = authService.login(loginDTO);

        Cookie cookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (1000L * 60 * 60 * 24 * 30 * 6)); // 6 months
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponse);
    }



    @GetMapping("/checkIsFirstTimeLogin")
    public ResponseEntity<Boolean> checkIsFirstTimeLogin(){
        return ResponseEntity.ok(authService.checkIsFirstTimeLogin());
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
        return ResponseEntity.ok(authService.resetPassword(resetPasswordDTO));
    }
}
