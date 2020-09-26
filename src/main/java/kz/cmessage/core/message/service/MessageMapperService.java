package kz.cmessage.core.message.service;

import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.message.dto.MessageDto;
import kz.cmessage.core.message.model.Message;
import kz.cmessage.core.user.dto.UserDto;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.service.UserService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static java.lang.String.format;

@Service
public class MessageMapperService {

    private ModelMapper mapper;
    private MessageService messageService;

    @Autowired
    public MessageMapperService(ModelMapper mapper, MessageService messageService) {
        this.mapper = mapper;
        this.messageService = messageService;
    }

    public Message toModel(MessageDto dto) {
        return Objects.isNull(dto) ? null : mapper.map(dto, Message.class);
    }

    public MessageDto toDto(Message entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, MessageDto.class);
    }

    @PostConstruct
    public void setupMapper(){
        mapper.createTypeMap(MessageDto.class, Message.class)
                .addMappings(mapping -> {
                    mapping.skip(Message::setConversation);
                })
                .setPostConverter(toModelConverter());

    }

    private Converter<MessageDto, Message> toModelConverter(){
        return context -> {
            MessageDto source = context.getSource();
            Message destination = context.getDestination();
            try {
                mapSpecificFields(source, destination);
                return context.getDestination();
            } catch (ObjectNotFoundException e) {
                return null;
            }
        };
    }

    private void mapSpecificFields(MessageDto source, Message destination) {

    }
}
