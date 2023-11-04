package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.TypeMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.TypeRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.TypeService;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {

    private static final String NO_SUCH_TYPE_ID_MSG = "There's no such type with id %d";
    private static final String TYPE_NAME_EXIST_MSG = "Type with same 'name' already exist";

    private final TypeRepository typeRepository;
    private final TypeMapper typeMapper;

    public TypeServiceImpl(TypeRepository typeRepository, TypeMapper typeMapper) {
        this.typeRepository = typeRepository;
        this.typeMapper = typeMapper;
    }

    @Override
    public List<TypeDto> findAll() {
        return typeMapper.typeListToTypeDtoList(typeRepository.findAll());
    }

    @Override
    public TypeDto findById(Long id) {
        Type type = typeRepository.findById(id)
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_TYPE_ID_MSG, id)));
        return typeMapper.typeToTypeDto(type);
    }

    @Override
    @Transactional
    public void create(TypeDto typeDto) {
        checkIfTypeExist(typeDto);
        typeRepository.save(typeMapper.typeDtoToType(typeDto));
    }

    @Override
    @Transactional
    public void update(TypeDto typeDto) {
        checkIfTypeExist(typeDto);
        Type typeToUpdate = typeRepository.findById(typeDto.getId())
            .orElseThrow(() -> new BadRequestException(String.format(NO_SUCH_TYPE_ID_MSG, typeDto.getId())));
        typeToUpdate.setName(typeDto.getName());
        typeRepository.save(typeToUpdate);
    }

    private void checkIfTypeExist(TypeDto typeDto) {
        typeRepository.findByName(typeDto.getName())
            .ifPresent(type -> {
                throw new BadRequestException(TYPE_NAME_EXIST_MSG);
            });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        typeRepository.deleteById(id);
    }
}
