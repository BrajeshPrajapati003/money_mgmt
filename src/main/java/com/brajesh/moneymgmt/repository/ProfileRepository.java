package com.brajesh.moneymgmt.repository;

import com.brajesh.moneymgmt.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // Select * from tbl_profiles where email = ?
    Optional<ProfileEntity> findByEmail(String email);

    // Select * from tbl_profiles when activation_token = ?
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
