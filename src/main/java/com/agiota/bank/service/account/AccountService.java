package com.agiota.bank.service.account;

import com.agiota.bank.dto.request.AccountRequestDTO;
import com.agiota.bank.dto.request.UpdateAccountStatusDTO;
import com.agiota.bank.dto.response.AccountResponseDTO;

import java.util.List;

public interface AccountService {
    AccountResponseDTO create(AccountRequestDTO requestDTO);

    AccountResponseDTO findById(Long id);

    List<AccountResponseDTO> findByUserId(Long userId);

    List<AccountResponseDTO> findAll();


    AccountResponseDTO update(Long id, AccountRequestDTO requestDTO);

    AccountResponseDTO updateStatus(Long id, UpdateAccountStatusDTO statusDTO);

    void delete(Long id);
}