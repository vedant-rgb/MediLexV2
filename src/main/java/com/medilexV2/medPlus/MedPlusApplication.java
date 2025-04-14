package com.medilexV2.medPlus;

import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.Users;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class MedPlusApplication implements CommandLineRunner {

	private final PasswordEncoder passwordEncoder;
	private final MedicalRepository medicalRepository;
	private final UserRepository userRepository;

	public MedPlusApplication(PasswordEncoder passwordEncoder, MedicalRepository medicalRepository,
							  UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.medicalRepository = medicalRepository;
		this.userRepository = userRepository;
	}

    public static void main(String[] args) {
		SpringApplication.run(MedPlusApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		Users users = new Users();
//		users.setEmail("admin@gmail.com");
//		users.setPassword(passwordEncoder.encode("admin"));
//		users.setRole("ADMIN");
//		userRepository.save(users);
	}
}
