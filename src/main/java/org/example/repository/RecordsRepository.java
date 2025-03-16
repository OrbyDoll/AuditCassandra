package org.example.repository;

import org.example.model.Record;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordsRepository extends CassandraRepository<Record, String> {}
