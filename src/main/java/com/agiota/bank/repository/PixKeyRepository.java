package com.agiota.bank.repository;

import com.agiota.bank.model.pixkey.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PixKeyRepository extends JpaRepository<PixKey, String> {
    List<PixKey> findByOwnerId(Long ownerId);
    Optional<PixKey> findByKeyValue(String keyValue);
}