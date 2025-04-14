package com.medilexV2.medPlus.repository;

import com.medilexV2.medPlus.entity.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<Users,String> {
    Optional<Users> findByEmail(String email);
}
