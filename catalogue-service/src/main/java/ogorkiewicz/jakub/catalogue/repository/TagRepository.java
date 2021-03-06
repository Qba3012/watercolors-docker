package ogorkiewicz.jakub.catalogue.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ogorkiewicz.jakub.catalogue.model.Tag;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long>{
 
    public Optional<Tag> findByName(String name);
    
}