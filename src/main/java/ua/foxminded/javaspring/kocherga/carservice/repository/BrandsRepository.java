package ua.foxminded.javaspring.kocherga.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.javaspring.kocherga.carservice.models.Brand;

@Repository
public interface BrandsRepository extends JpaRepository<Brand, Long> {

    Brand findByName(String name);
}
