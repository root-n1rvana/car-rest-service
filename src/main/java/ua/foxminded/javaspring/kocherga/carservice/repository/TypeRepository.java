package ua.foxminded.javaspring.kocherga.carservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.foxminded.javaspring.kocherga.carservice.models.Type;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<Type, Long> {

    Optional<Type> findByName(String name);

    List<Type> findByNameIn(Collection<String> names);
}
