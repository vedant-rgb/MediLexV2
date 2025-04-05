package com.medilexV2.medPlus.security;


import com.medilexV2.medPlus.dto.LoginDTO;
import com.medilexV2.medPlus.dto.LoginResponseDTO;
import com.medilexV2.medPlus.dto.SignUpRequest;
import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.User;
import com.medilexV2.medPlus.exceptions.ResourceNotFoundException;
import com.medilexV2.medPlus.repository.UserRepository;
import com.medilexV2.medPlus.service.MedicalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final MedicalService medicalService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, AuthenticationManager authenticationManager, JWTService jwtService, MedicalService medicalService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.medicalService = medicalService;
    }


    public String signUp(SignUpRequest signUpRequest){
        Optional<User> user =userRepository.findByEmail(signUpRequest.getEmail());
        if(user.isPresent()){
            throw new RuntimeException("Teacher already present with Registration ID : "+signUpRequest.getEmail());
        }
        Medical medical = modelMapper.map(signUpRequest, Medical.class);
        medical.setLocation(new GeoJsonPoint(0.0, 0.0));
        medical.setPhotos(new ArrayList<>());
        medical.setProducts(new ArrayList<>());
        medicalService.saveMedical(medical);

        User toBeSavedUuser = new User(signUpRequest.getEmail(),passwordEncoder.encode(signUpRequest.getPassword()));
        User savedUser = userRepository.save(toBeSavedUuser);
        return "Successfully registered with Registration ID : "+savedUser.getEmail();
    }

    public LoginResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }


    public LoginResponseDTO refreshToken(String refreshToken) {
        String email = jwtService.getEmailFromToken(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + email));

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }


    private User getCurrentuser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
