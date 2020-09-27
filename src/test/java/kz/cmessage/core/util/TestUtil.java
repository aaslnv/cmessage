package kz.cmessage.core.util;

import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.department.model.Department;
import kz.cmessage.core.enumiration.ConversationType;
import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.enumiration.Role;
import kz.cmessage.core.participant.model.Participant;
import kz.cmessage.core.participant.model.ParticipantPk;
import kz.cmessage.core.position.model.Position;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.userInformation.model.UserInformation;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static List<User> prepareUsers() {
        return Arrays.asList(prepareUser(), prepareUser());
    }

    public static User prepareUser() {
        User user = new User();
        UserInformation userInformation = prepareUserInformation();
        user.setId(RANDOM.nextLong());
        user.setUsername(RandomStringUtils.randomAlphabetic(10));
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        user.setRole(Role.USER);
        user.setCreatedDate(LocalDateTime.now());
        user.setUserInformation(userInformation);
        return user;
    }

    public static UserInformation prepareUserInformation() {
        UserInformation userInformation = new UserInformation();
        userInformation.setId(RANDOM.nextLong());
        userInformation.setFirstName(RandomStringUtils.randomAlphabetic(10));
        userInformation.setLastName(RandomStringUtils.randomAlphabetic(10));
        userInformation.setPhone(RandomStringUtils.randomAlphabetic(15));
        userInformation.setEmail(RandomStringUtils.randomAlphabetic(15));
        userInformation.setStatus(RandomStringUtils.randomAlphabetic(10));
        userInformation.setAvatarUrl(RandomStringUtils.randomAlphabetic(10));
        userInformation.setBirthDate(LocalDate.now());
        userInformation.setPosition(preparePosition());
        userInformation.setDepartment(prepareDepartment());
        return userInformation;
    }

    public static List<Participant> prepareParticipants() {
        return Arrays.asList(prepareParticipant(), prepareParticipant());
    }

    public static Participant prepareParticipant() {
        Participant participant = new Participant();
        ParticipantPk primaryKey = new ParticipantPk();
        primaryKey.setConversation(prepareConversation());
        primaryKey.setUser(prepareUser());
        participant.setPrimaryKey(primaryKey);
        participant.setType(ParticipantType.COMMON);
        return participant;
    }

    public static Conversation prepareConversation() {
        Conversation conversation = new Conversation();
        conversation.setId(RANDOM.nextLong());
        conversation.setCreator(prepareUser());
        conversation.setPhotoUrl(RandomStringUtils.randomAlphabetic(10));
        conversation.setType(ConversationType.PRIVATE);
        conversation.setCreatedDate(LocalDateTime.now());
        conversation.setLastMessageDate(LocalDateTime.now());
        return conversation;
    }

    public static Position preparePosition() {
        Position position = new Position();
        position.setId(RANDOM.nextLong());
        position.setNameTranslationCode(RandomStringUtils.randomAlphabetic(10));
        return position;
    }

    public static Department prepareDepartment() {
        Department department = new Department();
        department.setId(RANDOM.nextLong());
        department.setNameTranslationCode(RandomStringUtils.randomAlphabetic(10));
        department.setShortNameTranslationCode(RandomStringUtils.randomAlphabetic(10));
        return department;
    }
}
