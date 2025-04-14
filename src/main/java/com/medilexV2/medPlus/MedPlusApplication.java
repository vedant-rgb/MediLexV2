package com.medilexV2.medPlus;

import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.repository.MedicalRepository;
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

    public MedPlusApplication(PasswordEncoder passwordEncoder, MedicalRepository medicalRepository) {
        this.passwordEncoder = passwordEncoder;
        this.medicalRepository = medicalRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(MedPlusApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		Medical admin = new Medical();
//		admin.setEmail("admin@gmail.com");
//		admin.setPassword(passwordEncoder.encode("admin"));
//		admin.setMedicalName("Admin Medical Store");
//		admin.setRole("ROLE_ADMIN");
//		admin.setFirstTimeLogin(true);
//		medicalRepository.save(admin);
	}
}
