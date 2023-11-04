package ua.foxminded.javaspring.kocherga.carservice.service;

import ua.foxminded.javaspring.kocherga.carservice.models.dto.TypeDto;

import java.util.List;

public interface TypeService {

    List<TypeDto> findAll();

    TypeDto findById(Long id);

    void create(TypeDto typeDto);

    void update(TypeDto typeDto);

    void delete(Long id);
}
