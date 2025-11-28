package com.escalation.app.service;

import com.escalation.app.entity.Conversation;
import com.escalation.app.entity.Escalation;
import com.escalation.app.entity.Message;
import com.escalation.app.repository.ConversationRepository;
import com.escalation.app.repository.EscalationRepository;
import com.escalation.app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private static final Logger logger = LoggerFactory.getLogger(ConversationService.class);

    private final ConversationRepository conversationRepository;
    private final EscalationRepository escalationRepository;
    private final MessageRepository messageRepository;
    private final AuditService auditService;

    public Conversation createConversation(Long escalationId, Conversation conversation) {
        logger.debug("Creating conversation for escalation id: {}", escalationId);
        Escalation escalation = escalationRepository.findById(escalationId)
                .orElseThrow(() -> {
                    logger.error("Escalation not found with id: {}", escalationId);
                    return new RuntimeException("Escalation not found");
                });

        conversation.setEscalation(escalation);
        Conversation saved = conversationRepository.save(conversation);
        logger.debug("Saved conversation with id: {} for escalation id: {}", saved.getConversationId(), escalationId);

        auditService.log(escalationId, "SYSTEM", null,
                "Conversation Created", saved);

        logger.info("Successfully created conversation with id: {} for escalation id: {}", saved.getConversationId(), escalationId);
        return saved;
    }

    public List<Conversation> getConversationsForEscalation(Long escalationId) {
        logger.debug("Fetching conversations for escalation id: {}", escalationId);
        List<Conversation> conversations = conversationRepository.findAll().stream()
                .filter(c -> c.getEscalation().getEscalationId().equals(escalationId))
                .toList();
        logger.debug("Found {} conversations for escalation id: {}", conversations.size(), escalationId);
        return conversations;
    }

    public Message addMessage(Long conversationId, Message message) {
        logger.debug("Adding message to conversation id: {}", conversationId);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    logger.error("Conversation not found with id: {}", conversationId);
                    return new RuntimeException("Conversation not found");
                });

        message.setConversation(conversation);
        Message saved = messageRepository.save(message);
        logger.debug("Saved message with id: {} to conversation id: {}", saved.getMessageId(), conversationId);

        Long escalationId = conversation.getEscalation().getEscalationId();
        Integer actorId = message.getSenderId() != null ? message.getSenderId().intValue() : null;

        auditService.log(escalationId, "USER",
                actorId, "Message Added", saved);

        logger.info("Successfully added message with id: {} to conversation id: {}", saved.getMessageId(), conversationId);
        return saved;
    }

    public List<Message> getMessages(Long conversationId) {
        logger.debug("Fetching messages for conversation id: {}", conversationId);
        List<Message> messages = messageRepository.findAll().stream()
                .filter(m -> m.getConversation().getConversationId().equals(conversationId))
                .toList();
        logger.debug("Found {} messages for conversation id: {}", messages.size(), conversationId);
        return messages;
    }
}
