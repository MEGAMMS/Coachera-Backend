package com.coachera.backend.service;

import com.coachera.backend.dto.MaterialDTO;
import com.coachera.backend.entity.Material;
import com.coachera.backend.entity.Section;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.MaterialRepository;
import com.coachera.backend.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final SectionRepository sectionRepository;

    public MaterialService(MaterialRepository materialRepository, SectionRepository sectionRepository) {
        this.materialRepository = materialRepository;
        this.sectionRepository = sectionRepository;
    }

    public MaterialDTO createMaterial(Integer sectionId, MaterialDTO materialDTO) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        validateMaterialOrderIndexUniqueness(sectionId, materialDTO.getOrderIndex(), null);

        Material material = new Material();
        material.setSection(section);
        material.setTitle(materialDTO.getTitle());
        material.setOrderIndex(materialDTO.getOrderIndex());

        Material savedMaterial = materialRepository.save(material);
        return new MaterialDTO(savedMaterial);
    }

    public MaterialDTO updateMaterial(Integer materialId, MaterialDTO materialDTO) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));

        validateMaterialOrderIndexUniqueness(material.getSection().getId(), materialDTO.getOrderIndex(), materialId);

        material.setTitle(materialDTO.getTitle());
        material.setOrderIndex(materialDTO.getOrderIndex());

        Material updatedMaterial = materialRepository.save(material);
        return new MaterialDTO(updatedMaterial);
    }

    @Transactional(readOnly = true)
    public MaterialDTO getMaterialById(Integer materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        return new MaterialDTO(material);
    }

    @Transactional(readOnly = true)
    public List<MaterialDTO> getAllMaterialsBySectionId(Integer sectionId) {
        if (!sectionRepository.existsById(sectionId)) {
            throw new ResourceNotFoundException("Section not found with id: " + sectionId);
        }

        return materialRepository.findBySectionIdOrderByOrderIndexAsc(sectionId).stream()
                .map(MaterialDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteMaterial(Integer materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found with id: " + materialId));
        materialRepository.delete(material);
    }

    private void validateMaterialOrderIndexUniqueness(Integer sectionId, Integer orderIndex, Integer excludeMaterialId) {
        boolean orderIndexExists = excludeMaterialId == null
                ? materialRepository.existsBySectionIdAndOrderIndex(sectionId, orderIndex)
                : materialRepository.existsBySectionIdAndOrderIndexAndIdNot(sectionId, orderIndex, excludeMaterialId);

        if (orderIndexExists) {
            throw new DuplicateOrderIndexException(
                    "Order index " + orderIndex + " already exists in this section");
        }
    }
}