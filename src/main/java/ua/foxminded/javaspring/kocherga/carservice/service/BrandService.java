package ua.foxminded.javaspring.kocherga.carservice.service;

import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;

import java.util.List;

public interface BrandService {

    List<BrandDto> findAll();

    BrandDto findById(Long id);

    Brand findByName(String name);

    void create(BrandDto brandDto);

    void update(BrandDto brandDto);

    void delete(Long id);
}
