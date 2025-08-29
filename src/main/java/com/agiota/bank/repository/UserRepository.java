package com.agiota.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.agiota.bank.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    UserDetails findByName(String name);

    @Query("SELECT u FROM User u WHERE u.name = :name")
    public Optional<User> findByUsername(@Param("name") String name);
}
