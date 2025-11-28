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
        try {
            Conversation created = conversationService.createConversation(escalationId, conversation);
            logger.info("Successfully created conversation with id: {} for escalation id: {}", created.getConversationId(), escalationId);
            return created;
        } catch (Exception e) {
            logger.error("Error creating conversation for escalation id: {}", escalationId, e);
            throw e;
        }
    }

    @GetMapping("/escalations/{escalationId}/conversations")
    public List<Conversation> getConversations(@PathVariable Long escalationId) {
        logger.info("Fetching conversations for escalation id: {}", escalationId);
        try {
            List<Conversation> conversations = conversationService.getConversationsForEscalation(escalationId);
            logger.debug("Retrieved {} conversations for escalation id: {}", conversations.size(), escalationId);
            return conversations;
        } catch (Exception e) {
            logger.error("Error fetching conversations for escalation id: {}", escalationId, e);
            throw e;
        }
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public Message addMessage(
            @PathVariable Long conversationId,
            @RequestBody Message message) {
        logger.info("Adding message to conversation id: {}", conversationId);
        try {
            Message saved = conversationService.addMessage(conversationId, message);
            logger.info("Successfully added message with id: {} to conversation id: {}", saved.getMessageId(), conversationId);
            return saved;
        } catch (Exception e) {
            logger.error("Error adding message to conversation id: {}", conversationId, e);
            throw e;
        }
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<Message> getMessages(@PathVariable Long conversationId) {
        logger.info("Fetching messages for conversation id: {}", conversationId);
        try {
            List<Message> messages = conversationService.getMessages(conversationId);
            logger.debug("Retrieved {} messages for conversation id: {}", messages.size(), conversationId);
            return messages;
        } catch (Exception e) {
            logger.error("Error fetching messages for conversation id: {}", conversationId, e);
            throw e;
        }
    }
}
