package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.exception.MapperException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static java.lang.String.format;

@Service
public class ConversationMapperService {

    private ModelMapper mapper;
    private ConversationRepository conversationRepository;

    private UserRepository userRepository;

    @Autowired
    public ConversationMapperService(ModelMapper mapper, ConversationRepository conversationRepository, UserRepository userRepository) {
        this.mapper = mapper;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public Conversation toModel(ConversationDto dto) throws MapperException {
        try {
            return Objects.isNull(dto) ? null : mapper.map(dto, Conversation.class);
        } catch (ObjectNotFoundException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public ConversationDto toDto(Conversation entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, ConversationDto.class);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Conversation.class, ConversationDto.class)
                .addMappings(mapping -> mapping.skip(ConversationDto::setCreatorId))
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(ConversationDto.class, Conversation.class)
                .addMappings(mapping -> {
                    mapping.skip(Conversation::setCreator);
                    mapping.skip(Conversation::setCreatedDate);
                })
                .setPostConverter(toModelConverter());
    }

    private Converter<Conversation, ConversationDto> toDtoConverter() {
        return context -> {
            Conversation source = context.getSource();
            ConversationDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private Converter<ConversationDto, Conversation> toModelConverter() {
        return context -> {
            ConversationDto source = context.getSource();
            Conversation destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private void mapSpecificFields(Conversation source, ConversationDto destination) {
        destination.setCreatorId(source.getCreator().getId());
    }

    private void mapSpecificFields(ConversationDto source, Conversation destination) {
        Conversation conversation = conversationRepository.findById(source.getId())
                .orElseThrow(() -> new ObjectNotFoundException(format("Conversation [id = %s] does not exist", source.getId())));
        User creator = userRepository.findById(source.getCreatorId())
                .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] does not exist", source.getCreatorId())));
        destination.setCreator(creator);
        destination.setCreatedDate(conversation.getCreatedDate());
    }
}
