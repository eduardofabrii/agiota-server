package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.exception.ResourceAlreadyExistsException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.PixKeyMapper;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class PixKeyServiceImpl implements PixKeyService {
    private final PixKeyRepository pixKeyRepository;
    private final PixKeyMapper mapper;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    @Override
    public PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long accountId) {
        if (pixKeyRepository.existsById(dto.keyValue())) {
            throw new ResourceAlreadyExistsException("Pix key '" + dto.keyValue() + "' already exists.");
        }
        PixKey pixKey = mapper.toPixKeyPostRequest(dto, accountId);
        pixKeyRepository.save(pixKey);
        
        accountRepository.findById(accountId).ifPresent(account -> {
            notificationService.notifyPixKeyCreated(
                account.getUser(), 
                pixKey.getKeyValue(), 
                pixKey.getType().toString()
            );
        });
        
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public PixKeyResponseDTO getPixKey(String keyValue) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public List<PixKeyResponseDTO> listPixKeyByAccountId(Long accountId) {
        List<PixKey> pixKeys = pixKeyRepository.findByAccountId(accountId);
        return mapper.toPixKeyListResponse(pixKeys);
    }

    @Override
    public void deletePixKey(String keyValue) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("PixKey not found"));
        
        notificationService.notifyPixKeyDeleted(pixKey.getAccount().getUser(), keyValue);
        
        pixKeyRepository.deleteById(keyValue);
    }
}
