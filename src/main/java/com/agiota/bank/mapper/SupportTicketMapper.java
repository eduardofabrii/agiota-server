package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.model.supportticket.SupportTicket;
import com.agiota.bank.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class SupportTicketMapper {

    public SupportTicket toEntity(SupportTicketRequestDTO dto, User user) {
        SupportTicket supportTicket = new SupportTicket();
        supportTicket.setUser(user);
        supportTicket.setTitle(dto.getTitle());
        supportTicket.setDescription(dto.getDescription());
        return supportTicket;
    }

    public SupportTicketResponseDTO toResponse(SupportTicket entity) {
        SupportTicketResponseDTO response = new SupportTicketResponseDTO();
        response.setId(entity.getId());
        response.setUserId(entity.getUser().getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setStatus(entity.getStatus());
        response.setResponse(entity.getResponse());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public void updateEntityFromDto(UpdateSupportTicketRequestDTO dto, SupportTicket entity) {
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getResponse() != null) {
            entity.setResponse(dto.getResponse());
        }
    }
}
