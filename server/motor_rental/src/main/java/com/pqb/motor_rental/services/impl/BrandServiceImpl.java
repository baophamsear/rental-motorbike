package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Brand;
import com.pqb.motor_rental.repositories.BrandRepository;
import com.pqb.motor_rental.services.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.getAllByOrderByNameAsc();
    }
}
