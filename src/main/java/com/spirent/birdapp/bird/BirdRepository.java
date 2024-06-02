package com.spirent.birdapp.bird;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {

    Optional<Bird> findByName(String name);

    @Query("SELECT b FROM Bird b WHERE (:name IS NULL OR b.name = :name) AND (:color IS NULL OR b.color = :color)")
    List<Bird> findAllBirds(String name, String color);
}
