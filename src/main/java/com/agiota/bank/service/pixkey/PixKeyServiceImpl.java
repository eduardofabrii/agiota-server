package com.agiota.bank.service.pixkey;

import com.agiota.bank.dto.request.PixKeyRequestDTO;
import com.agiota.bank.dto.response.PixKeyResponseDTO;
import com.agiota.bank.exception.InvalidRequestException;
import com.agiota.bank.exception.ResourceAlreadyExistsException;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.PixKeyMapper;
import com.agiota.bank.model.account.Account;
import com.agiota.bank.model.pixkey.PixKey;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.AccountRepository;
import com.agiota.bank.repository.PixKeyRepository;
import com.agiota.bank.service.notification.NotificationService;
import lombok.AllArgsConstructor;
import com.agiota.bank.exception.AcessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@AllArgsConstructor
@Service
public class PixKeyServiceImpl implements PixKeyService {
    private final PixKeyRepository pixKeyRepository;
    private final PixKeyMapper mapper;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    @Override
    public PixKeyResponseDTO createPixKey(PixKeyRequestDTO dto, Long accountId, User user) {
        if (!StringUtils.hasText(dto.keyValue())) {
            throw new InvalidRequestException("O valor da chave PIX não pode ser nulo ou vazio.");
        }
        if (dto.type() == null) {
            throw new InvalidRequestException("O tipo da chave PIX não pode ser nulo.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new AcessDeniedException("Você não tem permissão para criar uma chave PIX para esta conta.");
        }

        if (pixKeyRepository.existsById(dto.keyValue())) {
            throw new ResourceAlreadyExistsException("Chave pix '" + dto.keyValue() + "' já existe.");
        }
        PixKey pixKey = mapper.toPixKeyPostRequest(dto, accountId);
        pixKeyRepository.save(pixKey);

        notificationService.notifyPixKeyCreated(
            account.getUser(),
            pixKey.getKeyValue(),
            pixKey.getType().toString()
        );

        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public PixKeyResponseDTO getPixKey(String keyValue) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("Chava pix não encontrada"));
        return mapper.toPixKeyPostResponse(pixKey);
    }

    @Override
    public List<PixKeyResponseDTO> listPixKeyByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada com o ID: " + accountId));

        List<PixKey> pixKeys = pixKeyRepository.findByAccountId(accountId);
        return mapper.toPixKeyListResponse(pixKeys);
    }

    @Override
    public void deletePixKey(String keyValue, User user) {
        PixKey pixKey = pixKeyRepository.findByKeyValue(keyValue)
                .orElseThrow(() -> new ResourceNotFoundException("Chave Pix não encontrada"));

        if (!pixKey.getAccount().getUser().getId().equals(user.getId())) {
            throw new AcessDeniedException("Você não tem permissão para deletar esta chave PIX.");
        }
        
        notificationService.notifyPixKeyDeleted(pixKey.getAccount().getUser(), keyValue);
        
        pixKeyRepository.deleteById(keyValue);
    }
}
