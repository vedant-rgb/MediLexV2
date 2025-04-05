package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}

