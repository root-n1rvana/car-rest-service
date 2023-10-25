package ua.foxminded.javaspring.kocherga.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.javaspring.kocherga.carservice.models.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

}
