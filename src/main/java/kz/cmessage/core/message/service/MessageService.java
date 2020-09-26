package kz.cmessage.core.message.service;

import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.message.dto.MessageDto;
import kz.cmessage.core.message.model.Message;
import kz.cmessage.core.message.repository.MessageRepository;
import kz.cmessage.core.message.repository.MessageRepositoryCustom;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    private MessageRepository messageRepository;
    private final MessageRepositoryCustom messageRepositoryCustom;
    private SessionUtil sessionUtil;
    private MessageMapperService messageMapperService;
    private MessageValidationService messageValidationService;

    @Autowired
    public MessageService(MessageRepository messageRepository, SessionUtil sessionUtil,
                          MessageMapperService messageMapperService, MessageRepositoryCustom messageRepositoryCustom,
                          MessageValidationService messageValidationService) {
        this.messageRepository = messageRepository;
        this.sessionUtil = sessionUtil;
        this.messageMapperService = messageMapperService;
        this.messageRepositoryCustom = messageRepositoryCustom;
        this.messageValidationService = messageValidationService;
    }

    public Set<MessageDto> getAllByConversationId(Long conversationId) {
        User user = sessionUtil.getSession().getUser();
        return messageRepository.findAllByConversationIdAndUserId(conversationId, user.getId())
                .stream()
                .map(messageMapperService::toDto)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MessageDto::getSentDate))));
    }

    public Set<MessageDto> getLastByConversationIdAndFromAndCount(Long conversationId, Integer from, Integer count) {
        User user = sessionUtil.getSession().getUser();
        return messageRepositoryCustom
                .findTopByConversationIdAndUserIdAndFromAndCount(conversationId, user.getId(), from, count)
                .stream()
                .map(messageMapperService::toDto)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(MessageDto::getSentDate))));
    }

    public MessageDto create(Long conversationId, MessageDto dto) throws ValidationException {
        messageValidationService.validateCreation(conversationId, dto);
        Message message = messageMapperService.toModel(dto);
        return messageMapperService.toDto(messageRepository.save(message));
    }
}
