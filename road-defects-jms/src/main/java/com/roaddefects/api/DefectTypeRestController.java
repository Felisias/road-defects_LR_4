package com.roaddefects.api;

import com.roaddefects.dto.DefectListDto;
import com.roaddefects.dto.DefectTypeDto;
import com.roaddefects.dto.DefectTypeListDto;
import com.roaddefects.service.DefectService;
import com.roaddefects.service.DefectTypeService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/types")
public class DefectTypeRestController {

    private final DefectTypeService typeService;
    private final DefectService defectService;
    private final Validator validator;

    public DefectTypeRestController(DefectTypeService typeService, DefectService defectService, Validator validator) {
        this.typeService = typeService;
        this.defectService = defectService;
        this.validator = validator;
    }

    @GetMapping
    public DefectTypeListDto list() {
        return new DefectTypeListDto(typeService.findAll());
    }

    @GetMapping("/{code}")
    public DefectTypeDto get(@PathVariable("code") String code) {
        return typeService.get(code);
    }

    @GetMapping("/{code}/defects")
    public DefectListDto defects(@PathVariable("code") String code) {
        return new DefectListDto(code, defectService.findByType(code));
    }

    @PostMapping
    public ResponseEntity<DefectTypeDto> create(@Valid @RequestBody DefectTypeDto dto) {
        DefectTypeDto created = typeService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}").buildAndExpand(created.getCode()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{code}")
    public DefectTypeDto update(@PathVariable("code") String code, @RequestBody DefectTypeDto dto) {
        dto.setCode(code);
        Set<ConstraintViolation<DefectTypeDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return typeService.update(dto);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable("code") String code) {
        typeService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
