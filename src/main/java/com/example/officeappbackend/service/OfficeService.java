package com.example.officeappbackend.service;

import com.example.officeappbackend.Entities.Office;
import com.example.officeappbackend.dto.OfficeDto;
import com.example.officeappbackend.repositories.OfficeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfficeService {
    private final OfficeRepository officeRepository;
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
        return new OfficeDto(
                office.getId(),
                office.getImageUrl(),
                office.getAddress()
        );
    }
}
