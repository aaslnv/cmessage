package kz.cmessage.core.message.service;

import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.message.dto.MessageDto;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageValidationService {

    private SessionUtil sessionUtil;

    @Autowired
    public MessageValidationService(SessionUtil sessionUtil) {
        this.sessionUtil = sessionUtil;
    }

    public void validateCreation(Long conversationId, MessageDto dto) throws ValidationException {
        User user = sessionUtil.getSession().getUser();
        if (!dto.getSenderId().equals(user.getId()))
            throw new ValidationException("Sender user id and session user id is not match");
    }
}
