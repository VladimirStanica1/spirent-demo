package com.spirent.birdapp.sighting;

import com.spirent.birdapp.bird.Bird;
import com.spirent.birdapp.bird.BirdRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class SightingService {
    private final SightingRepository sightingRepository;
    private final BirdRepository birdRepository;

    public List<SightingDto> getAllSightings() {
        return sightingRepository.findAll().stream()
                .map(this::fromEntity)
                .collect(toList());
    }

    public List<SightingDto> getAllSightingsByLocation(String location) {
        return sightingRepository.findByLocation(location).stream()
                .map(this::fromEntity)
                .collect(toList());
    }

    public List<SightingDto> getAllSightingsByBirdName(String birdName) {
        return sightingRepository.findByBirdName(birdName).stream()
                .map(this::fromEntity)
                .collect(toList());
    }

    public List<SightingDto> getAllSightingsByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return sightingRepository.findByDateTimeBetween(startDateTime, endDateTime).stream()
                .map(this::fromEntity)
                .collect(toList());
    }

    @Transactional
    public Optional<SightingDto> addSighting(CreateSightingDto sightingDto) {
        var bird = birdRepository.findByName(sightingDto.getBirdName())
                .orElseGet(() -> birdRepository.save(Bird.builder()
                        .name(sightingDto.getBirdName()).build()));

        var sighting = toEntity(sightingDto);
        sighting.setBird(bird);
        var savedSighting = sightingRepository.save(sighting);

        return Optional.of(fromEntity(savedSighting));
    }

    @Transactional
    public Optional<SightingDto> updateSighting(SightingDto sightingDto) {
        var existingSighting = sightingRepository.findById(sightingDto.getId());

        if (existingSighting.isEmpty()) {
            return Optional.empty();
        }

        var bird = birdRepository.findByName(sightingDto.getBirdName())
                .orElseGet(() -> birdRepository.save(Bird.builder()
                        .name(sightingDto.getBirdName()).build()));

        var sighting = existingSighting.get();
        sighting.setBird(bird);
        sighting.setLocation(sightingDto.getLocation());
        sighting.setDateTime(sightingDto.getDateTime());
        var updatedSighting = sightingRepository.save(sighting);
        return Optional.of(fromEntity(updatedSighting));
    }

    @Transactional
    public Optional<Long> deleteSighting(Long sightingId) {
        Optional<Sighting> sighting = sightingRepository.findById(sightingId);
        if (sighting.isEmpty()) {
            return Optional.empty();
        }
        sightingRepository.deleteById(sightingId);
        return Optional.of(sightingId);
    }

    private Sighting toEntity(CreateSightingDto sightingDto) {
        return Sighting.builder()
                .bird(Bird.builder().build())
                .location(sightingDto.getLocation())
                .dateTime(sightingDto.getDateTime())
                .build();
    }

    private SightingDto fromEntity(Sighting sighting) {
        return SightingDto.builder()
                .id(sighting.getId())
                .birdName(sighting.getBird().getName())
                .location(sighting.getLocation())
                .dateTime(sighting.getDateTime())
                .build();
    }
}
