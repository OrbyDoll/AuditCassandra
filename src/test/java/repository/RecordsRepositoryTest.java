package repository;
import org.example.Application;
import org.example.model.Action;
import org.example.model.Record;
import org.example.repository.RecordsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Timestamp;
import java.util.UUID;

@SpringBootTest( classes = Application.class)
@Testcontainers
public class RecordsRepositoryTest {
  @Autowired
  private RecordsRepository recordsRepository;

  @Container
  private static final CassandraContainer<?> cassandraContainer = new CassandraContainer<>("cassandra:3.11.2")
      .withExposedPorts(9042);
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
    System.out.println(recordsRepository.findById(String.valueOf(record.getUser_id())));
  }
}
