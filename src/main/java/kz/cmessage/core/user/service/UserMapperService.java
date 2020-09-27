package kz.cmessage.core.user.service;

import kz.cmessage.core.exception.MapperException;
import kz.cmessage.core.exception.ObjectNotFoundException;
import kz.cmessage.core.user.dto.UserDto;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import kz.cmessage.core.userInformation.model.UserInformation;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static java.lang.String.format;

@Service
public class UserMapperService {

    private ModelMapper mapper;
    private UserRepository userRepository;

    @Autowired
    public UserMapperService(ModelMapper mapper, UserRepository userRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public User toModel(UserDto dto) throws MapperException {
        try {
            return Objects.isNull(dto) ? null : mapper.map(dto, User.class);
        } catch (ObjectNotFoundException e) {
            throw new MapperException(e.getMessage(), e);
        }
    }

    public UserDto toDto(User entity) {
        return Objects.isNull(entity) ? null : mapper.map(entity, UserDto.class);
    }

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(UserDto.class, User.class)
                .addMappings(mapping -> {
                    mapping.skip(User::setPassword);
                    mapping.skip(User::setCreatedDate);
                    mapping.skip(User::setOnline);
                    mapping.skip(User::setLastOnlineDate);
                    mapping.skip(User::setUserInformation);
                })
                .setPostConverter(toModelConverter());
        mapper.createTypeMap(User.class, UserDto.class)
                .addMappings(mapping -> {
                    mapping.skip(UserDto::setFirstName);
                    mapping.skip(UserDto::setLastName);
                    mapping.skip(UserDto::setPhone);
                    mapping.skip(UserDto::setEmail);
                    mapping.skip(UserDto::setStatus);
                    mapping.skip(UserDto::setAvatarUrl);
                    mapping.skip(UserDto::setBirthDate);
                    mapping.skip(UserDto::setPosition);
                    mapping.skip(UserDto::setDepartment);
                })
                .setPostConverter(toDtoConverter());
    }

    private Converter<UserDto, User> toModelConverter() {
        return context -> {
            UserDto source = context.getSource();
            User destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private Converter<User, UserDto> toDtoConverter() {
        return context -> {
            User source = context.getSource();
            UserDto destination = context.getDestination();
            mapSpecificFields(source, destination);
            return context.getDestination();
        };
    }

    private void mapSpecificFields(UserDto source, User destination) {
        User user = userRepository.findById(source.getId())
                .orElseThrow(() -> new ObjectNotFoundException(format("User [id = %s] does not exist", source.getId())));
        destination.setPassword(user.getPassword());
        destination.setCreatedDate(user.getCreatedDate());
        destination.setOnline(user.isOnline());
        destination.setLastOnlineDate(user.getLastOnlineDate());
        destination.setUserInformation(user.getUserInformation());
    }

    // FIXME: 27.09.2020 Add mapping to PositionDto and DepartmentDto
    private void mapSpecificFields(User source, UserDto destination) {
        UserInformation userInformation = source.getUserInformation();
        destination.setFirstName(userInformation.getFirstName());
        destination.setLastName(userInformation.getLastName());
        destination.setPhone(userInformation.getPhone());
        destination.setEmail(userInformation.getEmail());
        destination.setStatus(userInformation.getStatus());
        destination.setAvatarUrl(userInformation.getAvatarUrl());
        destination.setBirthDate(userInformation.getBirthDate());
        destination.setDepartment(null);
        destination.setPosition(null);
    }
}
