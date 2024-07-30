package com.example.chat;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.bmc.generativeaiinference.responses.ChatResponse;
import com.oracle.cloud.spring.genai.ChatModel;
import com.oracle.cloud.spring.queue.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatModel chatModel;
    private final Queue queue;

    @Value("${myQueueId}")
    private String queueId;

    public ChatController(ChatModel chatModel, Queue queue) {
        this.chatModel = chatModel;
        this.queue = queue;
    }

    @PostMapping
    ResponseEntity<String> chat(@RequestBody String question) {
        ChatResponse response = chatModel.chat(question);
        String answer = chatModel.extractText(response);
        sendMessage(question + "\n\n---\n\n" + answer);
        return ResponseEntity.ok().body(answer);
    }

    private void sendMessage(String message) {
        // post the message
        queue.putMessages(queueId, new String[] {message});
    }
    
}
