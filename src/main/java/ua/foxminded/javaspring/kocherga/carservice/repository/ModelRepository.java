package ua.foxminded.javaspring.kocherga.carservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;

import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

    Optional<Model> findByNameAndYearAndBrandName(String name, int year, String brandName);

    Optional<Model> findByName(String name);

    @Query("SELECT DISTINCT m FROM Model m " +
           "WHERE (:brand IS NULL OR m.brand.name = :brand) " +
           "AND (:name IS NULL OR m.name = :name) " +
           "AND (:minYear IS NULL OR m.year >= :minYear) " +
           "AND (:maxYear IS NULL OR m.year <= :maxYear) " +
           "AND (:typeName IS NULL OR EXISTS (SELECT t FROM m.types t WHERE t.name = :typeName))")
    Page<Model> findModelsByFilters(@Param("brand") String brand,
                                    @Param("name") String model,
                                    @Param("minYear") Integer minYear,
                                    @Param("maxYear") Integer maxYear,
                                    @Param("typeName") String type,
                                    Pageable pageable);
}
