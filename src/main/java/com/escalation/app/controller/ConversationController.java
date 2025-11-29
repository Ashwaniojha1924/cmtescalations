package com.escalation.app.controller;

import com.escalation.app.entity.Conversation;
import com.escalation.app.entity.Message;
import com.escalation.app.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversationController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;

    @PostMapping("/escalations/{escalationId}/conversations")
    public Conversation createConversation(
            @PathVariable Long escalationId,
            @RequestBody Conversation conversation) {
        logger.info("Creating conversation for escalation id: {}", escalationId);
        Conversation created = conversationService.createConversation(escalationId, conversation);
        logger.info("Successfully created conversation with id: {} for escalation id: {}", created.getConversationId(), escalationId);
        return created;
    }

    @GetMapping("/escalations/{escalationId}/conversations")
    public List<Conversation> getConversations(@PathVariable Long escalationId) {
        logger.info("Fetching conversations for escalation id: {}", escalationId);
        List<Conversation> conversations = conversationService.getConversationsForEscalation(escalationId);
        logger.debug("Retrieved {} conversations for escalation id: {}", conversations.size(), escalationId);
        return conversations;
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public Message addMessage(
            @PathVariable Long conversationId,
            @RequestBody Message message) {
        logger.info("Adding message to conversation id: {}", conversationId);
        Message saved = conversationService.addMessage(conversationId, message);
        logger.info("Successfully added message with id: {} to conversation id: {}", saved.getMessageId(), conversationId);
        return saved;
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<Message> getMessages(@PathVariable Long conversationId) {
        logger.info("Fetching messages for conversation id: {}", conversationId);
        List<Message> messages = conversationService.getMessages(conversationId);
        logger.debug("Retrieved {} messages for conversation id: {}", messages.size(), conversationId);
        return messages;
    }
}
