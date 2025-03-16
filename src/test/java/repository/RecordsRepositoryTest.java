package repository;

import org.example.Application;
import org.example.model.Action;
import org.example.model.Record;
import org.example.repository.RecordsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.cassandra.CassandraInvalidQueryException;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
@Testcontainers
public class RecordsRepositoryTest {
  @Container
  private static final CassandraContainer<?> cassandraContainer =
      new CassandraContainer<>("cassandra:3.11.2")
          .withExposedPorts(9042);
  @Autowired
  private RecordsRepository recordsRepository;

  @Test
  @DisplayName("Тест на успешное добавление записи")
  void test1() {
    Record record = new Record(
        UUID.randomUUID(),
        new Timestamp(System.currentTimeMillis()),
        Action.INSERT,
        "Имитация вставки от пользователя"
    );
    recordsRepository.save(record);
    Record desiredRecord = recordsRepository.findByUserId(record.getUser_id());
    assertEquals(record.getUser_id(), desiredRecord.getUser_id());
    assertEquals(record.getEvent_details(), desiredRecord.getEvent_details());
    assertEquals(record.getEvent_time(), desiredRecord.getEvent_time());
    assertEquals(record.getEvent_type(), desiredRecord.getEvent_type());
  }

  @Test
  @DisplayName("Тест на неудачное добавление записи")
  void test2() {
    Record record = new Record(
        null,
        new Timestamp(System.currentTimeMillis()),
        Action.INSERT,
        "Имитация вставки от пользователя"
    );
    assertThrows(CassandraInvalidQueryException.class, () -> recordsRepository.save(record));
  }

  @Test
  @DisplayName("Тест на успешное получение записи")
  void test3() {
    Record record = new Record(
        UUID.randomUUID(),
        new Timestamp(System.currentTimeMillis()),
        Action.INSERT,
        "Имитация вставки от пользователя"
    );
    recordsRepository.save(record);
    Record desiredRecord = recordsRepository.findByUserId(record.getUser_id());
    assertEquals(record.getUser_id(), desiredRecord.getUser_id());
    assertEquals(record.getEvent_details(), desiredRecord.getEvent_details());
    assertEquals(record.getEvent_time(), desiredRecord.getEvent_time());
    assertEquals(record.getEvent_type(), desiredRecord.getEvent_type());
  }

  @Test
  @DisplayName("Тест на неудачное получение записи")
  void test4() {
    assertNull(recordsRepository.findByUserId(UUID.randomUUID()));
  }
}
