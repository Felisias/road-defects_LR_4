package com.roaddefects.service;

import com.roaddefects.dto.ChangeLogDto;
import com.roaddefects.entity.ChangeLog;
import com.roaddefects.repository.ChangeLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChangeLogService {

    private final ChangeLogRepository repository;

    public ChangeLogService(ChangeLogRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ChangeLogDto> findLatest() {
        return repository.findTop100ByOrderByIdDesc().stream()
                .map(e -> new ChangeLogDto(e.getId(), e.getChangedAt(), e.getChangeType().name(),
                        e.getEntity(), e.getEntityId(), e.getData()))
                .toList();
    }

    @Transactional
    public void save(ChangeLog entry) {
        repository.save(entry);
    }
}
