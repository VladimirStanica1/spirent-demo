package com.spirent.birdapp.bird;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class BirdService {
    private final BirdRepository birdRepository;

    public List<BirdDto> getAllBirds() {
        return birdRepository.findAll().stream()
                .map(this::fromEntity)
                .collect(toList());
    }

    public Optional<BirdDto> getByColor(String color) {
        return birdRepository.findByColor(color)
                .map(this::fromEntity);
    }

    public Optional<BirdDto> getByName(String name) {
        return birdRepository.findByName(name)
                .map(this::fromEntity);
    }

    public Optional<BirdDto> addBird(CreateBirdDto bird) {
        var savedBird = birdRepository.save(toEntity(bird));
        return Optional.of(fromEntity(savedBird));
    }

    @Transactional
    public Optional<?> updateBird(BirdDto bird) {
        var existingBird = birdRepository.findById(bird.getId());
        if (existingBird.isEmpty()) {
            return Optional.empty();
        }
        return existingBird
                .map(entity -> {
                    updateIfNotNull(entity::setName, bird.getName());
                    updateIfNotNull(entity::setColor, bird.getColor());
                    updateIfNotNull(entity::setWeight, bird.getWeight());
                    updateIfNotNull(entity::setHeight, bird.getHeight());
                    return fromEntity(birdRepository.save(entity));
                });
    }

    private <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    @Transactional
    public Optional<Long> deleteBird(Long birdId) {
        var bird = birdRepository.findById(birdId);
        if (bird.isEmpty()) {
            return Optional.empty();
        }
        birdRepository.deleteById(birdId);
        return Optional.of(birdId);
    }

    private Bird toEntity(CreateBirdDto birdDto) {
        return Bird.builder()
                .name(birdDto.getName())
                .color(birdDto.getColor())
                .weight(birdDto.getWeight())
                .height(birdDto.getHeight())
                .build();
    }

    private BirdDto fromEntity(Bird bird) {
        return BirdDto.builder()
                .id(bird.getId())
                .name(bird.getName())
                .color(bird.getColor())
                .weight(bird.getWeight())
                .height(bird.getHeight())
                .build();
    }
}
