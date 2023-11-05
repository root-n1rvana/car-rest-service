package ua.foxminded.javaspring.kocherga.carservice.models.mappers;

import org.mapstruct.Mapper;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TypeMapper {

    TypeDto typeToTypeDto(Type type);

    Type typeDtoToType(TypeDto typeDto);

    List<TypeDto> typeListToTypeDtoList(List<Type> types);

    List<Type> typeDtoListToTypeList(List<TypeDto> typesDto);
}
