package com.spirent.birdapp.bird;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    Optional<Bird> findByName(String name);

    Optional<Bird> findByColor(String color);
}
