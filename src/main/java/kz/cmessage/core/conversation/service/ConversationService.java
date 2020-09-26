package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.dto.CreateConversationRequestDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.enumiration.ConversationType;
import kz.cmessage.core.exception.BadRequestException;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Transactional
public class ConversationService {

    private ConversationRepository conversationRepository;
    private SessionUtil sessionUtil;
    private ConversationValidationService conversationValidationService;
    private ConversationMapperService conversationMapperService;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, SessionUtil sessionUtil,
                               ConversationValidationService conversationValidationService,
                               ConversationMapperService conversationMapperService) {
        this.conversationRepository = conversationRepository;
        this.sessionUtil = sessionUtil;
        this.conversationValidationService = conversationValidationService;
        this.conversationMapperService = conversationMapperService;
    }

    public List<ConversationDto> getUserConversations() {
        User user = sessionUtil.getSession().getUser();
        return conversationRepository.findAllActiveByUserId(user.getId()).stream()
                .map(conversation -> conversationMapperService.toDto(conversation))
                .collect(Collectors.toList());
    }

    public Optional<ConversationDto> getUserConversationById(Long id) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(id);
        conversationOptional.ifPresent(conversation -> {
            try {
                conversationValidationService.validateUserIsNotLeftParticipant(conversation);
            } catch (ValidationException e) {
                throw new IllegalAccessException("Illegal access attempt denied");
            }
        });
        return Optional.ofNullable(conversationMapperService.toDto(conversationOptional.orElse(null)));
    }

    public ConversationDto create(CreateConversationRequestDto dto) {
        Conversation conversation;
        try {
            conversationValidationService.validateConversationTypeByParticipantsCount(dto.getConversation());
            conversationValidationService.validateUserIsCreator(dto.getConversation());
        } catch (ValidationException e) {
            throw new BadRequestException("Unacceptable payload");
        }

        if (dto.getConversation().getType() == ConversationType.GROUP) {
            conversation = conversationRepository.save(conversationMapperService.toModel(dto.getConversation()));
        } else {
            Long firstUserId = dto.getConversation().getCreatorId();
            Long secondUserId = dto.getParticipantUserIds().stream()
                    .filter(participantId -> !firstUserId.equals(participantId))
                    .findFirst()
                    .orElse(0L);
            conversation = conversationRepository.findPrivateByUserIds(firstUserId, secondUserId)
                    .orElse(conversationRepository.save(conversationMapperService.toModel(dto.getConversation())));
        }
        return conversationMapperService.toDto(conversation);
    }

    public ConversationDto update(Long id, ConversationDto dto){
        if (!conversationRepository.existsById(id)){
            throw new ObjectNotFoundException(format("Conversation [id = %s] does not exist", id));
        }

        try {
            conversationValidationService.validateDtoByPersistentConversationId(dto, id);
        } catch (ValidationException e) {
            throw new BadRequestException("Payload and database data is not match");
        }

        Conversation conversation = conversationRepository.save(conversationMapperService.toModel(dto));
        return conversationMapperService.toDto(conversation);
    }
}
