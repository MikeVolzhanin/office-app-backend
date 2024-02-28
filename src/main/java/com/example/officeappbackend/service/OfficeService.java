package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.dto.OfficeDto;
import com.example.officeappbackend.repositories.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;
    private final ModelMapper modelMapper;
    public Optional<Office> findByAddress(String address){
        return officeRepository.findByAddress(address);
    }

    public Optional<Office> findById(Long id){
        return officeRepository.findById(id);
    }

    public List<Office> getAvailableOffices(){
        return officeRepository.findAll();
    }

    public OfficeDto convertToOfficeDto(Office office){
        return modelMapper.map(office, OfficeDto.class);
    }
}
