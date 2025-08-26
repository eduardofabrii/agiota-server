package com.agiota.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agiota.bank.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
}
