package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Application;
import org.example.ApplicationConfig;
import org.example.CassandraDriverConfigLoaderBuilderCustomizer;
import org.example.model.Action;
import org.example.model.Message;
import org.example.model.Record;
import org.example.service.KafkaConsumerService;
import org.example.service.RecordsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(
    classes = {KafkaConsumerService.class, Application.class, ApplicationConfig.class,
        CassandraDriverConfigLoaderBuilderCustomizer.class},
    properties = {
        "topic-to-consume-message=audit-topic",
        "spring.kafka.consumer.group-id=audit-group",
        "spring.kafka.consumer.auto-offset-reset=earliest"
    }
)
@Import({KafkaAutoConfiguration.class})
@Testcontainers
class KafkaConsumerServiceTest {
  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
  @Container
  private static final CassandraContainer<?> cassandraContainer =
      new CassandraContainer<>("cassandra:3.11.2")
          .withExposedPorts(9042);
  @Autowired
  private RecordsService recordsService;
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  @Autowired
  private ObjectMapper objectMapper;

  @DynamicPropertySource
  static void cassandraProperties(DynamicPropertyRegistry registry) {
    String contactPoint =
        cassandraContainer.getHost() + ":" + cassandraContainer.getMappedPort(9042);
    registry.add("spring.cassandra.contact-points", () -> contactPoint);
    registry.add("spring.cassandra.local-datacenter", () -> "datacenter1");
    registry.add("spring.cassandra.keyspace-name", () -> "my_keyspace");
  }

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws Exception {
    Message message = new Message(1L, Instant.now(), Action.INSERT, "Testing");
    Record expectedRecord = new Record(message);
    kafkaTemplate.send("audit-topic", objectMapper.writeValueAsString(message));

    await().atMost(Duration.ofSeconds(10))
        .pollDelay(Duration.ofMillis(500))
        .untilAsserted(() -> {
              Record recievedRecord = recordsService.getRecordById(1L);
              assertEquals(expectedRecord.getUser_id(), recievedRecord.getUser_id());
              assertEquals(expectedRecord.getEvent_type(), recievedRecord.getEvent_type());
              assertTrue(
                  expectedRecord.getEvent_time().isAfter(recievedRecord.getEvent_time()));
              assertEquals(expectedRecord.getEvent_details(), recievedRecord.getEvent_details());
            }
        );
  }
}
