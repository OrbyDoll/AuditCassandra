package org.example.repository;

import org.example.model.Record;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordsRepository extends CassandraRepository<Record, String> {
  @Query("SELECT * FROM user_audit WHERE user_id = ?0")
  Record findByUserId(Long userId);
}
