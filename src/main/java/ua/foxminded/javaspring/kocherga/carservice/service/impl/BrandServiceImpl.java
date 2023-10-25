package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.BrandMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandsRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.BrandService;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandsRepository brandsRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandsRepository brandsRepository, BrandMapper brandMapper) {
        this.brandsRepository = brandsRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    public List<BrandDto> findAll() {
        return brandMapper.brandListToBrandDtoList(brandsRepository.findAll());
    }

    @Override
    public BrandDto findById(Long id) {
        Brand brand = brandsRepository.findById(id).orElseThrow(
            () -> new EmptyResultDataAccessException("There's no such brand with id " + id, 1));
        return brandMapper.brandToBrandDto(brand);
    }

    @Override
    @Transactional
    public void create(BrandDto brandDto) {
        brandsRepository.save(brandMapper.brandDtoToBrand(brandDto));
    }

    @Override
    public boolean existsById(Long id) {
        return brandsRepository.existsById(id);
    }

    @Override
    @Transactional
    public void update(BrandDto brandDto) {
        Brand brandToUpdate = brandsRepository.findById(brandDto.getId()).orElseThrow(
            () -> new EmptyResultDataAccessException("There's no such brand with id " + brandDto.getId(), 1));
        brandToUpdate.setName(brandDto.getName());
        brandsRepository.save(brandToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        brandsRepository.deleteById(id);
    }
}
