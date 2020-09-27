package kz.cmessage.core.user.service;

import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.user.dto.UpdateUserByManagerRequestDto;
import kz.cmessage.core.user.dto.UpdateUserRequestDto;
import kz.cmessage.core.user.dto.UserDto;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.lang.String.format;
import static kz.cmessage.core.common.constant.DefaultResponseMessage.INVALID_PAYLOAD;

@Service
@Transactional
public class UserService {

    private UserMapperService userMapperService;
    private UserRepository userRepository;
    private UserValidationService userValidationService;

    @Autowired
    public UserService(UserMapperService userMapperService, UserRepository userRepository,
                       UserValidationService userValidationService) {
        this.userMapperService = userMapperService;
        this.userRepository = userRepository;
        this.userValidationService = userValidationService;
    }

    // FIXME: 05.09.2020 Add logger
    public Optional<User> getById(Long id) {
        try {
            userValidationService.validateUserIdBySession(id);
        } catch (ValidationException e) {
            throw new IllegalAccessException("Illegal access attempt denied");
        }
        return userRepository.findById(id);
    }

    public Optional<UserDto> getByUsername(String username) {
        User user = userRepository.findByUsernameAndDeletedIsFalse(username);
        return Optional.ofNullable(userMapperService.toDto(user));
    }

    public UserDto create(UserDto userDto) {
        User user = userRepository.save(userMapperService.toModel(userDto));
        return userMapperService.toDto(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // FIXME: 05.09.2020 Add logger
    public UserDto update(Long id, UserDto userDto) {
        if (!userRepository.existsById(id)){
            throw new ObjectNotFoundException(format("User [id = %s] does not exist", id));
        }

        try {
            userValidationService.validateUserDtoByPersistentUserId(userDto, id);
        } catch (ValidationException e) {
            throw new BadRequestException(INVALID_PAYLOAD.getValue());
        }

        User updatedUser = userRepository.save(userMapperService.toModel(userDto));
        return userMapperService.toDto(updatedUser);
    }

    public UserDto updateFields(Long id, UpdateUserRequestDto requestDto) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] does not exist", id)));

        if (requestDto instanceof UpdateUserByManagerRequestDto) {
            setFieldsByManager(dbUser, (UpdateUserByManagerRequestDto) requestDto);
        } else {
            setFieldsByUser(dbUser, requestDto);
        }

        User updatedUser = userRepository.save(dbUser);
        return userMapperService.toDto(updatedUser);
    }

    private void setFieldsByManager(User user, UpdateUserByManagerRequestDto dto) {
        setFieldsByUser(user, dto);
        if (dto.getBlockedDate() != null)
            user.setBlockedDate(dto.getBlockedDate());
        if (dto.getRole() != null)
            user.setRole(dto.getRole());
        user.setDeleted(dto.isDeleted());
    }

    private void setFieldsByUser(User user, UpdateUserRequestDto dto) {
        if (dto.getUsername() != null)
            user.setUsername(dto.getUsername());
        if (dto.getLocale() != null)
            user.setLocale(dto.getLocale());
    }
}
