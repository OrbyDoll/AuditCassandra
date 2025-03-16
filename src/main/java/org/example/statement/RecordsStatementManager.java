package org.example.statement;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;

@Component
public class RecordsStatementManager {
  public PreparedStatement getInsertStatement(CqlSession session) {
    return session.prepare(
        "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type, event_details)" +
            "VALUES (?, ?, ?, ?)"
    );
  }

  public PreparedStatement getSelectStatement(CqlSession session) {
    return session.prepare(
        "SELECT * FROM my_keyspace.user_audit WHERE user_id = ?"
    );
  }
}
