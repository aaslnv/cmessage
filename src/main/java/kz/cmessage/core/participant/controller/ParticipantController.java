package kz.cmessage.core.participant.controller;

import kz.cmessage.core.common.dto.ResponseDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.participant.dto.ParticipantDto;
import kz.cmessage.core.participant.service.ParticipantService;
import kz.cmessage.core.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/conversations/{conversationId}/participants")
public class ParticipantController {

    private ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> getAll(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation) {
        List<ParticipantDto> responseData = participantService.getAllNotLeftByConversation(conversation);
        ResponseDto<?> responseDto = new ResponseDto<>(responseData);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping
    @RequestMapping("/{userId}")
    public ResponseEntity<ResponseDto<?>> getByUser(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                    @PathVariable(value = "userId") @Valid @NotNull User user) {
        ResponseDto<?> responseDto = participantService.getByConversationAndUser(conversation, user)
                .map(ResponseDto::new)
                .orElse(new ResponseDto<>(true, HttpStatus.NOT_FOUND));
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping
    public ResponseEntity<ResponseDto<?>> addParticipants(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                          @RequestBody @Valid @NotEmpty Set<Long> userIds) {
        List<ParticipantDto> responseData = participantService.addParticipants(conversation, userIds);
        ResponseDto<?> responseDto = new ResponseDto<>(responseData, HttpStatus.CREATED);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PutMapping
    @RequestMapping("/{userId}")
    public ResponseEntity<ResponseDto<?>> update(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                 @PathVariable(value = "userId") @Valid @NotNull User user,
                                                 @RequestBody @Valid ParticipantDto dto) {
        ParticipantDto responseData = participantService.update(conversation, user, dto);
        ResponseDto<?> responseDto = new ResponseDto<>(responseData, HttpStatus.OK);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @DeleteMapping
    @RequestMapping("/{userId}")
    public ResponseEntity<ResponseDto<?>> leave(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                @PathVariable(value = "userId") @Valid @NotNull User user) {
        participantService.leaveByConversationAndUser(conversation, user);
        ResponseDto<?> responseDto = new ResponseDto<>(true, HttpStatus.OK);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PatchMapping
    @RequestMapping("/{userId}/setAdmin")
    public ResponseEntity<ResponseDto<?>> setAdmin(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                   @PathVariable(value = "userId") @Valid @NotNull User user) {
        participantService.switchIsAdmin(conversation, user, true);
        ResponseDto<?> responseDto = new ResponseDto<>(true, HttpStatus.OK);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PatchMapping
    @RequestMapping("/{userId}/setNotAdmin")
    public ResponseEntity<ResponseDto<?>> setNotAdmin(@PathVariable(value = "conversationId") @Valid @NotNull Conversation conversation,
                                                      @PathVariable(value = "userId") @Valid @NotNull User user) {
        participantService.switchIsAdmin(conversation, user, false);
        ResponseDto<?> responseDto = new ResponseDto<>(true, HttpStatus.OK);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
