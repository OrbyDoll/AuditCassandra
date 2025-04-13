package org.example.service;

import lombok.AllArgsConstructor;
import org.example.model.Record;
import org.example.repository.RecordsRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RecordsService {
  private RecordsRepository recordsRepository;

  public void insertRecord(Record record) {
    recordsRepository.save(record);
  }

  public Record getRecordById(Long id) {
    return recordsRepository.findByUserId(id);
  }
}
