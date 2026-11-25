package com.roaddefects.api;

import com.roaddefects.dto.DefectDto;
import com.roaddefects.dto.DefectForm;
import com.roaddefects.dto.DefectListDto;
import com.roaddefects.service.DefectService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/defects")
public class DefectRestController {

    private final DefectService defectService;

    public DefectRestController(DefectService defectService) {
        this.defectService = defectService;
    }

    @GetMapping
    public DefectListDto list() {
        return new DefectListDto(null, defectService.findAll());
    }

    @GetMapping("/{id}")
    public DefectDto get(@PathVariable("id") Long id) {
        return defectService.get(id);
    }

    @PostMapping
    public ResponseEntity<DefectDto> create(@Valid @RequestBody DefectForm form) {
        form.setId(null);
        DefectDto created = defectService.save(form);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public DefectDto update(@PathVariable("id") Long id, @Valid @RequestBody DefectForm form) {
        form.setId(id);
        return defectService.save(form);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        defectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
