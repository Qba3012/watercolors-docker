package ogorkiewicz.jakub.catalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.catalogue.model.Painting;

@Repository
public interface PaintingRepository extends JpaRepository<Painting, Long> {}