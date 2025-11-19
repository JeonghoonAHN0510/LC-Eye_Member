package lceye.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lceye.model.entity.UnitsEntity;

public interface UnitsRepository extends JpaRepository<UnitsEntity, Integer> {
}
