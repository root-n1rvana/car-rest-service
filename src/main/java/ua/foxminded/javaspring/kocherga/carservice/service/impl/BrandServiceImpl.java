package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.BrandMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.BrandRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.BrandService;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private static final String NO_SUCH_BRAND_ID_MSG = "There's no such brand with id %d";
    private static final String BRAND_NAME_EXIST_MSG = "Brand with same 'name' already exist";
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
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_BRAND_ID_MSG, id)));
        return brandMapper.brandToBrandDto(brand);
    }

    @Override
    @Transactional
    public void create(BrandDto brandDto) {
        checkIfBrandExist(brandDto);
        brandRepository.save(brandMapper.brandDtoToBrand(brandDto));
    }

    @Override
    @Transactional
    public void update(BrandDto brandDto) {
        checkIfBrandExist(brandDto);
        Brand brandToUpdate = brandRepository.findById(brandDto.getId())
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_BRAND_ID_MSG, brandDto.getId())));
        brandToUpdate.setName(brandDto.getName());
        brandRepository.save(brandToUpdate);
    }

    private void checkIfBrandExist(BrandDto brandDto) {
        brandRepository.findByName(brandDto.getName())
            .ifPresent(type -> {
                throw new BadRequestException(BRAND_NAME_EXIST_MSG);
            });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        brandRepository.deleteById(id);
    }
}
