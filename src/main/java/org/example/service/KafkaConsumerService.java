package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Message;
import org.example.model.Record;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
  private final ObjectMapper objectMapper;
  private final RecordsService recordsService;

  public KafkaConsumerService(ObjectMapper objectMapper, RecordsService recordsService) {
    this.objectMapper = objectMapper;
    this.recordsService = recordsService;
  }

  @SneakyThrows
  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void consumeMessage(String message) {
    Message parsedMessage = objectMapper.readValue(message, Message.class);
    log.info("Retrieved message {}", message);
    recordsService.insertRecord(new Record(parsedMessage.getUserId(), parsedMessage.getTimestamp(),
        parsedMessage.getActionType(), parsedMessage.getDetails()));
  }
}
