package ua.foxminded.javaspring.kocherga.carservice.models.mappers;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.ModelDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    ModelDto modelToModelDto(Model model);

    Model modelDtoToModel(ModelDto modelDto);

    List<ModelDto> modelListToModelDtoList(List<Model> models);

    List<Model> modelDtoListToModeList(List<ModelDto> modelsDto);

    default Page<ModelDto> modelPageToModelDtoPage(Page<Model> models) {
        return models.map(this::modelToModelDto);
    }
}
