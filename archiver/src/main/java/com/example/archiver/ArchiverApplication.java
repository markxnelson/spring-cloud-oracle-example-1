package com.example.archiver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.oracle.bmc.queue.model.GetMessage;
import com.oracle.bmc.queue.responses.GetMessagesResponse;
import com.oracle.cloud.spring.queue.Queue;

@SpringBootApplication
public class ArchiverApplication implements CommandLineRunner {

	@Value("${myQueueId}")
	private String queueId;

	@Autowired
	Queue queue;

	@Autowired
	MessageRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ArchiverApplication.class, args);
	}

	public void run(String... args) {
		while (true) {
			// look for messages
			GetMessagesResponse response = queue.getMessages(queueId, 60, 10, 10);

			// loop through messages
			List<GetMessage> messages = response.getGetMessages().getMessages();
			for (GetMessage message : messages) {
				String content = message.getContent();
				repository.saveAndFlush(new Message(null, content));
			}
			
			try {
				Thread.sleep(10_000);
			} catch (InterruptedException ignore) {};
		}
	}

}
