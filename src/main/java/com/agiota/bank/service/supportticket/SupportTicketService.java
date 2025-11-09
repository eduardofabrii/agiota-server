package com.agiota.bank.service.supportticket;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.model.user.User;

import java.util.List;

public interface SupportTicketService {
    SupportTicketResponseDTO create(SupportTicketRequestDTO request, User user);
    List<SupportTicketResponseDTO> findAllByUser(User user);
    SupportTicketResponseDTO findById(Long id);
    SupportTicketResponseDTO update(Long id, UpdateSupportTicketRequestDTO request);
    void delete(Long id);
}
