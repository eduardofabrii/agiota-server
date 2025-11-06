package com.agiota.bank.repository;

import com.agiota.bank.model.beneficiary.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    
    List<Beneficiary> findByOwnerAccountId(Long ownerAccountId);
    
    boolean existsByCpfCnpj(String cpfCnpj);
}