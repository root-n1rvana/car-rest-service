package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.BrandMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.BrandService;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandDto> findAll() {
        return brandMapper.brandListToBrandDtoList(brandRepository.findAll());
    }

    @Override
    public BrandDto findById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
            () -> new EmptyResultDataAccessException("There's no such brand with id " + id, 1));
        return brandMapper.brandToBrandDto(brand);
    }

    @Override
    public Brand findByName(String name) {
        return brandRepository.findByName(name).orElseThrow(
            () -> new EmptyResultDataAccessException("There's no such brand with name " + name, 1));
    }

    @Override
    @Transactional
    public void create(BrandDto brandDto) {
        brandRepository.save(brandMapper.brandDtoToBrand(brandDto));
    }

    @Override
    @Transactional
    public void update(BrandDto brandDto) {
        Brand brandToUpdate = brandRepository.findById(brandDto.getId()).orElseThrow(
            () -> new EmptyResultDataAccessException("There's no such brand with id " + brandDto.getId(), 1));
        brandToUpdate.setName(brandDto.getName());
        brandRepository.save(brandToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }
}
