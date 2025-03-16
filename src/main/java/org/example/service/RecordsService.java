package org.example.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AllArgsConstructor;
import org.example.model.Record;
import org.example.repository.RecordsRepository;
import org.example.statement.RecordsStatementManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class RecordsService {
  private RecordsRepository recordsRepository;

  @Autowired
  private RecordsStatementManager recordsStatementManager;

  @Autowired
  private CqlSession session;

  public void insertRecord(Record record) {
    BoundStatement insertStatement = recordsStatementManager.getInsertStatement(session).bind(
        record.getUser_id(),
        record.getEvent_time(),
        record.getEvent_type().toString(),
        record.getEvent_details()
    );
  }

  public List<Row> getRecordsById(UUID id) {
    BoundStatement selectStatement = recordsStatementManager.getSelectStatement(session).bind(id);
    ResultSet resultSet = session.execute(selectStatement);
    ArrayList<Row> rows = new ArrayList<>();
    for (Row row : resultSet) {
      rows.add(row);
    }
    return rows;
  }
}
