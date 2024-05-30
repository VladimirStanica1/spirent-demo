package com.spirent.birdapp.sighting;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SightingRepository extends JpaRepository<Sighting, Long> {
    List<Sighting> findByBirdName(String name);

    List<Sighting> findByLocation(String location);

    List<Sighting> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
