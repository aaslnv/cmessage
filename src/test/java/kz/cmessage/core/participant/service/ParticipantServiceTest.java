package kz.cmessage.core.participant.service;

import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.exception.IllegalAccessException;
import kz.cmessage.core.participant.model.Participant;
import kz.cmessage.core.participant.repository.ParticipantRepository;
import kz.cmessage.core.user.model.User;
import kz.cmessage.core.user.repository.UserRepository;
import kz.cmessage.core.util.SessionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static kz.cmessage.core.util.TestUtil.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private SessionUtil sessionUtil;
    @Mock
    private ParticipantValidationService participantValidationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ParticipantMapperService participantMapperService;

    @Test
    public void test_getAllNotLeftByConversation_sessionUserIsParticipant() {
        User sessionUser = prepareUser();
        Conversation conversation = prepareConversation();
        List<Participant> participants = prepareParticipants();

        when(sessionUtil.getSession().getUser()).thenReturn(sessionUser);
        when(participantValidationService.isUserNotLeftParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId()))
                .thenReturn(true);
        when(participantRepository.findAllByLeftIsFalseAndPrimaryKeyConversationId(conversation.getId()))
                .thenReturn(participants);
        when(participantMapperService.toDto(any(Participant.class))).thenReturn(null);

        participantService.getAllNotLeftByConversation(conversation);

        verify(participantRepository, times(1)).findAllByLeftIsFalseAndPrimaryKeyConversationId(conversation.getId());
        verify(participantMapperService, times(participants.size())).toDto(any(Participant.class));
    }

    @Test(expected = IllegalAccessException.class)
    public void test_getAllNotLeftByConversation_sessionUserIsNotParticipant() {
        User sessionUser = prepareUser();
        Conversation conversation = prepareConversation();

        when(sessionUtil.getSession().getUser()).thenReturn(sessionUser);
        when(participantValidationService.isUserNotLeftParticipantByConversationIdAndUserId(conversation.getId(), sessionUser.getId()))
                .thenReturn(false);

        participantService.getAllNotLeftByConversation(conversation);
    }
}
