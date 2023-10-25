package ua.foxminded.javaspring.kocherga.carservice.models.mappers;

import org.mapstruct.Mapper;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.BrandDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    BrandDto brandToBrandDto(Brand brand);

    Brand brandDtoToBrand(BrandDto brandDto);

    List<BrandDto> brandListToBrandDtoList(List<Brand> brands);

    List<Brand> brandDtoListToBrandList(List<BrandDto> brandsDto);
}
