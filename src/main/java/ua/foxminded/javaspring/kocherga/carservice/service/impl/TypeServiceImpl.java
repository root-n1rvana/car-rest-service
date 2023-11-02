package ua.foxminded.javaspring.kocherga.carservice.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;
import ua.foxminded.javaspring.kocherga.carservice.models.mappers.TypeMapper;
import ua.foxminded.javaspring.kocherga.carservice.repository.TypeRepository;
import ua.foxminded.javaspring.kocherga.carservice.service.TypeService;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

import java.util.Collection;
import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {

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
            .orElseThrow(() -> new BadRequestException("There's no such type with id " + id));
        return typeMapper.typeToTypeDto(type);
    }

    @Override
    public TypeDto findByName(String name) {
        Type type = typeRepository.findByName(name)
            .orElseThrow(() -> new BadRequestException("There's no such type with name " + name));
        return typeMapper.typeToTypeDto(type);
    }

    @Override
    public List<Type> findByNameIn(Collection<String> names) {
        return typeRepository.findByNameIn(names);
    }

    @Override
    @Transactional
    public void create(TypeDto typeDto) {
        typeRepository.save(typeMapper.typeDtoToType(typeDto));
    }

    @Override
    @Transactional
    public void update(TypeDto typeDto) {
        Type typeToUpdate = typeRepository.findById(typeDto.getId())
            .orElseThrow(() -> new BadRequestException("There's no such type with id " + typeDto.getId()));
        typeToUpdate.setName(typeDto.getName());
        typeRepository.save(typeToUpdate);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        typeRepository.deleteById(id);
    }
}
