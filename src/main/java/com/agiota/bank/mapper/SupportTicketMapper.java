package com.agiota.bank.mapper;

import com.agiota.bank.dto.request.SupportTicketRequestDTO;
import com.agiota.bank.dto.request.UpdateSupportTicketRequestDTO;
import com.agiota.bank.dto.response.SupportTicketResponseDTO;
import com.agiota.bank.model.supportticket.SupportTicket;
import com.agiota.bank.model.user.User;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SupportTicketMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "response", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", source = "user")
    SupportTicket toEntity(SupportTicketRequestDTO dto, User user);

    @Mapping(source = "user.id", target = "userId")
    SupportTicketResponseDTO toResponse(SupportTicket entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateSupportTicketRequestDTO dto, @MappingTarget SupportTicket entity);
}
