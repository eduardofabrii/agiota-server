package com.agiota.bank.service.supportticket;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.exception.ResourceNotFoundException;
import com.agiota.bank.mapper.SupportTicketMapper;
import com.agiota.bank.model.supportticket.SupportTicket;
import com.agiota.bank.model.user.User;
import com.agiota.bank.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketMapper supportTicketMapper;

    @Override
    public SupportTicketResponseDTO create(SupportTicketRequestDTO request, User user) {
        SupportTicket supportTicket = supportTicketMapper.toEntity(request, user);
        return supportTicketMapper.toResponse(supportTicketRepository.save(supportTicket));
    }

    @Override
    public List<SupportTicketResponseDTO> findAllByUser(User user) {
        return supportTicketRepository.findByUserId(user.getId()).stream()
                .map(supportTicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportTicketResponseDTO findById(Long id) {
        SupportTicket supportTicket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket de suporte não encontrado com o ID: " + id));
        return supportTicketMapper.toResponse(supportTicket);
    }

    @Override
    public SupportTicketResponseDTO update(Long id, UpdateSupportTicketRequestDTO request) {
        SupportTicket supportTicket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket de suporte não encontrado com o ID: " + id));
        supportTicketMapper.updateEntityFromDto(request, supportTicket);
        return supportTicketMapper.toResponse(supportTicketRepository.save(supportTicket));
    }

    @Override
    public void delete(Long id) {
        if (!supportTicketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket de suporte não encontrado com o ID: " + id);
        }
        supportTicketRepository.deleteById(id);
    }
}
