package com.example.chefbackend.service;

import com.example.chefbackend.dto.ChefDetailDto;
import com.example.chefbackend.dto.ChefFilterDto;
import com.example.chefbackend.dto.ChefLeaderboardDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.mapper.ChefMapper;
import com.example.chefbackend.model.Chef;
import com.example.chefbackend.repository.ChefRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChefService {

    private final ChefRepository chefRepository;
    private final ChefMapper chefMapper;

    public ChefService(ChefRepository chefRepository, ChefMapper chefMapper) {
        this.chefRepository = chefRepository;
        this.chefMapper = chefMapper;
    }

    public List<ChefSummaryDto> getAllChefsSummary() {
        return chefRepository.findAll().stream()
                .map(chefMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    public ChefDetailDto getChefById(Long id) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chef not found with id: " + id));
        return chefMapper.toDetailDto(chef);
    }

    public ChefDetailDto createChef(ChefDetailDto chefDto) {
        Chef chef = chefMapper.toEntity(chefDto);
        Chef savedChef = chefRepository.save(chef);
        return chefMapper.toDetailDto(savedChef);
    }

    public ChefDetailDto updateChef(Long id, ChefDetailDto chefDto) {
        if (!chefRepository.existsById(id)) {
            throw new RuntimeException("Chef not found with id: " + id);
        }
        Chef chef = chefMapper.toEntity(chefDto);
        chef.setId(id);
        Chef updatedChef = chefRepository.save(chef);
        return chefMapper.toDetailDto(updatedChef);
    }

    public void deleteChef(Long id) {
        if (!chefRepository.existsById(id)) {
            throw new RuntimeException("Chef not found with id: " + id);
        }
        chefRepository.deleteById(id);
    }

    public List<ChefSummaryDto> getFilteredChefs(ChefFilterDto filter, String sortBy) {
        if ("mostViewed".equalsIgnoreCase(sortBy)) {
            return chefRepository.findAllByOrderByViewCountDesc();
        }

        return chefRepository.findChefsByFilters(
                filter.foodOrigin(),
                filter.expertise(),
                filter.minExperience(),
                filter.maxExperience(),
                filter.minBasePrice(),
                filter.maxBasePrice()
        );
    }

    public List<ChefLeaderboardDto> getTopChefs() {
        return chefRepository.findTop5ByViewCount();
    }
}
