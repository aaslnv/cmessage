package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.message.dto.MessageDto;
import kz.cmessage.core.message.model.Message;
import kz.cmessage.core.message.service.MessageService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Service
public class ConversationMapperService {

    private ModelMapper mapper;
    private ConversationService conversationService;

    @Autowired
    public ConversationMapperService(ModelMapper mapper, ConversationService conversationService) {
        this.mapper = mapper;
        this.conversationService = conversationService;
    }

    public Conversation toModel(ConversationDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Conversation.class);
    }

    public ConversationDto toDto(Conversation entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, ConversationDto.class);
    }
}
