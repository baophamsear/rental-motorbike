package com.pqb.motor_rental.controllers.api;

import com.pqb.motor_rental.entities.Brand;
import com.pqb.motor_rental.services.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class ApiBrandController {
    private final BrandService brandService;

    public ApiBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<List<Brand>> getAllBrands() {
        try{
            return ResponseEntity.ok(brandService.getAllBrands());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.noContent().build();
        }
    }

}
