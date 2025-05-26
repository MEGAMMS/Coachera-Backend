package com.coachera.backend.service;

import com.coachera.backend.dto.SectionDTO;
import com.coachera.backend.entity.Section;
import com.coachera.backend.entity.Week;
import com.coachera.backend.exception.DuplicateOrderIndexException;
import com.coachera.backend.exception.ResourceNotFoundException;
import com.coachera.backend.repository.SectionRepository;
import com.coachera.backend.repository.WeekRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final WeekRepository weekRepository;

    public SectionService(SectionRepository sectionRepository, WeekRepository weekRepository) {
        this.sectionRepository = sectionRepository;
        this.weekRepository = weekRepository;
    }

    
    public SectionDTO createSection(Integer weekId, SectionDTO sectionDTO) {
        Week week = weekRepository.findById(weekId)
                .orElseThrow(() -> new ResourceNotFoundException("Week not found with id: " + weekId));

        validateSectionOrderIndexUniqueness(weekId, sectionDTO.getOrderIndex(), null);

        Section section = new Section();
        section.setWeek(week);
        section.setTitle(sectionDTO.getTitle());
        section.setOrderIndex(sectionDTO.getOrderIndex());

        Section savedSection = sectionRepository.save(section);
        return new SectionDTO(savedSection);
    }

    public SectionDTO updateSection(Integer sectionId, SectionDTO sectionDTO) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));

        validateSectionOrderIndexUniqueness(section.getWeek().getId(), sectionDTO.getOrderIndex(), sectionId);

        section.setTitle(sectionDTO.getTitle());
        section.setOrderIndex(sectionDTO.getOrderIndex());

        Section updatedSection = sectionRepository.save(section);
        return new SectionDTO(updatedSection);
    }

    @Transactional(readOnly = true)
    public SectionDTO getSectionById(Integer sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        return new SectionDTO(section);
    }

    @Transactional(readOnly = true)
    public List<SectionDTO> getAllSectionsByWeekId(Integer weekId) {
        if (!weekRepository.existsById(weekId)) {
            throw new ResourceNotFoundException("Week not found with id: " + weekId);
        }

        return sectionRepository.findByWeekIdOrderByOrderIndexAsc(weekId).stream()
                .map(SectionDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteSection(Integer sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with id: " + sectionId));
        sectionRepository.delete(section);
    }

    private void validateSectionOrderIndexUniqueness(Integer weekId, Integer orderIndex, Integer excludeSectionId) {
        boolean orderIndexExists = excludeSectionId == null
                ? sectionRepository.existsByWeekIdAndOrderIndex(weekId, orderIndex)
                : sectionRepository.existsByWeekIdAndOrderIndexAndIdNot(weekId, orderIndex, excludeSectionId);

        if (orderIndexExists) {
            throw new DuplicateOrderIndexException(
                    "Order index " + orderIndex + " already exists in this week");
        }
    }
}