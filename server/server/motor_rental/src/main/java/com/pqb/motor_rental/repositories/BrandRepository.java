package com.pqb.motor_rental.repositories;

import com.pqb.motor_rental.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> getAllByOrderByNameAsc();
}
