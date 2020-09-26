package kz.cmessage.core.user.service;

import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.user.dto.UserDto;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class UserValidationService {

    private SessionUtil sessionUtil;

    @Autowired
    public UserValidationService(SessionUtil sessionUtil) {
        this.sessionUtil = sessionUtil;
    }

    public void validateUserIdBySession(Long userId) throws ValidationException {
        User user = sessionUtil.getSession().getUser();
        if (!userId.equals(user.getId()))
            throw new ValidationException(format("User [id = %s] and session User [id = %s] is not match", userId, user.getId()));
    }

    public void validateUserDtoByPersistentUserId(@NonNull UserDto dto, @NonNull Long userId) throws ValidationException {
        if (!userId.equals(dto.getId())){
            throw new ValidationException(format("UserDto [id = %s] and User [id = %s] is not match", dto.getId(), userId));
        }
    }
}
