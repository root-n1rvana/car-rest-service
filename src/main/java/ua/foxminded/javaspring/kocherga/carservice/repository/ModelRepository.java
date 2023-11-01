package ua.foxminded.javaspring.kocherga.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

    Optional<Model> findByNameAndYearAndBrandName(String name, int year, String brandName);

    Optional<Model> findByName(String name);
}
