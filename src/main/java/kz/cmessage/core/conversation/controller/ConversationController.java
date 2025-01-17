package kz.cmessage.core.conversation.controller;

import kz.cmessage.core.common.dto.ResponseDto;
import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.dto.CreateConversationRequestDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<ResponseDto<?>> getAll() {
        List<ConversationDto> responseData = conversationService.getUserConversations();
        ResponseDto<?> responseDto = new ResponseDto<>(responseData);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> getById(@PathVariable Long id) {
        ResponseDto<?> responseDto = conversationService.getUserConversationById(id)
                .map(ResponseDto::new)
                .orElse(new ResponseDto<>(true, HttpStatus.NOT_FOUND));
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping
    public ResponseEntity<ResponseDto<?>> create(@RequestBody @Valid CreateConversationRequestDto dto) {
        ConversationDto responseData = conversationService.create(dto);
        ResponseDto<?> responseDto = new ResponseDto<>(responseData);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<ResponseDto<?>> update(@PathVariable(value = "id") @Valid @NotNull Conversation conversation,
                                                 @RequestBody @Valid ConversationDto dto) {
        ConversationDto responseData = conversationService.update(conversation, dto);
        ResponseDto<?> responseDto = new ResponseDto<>(responseData);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
