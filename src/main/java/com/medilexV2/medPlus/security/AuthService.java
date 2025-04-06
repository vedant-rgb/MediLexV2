package com.medilexV2.medPlus.security;


import com.medilexV2.medPlus.dto.LoginDTO;
import com.medilexV2.medPlus.dto.LoginResponseDTO;
import com.medilexV2.medPlus.dto.SignUpRequest;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.MedicalRepository;
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

    public AuthService(PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthenticationManager authenticationManager, JWTService jwtService, MedicalService medicalService, MedicalRepository medicalRepository) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.medicalService = medicalService;
        this.medicalRepository = medicalRepository;
    }

    public String signUp(SignUpRequest signUpRequest){
        try {
            logger.info("started sigup with email " + signUpRequest.getEmail());

            Optional<Medical> medical = medicalRepository.findByEmail(signUpRequest.getEmail());
            if (medical.isPresent()) {
                throw new RuntimeException("Meeical is already register with the email : " + signUpRequest.getEmail());
            }
            Medical toBeSaved = modelMapper.map(signUpRequest, Medical.class);
            logger.info(toBeSaved);
            toBeSaved.setLocation(new GeoJsonPoint(0.0, 0.0));
            toBeSaved.setPhotos(new ArrayList<>());
            toBeSaved.setProducts(new ArrayList<>());
            toBeSaved.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
            Medical saved = medicalRepository.save(toBeSaved);
            logger.info(saved);
            return "Successfully registered with Registration ID : " + saved.getEmail();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        logger.info("Logging the medical "+loginDTO.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        Medical medical = (Medical) authentication.getPrincipal();
        logger.info("Found the medical "+medical);
        String accessToken = jwtService.generateAccessToken(medical);
        String refreshToken = jwtService.generateRefreshToken(medical);

        return new LoginResponseDTO(accessToken, refreshToken);
    }


    public LoginResponseDTO refreshToken(String refreshToken) {
        String email = jwtService.getEmailFromToken(refreshToken);

        Medical medical = medicalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + email));

        String accessToken = jwtService.generateAccessToken(medical);

        return new LoginResponseDTO(accessToken, refreshToken);
    }


    private Medical getCurrentuser(){
        return (Medical) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
