package kz.cmessage.core.message.controller;

import kz.cmessage.core.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversations/${conversationId}/messages")
public class MessageController {

    private MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messageService.getAllByConversationId(conversationId));
    }

    @GetMapping
    public ResponseEntity<?> getLastByFromNumberAndCount(@PathVariable Long conversationId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                                         @RequestParam Integer count) {
        return ResponseEntity.ok(messageService.getLastByConversationIdAndFromAndCount(conversationId, from, count));
    }
}
