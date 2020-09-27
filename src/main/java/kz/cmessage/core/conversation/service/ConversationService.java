package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.dto.CreateConversationRequestDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.enumiration.ConversationType;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.MapperException;
import kz.cmessage.core.exception.UnprocessableEntityException;
import kz.cmessage.core.participant.service.ParticipantValidationService;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kz.cmessage.core.common.constant.DefaultResponseMessage.ILLEGAL_ACCESS_ATTEMPT_DENIED;
import static kz.cmessage.core.common.constant.DefaultResponseMessage.INVALID_PAYLOAD;

@Slf4j
@Service
@Transactional
public class ConversationService {

    private ConversationRepository conversationRepository;
    private SessionUtil sessionUtil;
    private ConversationValidationService conversationValidationService;
    private ConversationMapperService conversationMapperService;
    private ParticipantValidationService participantValidationService;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, SessionUtil sessionUtil,
                               ConversationValidationService conversationValidationService,
                               ConversationMapperService conversationMapperService, ParticipantValidationService participantValidationService) {
        this.conversationRepository = conversationRepository;
        this.sessionUtil = sessionUtil;
        this.conversationValidationService = conversationValidationService;
        this.conversationMapperService = conversationMapperService;
        this.participantValidationService = participantValidationService;
    }

    public List<ConversationDto> getUserConversations() {
        User user = sessionUtil.getSession().getUser();
        return conversationRepository.findAllActiveByUserId(user.getId()).stream()
                .map(conversation -> conversationMapperService.toDto(conversation))
                .collect(Collectors.toList());
    }

    public Optional<ConversationDto> getUserConversationById(Long id) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService.isUserNotLeftParticipantByConversationIdAndUserId(id, sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not a participant of conversation [id = {}] but trying to get conversation",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), id);
            throw new IllegalAccessException("Illegal access attempt denied");
        }
        return conversationRepository.findById(id)
                .map(conversation -> conversationMapperService.toDto(conversation));
    }

    public ConversationDto create(CreateConversationRequestDto dto) {
        User sessionUser = sessionUtil.getSession().getUser();
        Conversation conversation;

        if (!conversationValidationService
                .isRightTypeSetByConversationDtoAndParticipantsIds(dto.getConversation(), dto.getParticipantUserIds())) {
            log.error("Set incorrect conversation type [{}], when participants count is {} ",
                    dto.getConversation().getType(), dto.getParticipantUserIds().size());
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue());
        }

        if (sessionUser.getId().equals(dto.getConversation().getCreatorId())) {
            log.error("Dto creator [User id = {}] and session user [id = {}] not match",
                    dto.getConversation().getCreatorId(), sessionUser.getId());
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue());
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

    public ConversationDto update(Conversation conversation, ConversationDto dto) {
        User sessionUser = sessionUtil.getSession().getUser();
        Conversation conversationFromDto;

        if (!participantValidationService.isUserAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("User [id = {}] is not an admin of conversation [id = {}] but trying to update it",
                    sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        try {
            conversationFromDto = conversationMapperService.toModel(dto);
        } catch (MapperException e) {
            log.error("Cannot map Conversation dto to Conversation entity: {}", e.getMessage());
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue(), e);
        }

        if (conversationValidationService.isSecuredFieldsChanged(conversation, conversationFromDto)) {
            log.error("Secured fields [creatorId, type] of Conversation cannot be changed");
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue());
        }

        return conversationMapperService.toDto(conversationRepository.save(conversationFromDto));
    }
}
