package com.medilexV2.medPlus.security;


import com.medilexV2.medPlus.dto.*;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.Users;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.repository.UserRepository;
import com.medilexV2.medPlus.service.MedicalService;
import com.medilexV2.medPlus.thirdPartyService.OcrService;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service

public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MedicalService medicalService;
    private final MedicalRepository medicalRepository;
    Logger logger = org.apache.logging.log4j.LogManager.getLogger(AuthService.class);
    private final UserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthenticationManager authenticationManager, JWTService jwtService, MedicalService medicalService, MedicalRepository medicalRepository,
                       UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.medicalService = medicalService;
        this.medicalRepository = medicalRepository;
        this.userRepository = userRepository;
    }

    public String signUp(SignUpRequest signUpRequest) {
        try {
            logger.info("Started signup with email " + signUpRequest.getEmail());

            // Check if medical store already exists
            Optional<Medical> medical = medicalRepository.findByEmail(signUpRequest.getEmail());
            if (medical.isPresent()) {
                throw new RuntimeException("Medical is already registered with the email: " + signUpRequest.getEmail());
            }

            Users users = new Users();
            users.setEmail(signUpRequest.getEmail());
            users.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            users.setRole("STORE");
            users.setCreatedAt(LocalDateTime.now());
            users.setUpdatedAt(LocalDateTime.now());
            users.setMedicalEmail(signUpRequest.getEmail());
            userRepository.save(users);

            // Create GeoJsonPoint from coordinates
            GeoJsonPoint geoPoint = new GeoJsonPoint(signUpRequest.getLongitude(), signUpRequest.getLatitude());

            Medical newMedical = new Medical();
            newMedical.setEmail(signUpRequest.getEmail());
            newMedical.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            newMedical.setMedicalName(signUpRequest.getMedicalName());
            newMedical.setMedicalAddress(signUpRequest.getMedicalAddress());
            newMedical.setLicenseNumber(signUpRequest.getLicenseNumber());
            newMedical.setContactNumber(signUpRequest.getContactNumber());
            newMedical.setPhotos(new ArrayList<>());
            newMedical.setProducts(new ArrayList<>());
            newMedical.setFirstTimeLogin(true);
            newMedical.setActive(true);
            newMedical.setCreatedAt(LocalDateTime.now());
            newMedical.setUpdatedAt(LocalDateTime.now());

            // Save to DB
            Medical saved = medicalRepository.save(newMedical);
            logger.info("Saved: " + saved);

            return "Successfully registered with Registration ID: " + saved.getEmail();

        } catch (RuntimeException e) {
            logger.error("Error during signup", e);
            throw new RuntimeException("Signup failed: " + e.getMessage());
        }
    }


    public LoginResponseDTO login(LoginDTO loginDTO) {
        logger.info("Attempting login for email: " + loginDTO.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(), loginDTO.getPassword())
        );

        Users user = (Users) authentication.getPrincipal(); // Cast to Users
        logger.info("Authenticated user: " + user.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }


    public LoginResponseDTO refreshToken(String refreshToken) {
        String email = jwtService.getEmailFromToken(refreshToken);

        // Fetch the user from the repository using email
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate a new access token for the user
        String accessToken = jwtService.generateAccessToken(user);

        // Return the new access token and the original refresh token
        return new LoginResponseDTO(accessToken, refreshToken);
    }



    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        Users currUser = getCurrentUser();
        Medical user = medicalRepository.findByEmail(currUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Medical not found for email : " + currUser.getUsername()));
        String encoded = passwordEncoder.encode(resetPasswordDTO.getOldPassword());
        if(!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), currUser.getPassword())){
            throw new RuntimeException("Old password is wrong");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setFirstTimeLogin(false);
        Medical savedUser = medicalRepository.save(user);
        return "Password updated successfully";
    }


    private Users getCurrentUser() {
        return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
