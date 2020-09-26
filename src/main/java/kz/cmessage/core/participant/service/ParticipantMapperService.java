package kz.cmessage.core.participant.service;

import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.exception.MapperException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.participant.dto.ParticipantDto;
import kz.cmessage.core.participant.model.Participant;
import kz.cmessage.core.participant.model.ParticipantPk;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import kz.cmessage.core.user.service.UserMapperService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static java.lang.String.format;

@Service
public class ParticipantMapperService {

    private ModelMapper mapper;
    private UserRepository userRepository;
    private ConversationRepository conversationRepository;
    private UserMapperService userMapperService;

    @Autowired
    public ParticipantMapperService(ModelMapper mapper, UserRepository userRepository, ConversationRepository conversationRepository, UserMapperService userMapperService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.userMapperService = userMapperService;
    }

    public Participant toModel(ParticipantDto dto) throws MapperException {
        try {
            return Objects.isNull(dto) ? null : mapper.map(dto, Participant.class);
        } catch (ObjectNotFoundException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public ParticipantDto toDto(Participant entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, ParticipantDto.class);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(Participant.class, ParticipantDto.class)
                .addMappings(mapping -> {
                    mapping.skip(ParticipantDto::setConversationId);
                    mapping.skip(ParticipantDto::setUser);
                })
                .setPostConverter(toDtoConverter());
        mapper.createTypeMap(ParticipantDto.class, Participant.class)
                .addMappings(mapping -> mapping.skip(Participant::setPrimaryKey))
                .setPostConverter(toModelConverter());
    }

    private Converter<Participant, ParticipantDto> toDtoConverter() {
        return context -> {
            Participant source = context.getSource();
            ParticipantDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private Converter<ParticipantDto, Participant> toModelConverter() {
        return context -> {
            ParticipantDto source = context.getSource();
            Participant destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private void mapSpecificFields(Participant source, ParticipantDto destination) {
        destination.setConversationId(source.getPrimaryKey().getConversation().getId());
        destination.setUser(userMapperService.toDto(source.getPrimaryKey().getUser()));
    }

    private void mapSpecificFields(ParticipantDto source, Participant destination) {
        Conversation conversation = null;
        User user = null;

        if (!Objects.isNull(source.getConversationId())) {
            conversation = conversationRepository.findById(source.getConversationId())
                    .orElseThrow(() -> new ObjectNotFoundException(format("Conversation [id = %s] not found", source.getConversationId())));
        }

        if (!Objects.isNull(source.getUser().getId())) {
            user = userRepository.findById(source.getUser().getId())
                    .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] not found", source.getUser().getId())));
        }

        ParticipantPk primaryKey = new ParticipantPk(conversation, user);
        destination.setPrimaryKey(primaryKey);
    }
}
