package kz.cmessage.core.participant.service;

import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.MapperException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.exception.UnprocessableEntityException;
import kz.cmessage.core.participant.dto.ParticipantDto;
import kz.cmessage.core.participant.model.Participant;
import kz.cmessage.core.participant.model.ParticipantPk;
import kz.cmessage.core.participant.repository.ParticipantRepository;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import kz.cmessage.core.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static kz.cmessage.core.common.constant.DefaultResponseMessage.ILLEGAL_ACCESS_ATTEMPT_DENIED;
import static kz.cmessage.core.common.constant.DefaultResponseMessage.INVALID_PAYLOAD;

@Slf4j
@Service
@Transactional
public class ParticipantService {

    private ParticipantRepository participantRepository;
    private SessionUtil sessionUtil;
    private ParticipantValidationService participantValidationService;
    private ParticipantMapperService participantMapperService;
    private UserRepository userRepository;

    public ParticipantService(ParticipantRepository participantRepository, SessionUtil sessionUtil,
                              ParticipantValidationService participantValidationService,
                              ParticipantMapperService participantMapperService,
                              UserRepository userRepository) {
        this.participantRepository = participantRepository;
        this.sessionUtil = sessionUtil;
        this.participantValidationService = participantValidationService;
        this.participantMapperService = participantMapperService;
        this.userRepository = userRepository;
    }

    public List<ParticipantDto> getAllNotLeftByConversation(Conversation conversation) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService
                .validateUserIsParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not a participant of conversation [id = {}] but trying to get participants",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        return participantRepository.findAllByLeftIsFalseAndPrimaryKeyConversationId(conversation.getId()).stream()
                .map(participant -> participantMapperService.toDto(participant))
                .collect(Collectors.toList());
    }

    public ParticipantDto getByConversationAndUser(Conversation conversation, User user) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService
                .validateUserIsParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not a participant of conversation [id = {}] but trying to get participant",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        return participantRepository.findByPrimaryKeyConversationIdAndPrimaryKeyUserId(conversation.getId(), user.getId())
                .map(participant -> participantMapperService.toDto(participant))
                .orElseThrow(() -> new ObjectNotFoundException(format("Participant [user id = %s] does not exist", user.getId())));
    }

    public List<ParticipantDto> addParticipants(Conversation conversation, Set<Long> userIds) throws IllegalAccessException {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService.validateUserIsAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to add participants",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        List<Participant> participants = userIds.stream()
                .map(id -> {
                    User participantUser = userRepository.findById(id)
                            .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] does not exist", id)));
                    ParticipantPk primaryKey = new ParticipantPk(conversation, participantUser);
                    return new Participant(primaryKey, ParticipantType.COMMON, false);
                })
                .collect(Collectors.toList());

        return participantRepository.saveAll(participants).stream()
                .map(participant -> participantMapperService.toDto(participant))
                .collect(Collectors.toList());
    }

    public ParticipantDto update(Conversation conversation, User user, ParticipantDto dto) {
        Participant participant;
        ParticipantPk participantPk;

        if (!participantRepository
                .existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserId(conversation.getId(), user.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to add participants",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), user.getId(), conversation.getId());
            throw new IllegalAccessException(format("Participant [Conversation ID = %s, User ID = %s] does not exist",
                    conversation.getId(), user.getId()));
        }

        try {
            participant = participantMapperService.toModel(dto);
            participantPk = participant.getPrimaryKey();
        } catch (MapperException e) {
            log.error("Cannot map Participant dto to Participant entity: {}", e.getMessage());
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue(), e);
        }

        if (isNull(participantPk.getUser()) || isNull(participantPk.getConversation()) ||
                !participantPk.getUser().getId().equals(user.getId()) ||
                !participantPk.getConversation().getId().equals(conversation.getId())) {
            log.error("Request path variable [Conversation id = {}, User id ={}] and payload " +
                            "[Conversation id = {}, User id ={}] is not match", conversation.getId(), user.getId(),
                    dto.getConversationId(), dto.getUser().getId());
            throw new UnprocessableEntityException(INVALID_PAYLOAD.getValue());
        }

        return participantMapperService.toDto(participantRepository.save(participant));
    }

    public void leaveByConversationAndUser(Conversation conversation, User user) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!user.getId().equals(sessionUser.getId()) && !participantValidationService
                .validateUserIsAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to kick out User [id = {}]",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId(), user.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        participantRepository.leaveByConversationIdAndUserId(conversation.getId(), user.getId());
    }

    public void setParticipantAdminByConversationAndUser(Conversation conversation, User user) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService.validateUserIsAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to assign admin User [id = {}]",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId(), user.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        participantRepository.setAdminByConversationIdAndUserId(conversation.getId(), user.getId());
    }
}
