package com.roaddefects.service;

import com.roaddefects.dto.DefectTypeDto;
import com.roaddefects.entity.ChangeType;
import com.roaddefects.entity.DefectType;
import com.roaddefects.jms.EntityChangedEvent;
import com.roaddefects.repository.DefectRepository;
import com.roaddefects.repository.DefectTypeRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DefectTypeService {

    private static final String ENTITY = "DefectType";

    private final DefectTypeRepository typeRepository;
    private final DefectRepository defectRepository;
    private final ApplicationEventPublisher events;

    public DefectTypeService(DefectTypeRepository typeRepository, DefectRepository defectRepository,
                             ApplicationEventPublisher events) {
        this.typeRepository = typeRepository;
        this.defectRepository = defectRepository;
        this.events = events;
    }

    public List<DefectTypeDto> findAll() {
        return typeRepository.findAllByOrderByCodeAsc().stream()
                .map(DefectTypeService::toDto)
                .toList();
    }

    public Optional<DefectTypeDto> findByCode(String code) {
        return typeRepository.findById(code).map(DefectTypeService::toDto);
    }

    public DefectTypeDto get(String code) {
        return findByCode(code)
                .orElseThrow(() -> new NotFoundException("Тип «" + code + "» не найден."));
    }

    @Transactional
    public DefectTypeDto create(DefectTypeDto dto) {
        if (typeRepository.existsById(dto.getCode())) {
            throw new BusinessException("Тип с кодом «" + dto.getCode() + "» уже существует.");
        }
        DefectTypeDto created = toDto(typeRepository.save(new DefectType(dto.getCode(), dto.getName())));
        events.publishEvent(new EntityChangedEvent(ChangeType.INSERT, ENTITY, created.getCode(), created));
        return created;
    }

    @Transactional
    public DefectTypeDto update(DefectTypeDto dto) {
        DefectType type = typeRepository.findById(dto.getCode())
                .orElseThrow(() -> new NotFoundException("Тип «" + dto.getCode() + "» не найден."));
        type.setName(dto.getName());
        DefectTypeDto updated = toDto(type);
        events.publishEvent(new EntityChangedEvent(ChangeType.UPDATE, ENTITY, updated.getCode(), updated));
        return updated;
    }

    @Transactional
    public void delete(String code) {
        DefectType type = typeRepository.findById(code)
                .orElseThrow(() -> new NotFoundException("Тип «" + code + "» не найден."));
        long used = defectRepository.countByType_Code(code);
        if (used > 0) {
            throw new BusinessException("Нельзя удалить тип «" + code + "»: к нему относится дефектов: " + used + ".");
        }
        DefectTypeDto deleted = toDto(type);
        typeRepository.delete(type);
        events.publishEvent(new EntityChangedEvent(ChangeType.DELETE, ENTITY, code, deleted));
    }

    private static DefectTypeDto toDto(DefectType type) {
        return new DefectTypeDto(type.getCode(), type.getName());
    }
}
