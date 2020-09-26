package kz.cmessage.core.conversation.controller;

import kz.cmessage.core.common.dto.ResponseDto;
import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.dto.CreateConversationRequestDto;
import kz.cmessage.core.conversation.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<?> getUserConversations() {
        return ResponseEntity.ok(conversationService.getUserConversations());
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return conversationService.getUserConversationById(id)
                .map((conversation -> ResponseEntity.ok().body(conversation)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateConversationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversationService.create(dto));
    }

    @PutMapping
    @RequestMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid ConversationDto dto) {
        return ResponseEntity.ok(conversationService.update(id, dto));
    }
}
