package com.pqb.motor_rental.services.impl;

import com.pqb.motor_rental.entities.Brand;
import com.pqb.motor_rental.services.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Override
    public List<Brand> getAllBrands() {
        return List.of();
    }
}
