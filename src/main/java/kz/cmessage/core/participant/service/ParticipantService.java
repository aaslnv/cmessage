package kz.cmessage.core.participant.service;

import kz.cmessage.core.blackList.service.BlackListValidationService;
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

import java.time.LocalDateTime;
import java.util.*;
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
    private BlackListValidationService blackListValidationService;

    public ParticipantService(ParticipantRepository participantRepository, SessionUtil sessionUtil,
                              ParticipantValidationService participantValidationService,
                              ParticipantMapperService participantMapperService,
                              UserRepository userRepository, BlackListValidationService blackListValidationService) {
        this.participantRepository = participantRepository;
        this.sessionUtil = sessionUtil;
        this.participantValidationService = participantValidationService;
        this.participantMapperService = participantMapperService;
        this.userRepository = userRepository;
        this.blackListValidationService = blackListValidationService;
    }

    public List<ParticipantDto> getAllNotLeftByConversation(Conversation conversation) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService
                .isUserNotLeftParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not a participant of conversation [id = {}] but trying to get participants",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        return participantRepository.findAllByLeftIsFalseAndPrimaryKeyConversationId(conversation.getId()).stream()
                .map(participant -> participantMapperService.toDto(participant))
                .collect(Collectors.toList());
    }

    public Optional<ParticipantDto> getByConversationAndUser(Conversation conversation, User user) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService
                .isUserNotLeftParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not a participant of conversation [id = {}] but trying to get participant",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        return participantRepository.findByPrimaryKeyConversationIdAndPrimaryKeyUserId(conversation.getId(), user.getId())
                .map(participant -> participantMapperService.toDto(participant));
    }

    public List<ParticipantDto> addParticipants(Conversation conversation, Set<Long> userIds) throws IllegalAccessException {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService.isUserAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to add participants",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        userIds.removeIf(id -> Objects.isNull(id) ||
                blackListValidationService.ifUserBlockedByUserByIds(sessionUser.getId(), id));

        List<Long> existentParticipantUserIds = new ArrayList<>();
        List<Long> participantUserIdsToAdd = new ArrayList<>();

        for (Long userId : userIds) {
            if (participantValidationService.isUserParticipantByConversationIdAndUserId(conversation.getId(), userId)) {
                existentParticipantUserIds.add(userId);
            } else {
                participantUserIdsToAdd.add(userId);
            }
        }

        List<Participant> participants = participantUserIdsToAdd.stream()
                .map(id -> {
                    User participantUser = userRepository.findById(id)
                            .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] does not exist", id)));
                    ParticipantPk primaryKey = new ParticipantPk(conversation, participantUser);
                    return new Participant(primaryKey, ParticipantType.COMMON, false, LocalDateTime.now());
                })
                .collect(Collectors.toList());

        participantRepository.saveAll(participants);
        existentParticipantUserIds.forEach(id -> participantRepository
                .setLeftIsFalseByConversationIdAndUserIds(conversation.getId(), existentParticipantUserIds));

        return userIds.stream()
                .map(id -> participantRepository
                        .findByPrimaryKeyConversationIdAndPrimaryKeyUserId(conversation.getId(), id)
                        .orElseThrow(() -> new ObjectNotFoundException(format("Participant [Conversation id = %s, " +
                                "User id = %s] does not exist", conversation.getId(), id))))
                .map(participant -> participantMapperService.toDto(participant))
                .collect(Collectors.toList());
    }

    public ParticipantDto update(Conversation conversation, User user, ParticipantDto dto) {
        User sessionUser = sessionUtil.getSession().getUser();
        Participant participant;
        ParticipantPk participantPk;

        if (!user.getId().equals(sessionUser.getId()) && !participantValidationService
                .isUserAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to update participant [User id = {}]",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId(), user.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
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

    // TODO: 28.09.2020 Add messages deleting feature
    public void leaveByConversationAndUser(Conversation conversation, User user) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!user.getId().equals(sessionUser.getId()) && !participantValidationService
                .isUserAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to kick out participant [user id = {}]",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId(), user.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        if (participantRepository.existsOtherParticipantsByConversationIdAndUserId(conversation.getId(), user.getId()) &&
                !participantRepository.existsOtherAdminsByConversationIdAndUserId(conversation.getId(), user.getId())) {
            Participant participant = participantRepository
                    .findFirstByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndTypeOrderByCreatedDateDesc(
                            conversation.getId(), user.getId(), ParticipantType.ADMIN);
            participant.setType(ParticipantType.ADMIN);
            participantRepository.save(participant);
        }

        participantRepository.leaveByConversationIdAndUserId(conversation.getId(), user.getId());
    }

    public void switchIsAdmin(Conversation conversation, User user, boolean isAdmin) {
        User sessionUser = sessionUtil.getSession().getUser();

        if (!participantValidationService.isUserAdminByConversationIdAndUserId(conversation.getId(), sessionUser.getId())) {
            log.warn("{}: User [id = {}] is not an admin of conversation [id = {}] but trying to assign admin User [id = {}]",
                    ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue(), sessionUser.getId(), conversation.getId(), user.getId());
            throw new IllegalAccessException(ILLEGAL_ACCESS_ATTEMPT_DENIED.getValue());
        }

        if (participantValidationService.isUserNotLeftParticipantByConversationIdAndUserId(conversation.getId(), user.getId())) {
            throw new ObjectNotFoundException(format("Participant [Conversation id = %s, User id = %s] not found",
                    conversation.getId(), user.getId()));
        }

        if (isAdmin) {
            participantRepository.setTypeIsAdminByConversationIdAndUserId(conversation.getId(), user.getId());
        } else {
            participantRepository.setTypeIsCommonByConversationIdAndUserId(conversation.getId(), user.getId());
        }
    }
}
