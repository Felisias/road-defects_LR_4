package com.roaddefects.service;

import com.roaddefects.dto.DefectDto;
import com.roaddefects.dto.DefectForm;
import com.roaddefects.entity.ChangeType;
import com.roaddefects.entity.Defect;
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
public class DefectService {

    private static final String ENTITY = "Defect";

    private final DefectRepository defectRepository;
    private final DefectTypeRepository typeRepository;
    private final ApplicationEventPublisher events;

    public DefectService(DefectRepository defectRepository, DefectTypeRepository typeRepository,
                         ApplicationEventPublisher events) {
        this.defectRepository = defectRepository;
        this.typeRepository = typeRepository;
        this.events = events;
    }

    public List<DefectDto> findAll() {
        return defectRepository.findAllWithType().stream()
                .map(DefectService::toDto)
                .toList();
    }

    public List<DefectDto> findByType(String typeCode) {
        if (!typeRepository.existsById(typeCode)) {
            throw new NotFoundException("Тип «" + typeCode + "» не найден.");
        }
        return defectRepository.findByTypeWithType(typeCode).stream()
                .map(DefectService::toDto)
                .toList();
    }

    public DefectDto get(Long id) {
        return defectRepository.findById(id)
                .map(DefectService::toDto)
                .orElseThrow(() -> new NotFoundException("Дефект с id " + id + " не найден."));
    }

    public Optional<DefectForm> findForm(Long id) {
        return defectRepository.findById(id).map(DefectService::toForm);
    }

    @Transactional
    public DefectDto save(DefectForm form) {
        DefectType type = typeRepository.findById(form.getTypeCode())
                .orElseThrow(() -> new BusinessException("Тип «" + form.getTypeCode() + "» не существует."));

        boolean isNew = form.getId() == null;
        Defect defect;
        if (isNew) {
            defect = new Defect();
        } else {
            defect = defectRepository.findById(form.getId())
                    .orElseThrow(() -> new NotFoundException("Дефект с id " + form.getId() + " не найден."));
        }

        defect.setType(type);
        defect.setLatitude(form.getLatitude());
        defect.setLongitude(form.getLongitude());
        defect.setDetectedAt(form.getDetectedAt());
        DefectDto saved = toDto(defectRepository.save(defect));

        events.publishEvent(new EntityChangedEvent(isNew ? ChangeType.INSERT : ChangeType.UPDATE,
                ENTITY, String.valueOf(saved.getId()), saved));
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Defect defect = defectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Дефект с id " + id + " не найден."));
        DefectDto deleted = toDto(defect);
        defectRepository.delete(defect);
        events.publishEvent(new EntityChangedEvent(ChangeType.DELETE, ENTITY, String.valueOf(id), deleted));
    }

    private static DefectDto toDto(Defect d) {
        return new DefectDto(d.getId(), d.getType().getCode(), d.getType().getName(),
                d.getLatitude(), d.getLongitude(), d.getDetectedAt());
    }

    private static DefectForm toForm(Defect d) {
        DefectForm form = new DefectForm();
        form.setId(d.getId());
        form.setTypeCode(d.getType().getCode());
        form.setLatitude(d.getLatitude());
        form.setLongitude(d.getLongitude());
        form.setDetectedAt(d.getDetectedAt());
        return form;
    }
}
