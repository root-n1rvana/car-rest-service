package ua.foxminded.javaspring.kocherga.carservice.service;

import jakarta.transaction.Transactional;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;
import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;

import java.util.Collection;
import java.util.List;

public interface TypeService {

    List<TypeDto> findAll();

    TypeDto findById(Long id);

    TypeDto findByName(String name);

    List<Type> findByNameIn(Collection<String> names);

    @Transactional
    void create(TypeDto typeDto);

    @Transactional
    void update(TypeDto typeDto);

    @Transactional
    void delete(Long id);
}
