package com.agiota.bank.repository;

import com.agiota.bank.model.device.AuthorizedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorizedDeviceRepository extends JpaRepository<AuthorizedDevice, Long> {
    List<AuthorizedDevice> findByUserId(Long userId);
    Optional<AuthorizedDevice> findByUserIdAndIpAddress(Long userId, String ipAddress);
}
