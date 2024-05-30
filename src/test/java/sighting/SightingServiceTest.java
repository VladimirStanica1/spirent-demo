package sighting;

import com.spirent.birdapp.bird.Bird;
import com.spirent.birdapp.bird.BirdRepository;
import com.spirent.birdapp.sighting.CreateSightingDto;
import com.spirent.birdapp.sighting.Sighting;
import com.spirent.birdapp.sighting.SightingDto;
import com.spirent.birdapp.sighting.SightingRepository;
import com.spirent.birdapp.sighting.SightingService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SightingServiceTest {
    @Mock
    private SightingRepository sightingRepository;
    @Mock
    private BirdRepository birdRepository;

    @InjectMocks
    private SightingService sightingService;

    private Sighting sighting;
    private SightingDto sightingDto;
    private CreateSightingDto createSightingDto;

    @BeforeEach
    public void setUp() {
        var dateTime = LocalDateTime.now().minusDays(1);
        sighting = Sighting.builder()
                .id(1L)
                .bird(Bird.builder()
                        .id(1L)
                        .color("color")
                        .weight(1.0)
                        .height(1.0)
                        .name("sparrow")
                        .build())
                .location("location")
                .dateTime(dateTime)
                .build();
        sightingDto = SightingDto.builder()
                .id(1L)
                .birdName("sparrow")
                .location("location")
                .dateTime(dateTime)
                .build();
        createSightingDto = CreateSightingDto.builder()
                .birdName("sparrow")
                .location("location")
                .dateTime(dateTime)
                .build();
    }

    @Test
    public void getAllSightings_whenSightingsExist_returnsListOfSightings() {
        //Given
        when(sightingRepository.findAll()).thenReturn(Collections.singletonList(sighting));
        //When
        var result = sightingService.getAllSightings();
        //Then
        assertEquals(1, result.size());
        assertEquals(sightingDto, result.get(0));
    }

    @Test
    public void getByLocation_whenSightingWithLocationExists_returnsSighting() {
        //Given
        when(sightingRepository.findByLocation(any(String.class))).thenReturn(Collections.singletonList(sighting));
        //When
        var result = sightingService.getAllSightingsByLocation("location");
        //Then
        assertEquals(1, result.size());
        assertEquals(sightingDto, result.get(0));
    }

    @Test
    public void getByBird_whenSightingWithBirdExists_returnsSighting() {
        //Given
        when(sightingRepository.findByBirdName(any(String.class))).thenReturn(Collections.singletonList(sighting));
        //When
        var result = sightingService.getAllSightingsByBirdName("birdName");
        //Then
        assertEquals(1, result.size());
        assertEquals(sightingDto, result.get(0));
    }

    @Test
    void addSighting_whenBirdDoesNotExist_shouldCreateBirdAndSaveSighting() {
        // Given
        var bird = Bird.builder().name("Sparrow").build();
        var sighting = Sighting.builder()
                .bird(bird)
                .location("location")
                .dateTime(LocalDateTime.now())
                .build();

        when(birdRepository.findByName("sparrow")).thenReturn(Optional.empty());
        when(birdRepository.save(any(Bird.class))).thenReturn(bird);
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);

        // When
        Optional<SightingDto> result = sightingService.addSighting(createSightingDto);

        // Then
        verify(birdRepository, times(1)).save(any(Bird.class));
        verify(sightingRepository, times(1)).save(any(Sighting.class));
        assertEquals("Sparrow", result.get().getBirdName());
    }

    @Test
    void addSighting_whenBirdExists_shouldSaveSighting() {
        // Given
        var bird = Bird.builder().name("Sparrow").build();
        var sighting = Sighting.builder()
                .bird(bird)
                .location("location")
                .dateTime(LocalDateTime.now())
                .build();

        when(birdRepository.findByName("sparrow")).thenReturn(Optional.of(bird));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);

        // When
        var result = sightingService.addSighting(createSightingDto);

        // Then
        verify(birdRepository, times(0)).save(any(Bird.class));
        verify(sightingRepository, times(1)).save(any(Sighting.class));
        assertEquals("Sparrow", result.get().getBirdName());
    }

    @Test
    void addSighting_whenRepositoryThrowsException_shouldPropagateException() {
        // Given
        var bird = Bird.builder().name("Sparrow").build();

        //When
        when(birdRepository.findByName("sparrow")).thenReturn(Optional.of(bird));
        when(sightingRepository.save(any(Sighting.class))).thenThrow(new RuntimeException());

        // Then
        assertThrows(RuntimeException.class, () -> sightingService.addSighting(createSightingDto));
    }

    @Test
    void updateSighting_whenSightingExists_updatesAndReturnsSighting() {
        // Given
        var bird = Bird.builder().id(1L).name("sparrow").build();

        when(sightingRepository.findById(sightingDto.getId())).thenReturn(Optional.of(sighting));
        when(birdRepository.findByName(sightingDto.getBirdName())).thenReturn(Optional.of(bird));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);

        // When
        var result = sightingService.updateSighting(sightingDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals(sightingDto.getId(), result.get().getId());
        assertEquals(sightingDto.getBirdName(), result.get().getBirdName());
    }

    @Test
    void updateSighting_whenSightingDoesNotExist_returnsEmptyOptional() {
        // Given
        when(sightingRepository.findById(sightingDto.getId())).thenReturn(Optional.empty());

        // When
        var result = sightingService.updateSighting(sightingDto);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void updateSighting_whenBirdDoesNotExist_createsNewBirdAndUpdatesSighting() {
        // Given
        var bird = Bird.builder().id(1L).name("sparrow").build();

        when(sightingRepository.findById(sightingDto.getId())).thenReturn(Optional.of(sighting));
        when(birdRepository.findByName(sightingDto.getBirdName())).thenReturn(Optional.empty());
        when(birdRepository.save(any(Bird.class))).thenReturn(bird);
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);

        // When
        var result = sightingService.updateSighting(sightingDto);

        // Then
        assertTrue(result.isPresent());
        assertEquals(sightingDto.getId(), result.get().getId());
        assertEquals(sightingDto.getBirdName(), result.get().getBirdName());
    }

    @Test
    public void deleteSighting_whenSightingExists_deletesSightingAndReturnsSightingId() {
        // Given
        when(sightingRepository.findById(any(Long.class))).thenReturn(Optional.of(sighting));
        // When
        var result = sightingService.deleteSighting(sightingDto.getId());

        // Then
        assertThat(result).isPresent();
        assertEquals(1, result.get());
    }

    @Test
    public void deleteSighting_whenSightingDoesNotExist_returnsEmptyOptional() {
        // Given
        when(sightingRepository.findById(any(Long.class))).thenReturn(Optional.of(sighting));
        when(birdRepository.findByName(any(String.class))).thenReturn(Optional.of(sighting.getBird()));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);

        // When
        var result = sightingService.updateSighting(sightingDto);

        // Then
        assertEquals(Optional.of(sightingDto), result);
    }

    @Test
    public void testDeleteSightingWhenSightingExists() {
        // Given
        Long sightingId = 1L;
        Sighting sighting = new Sighting();
        when(sightingRepository.findById(sightingId)).thenReturn(Optional.of(sighting));
        doNothing().when(sightingRepository).deleteById(sightingId);

        // When
        Optional<Long> result = sightingService.deleteSighting(sightingId);

        // Then
        verify(sightingRepository, times(1)).deleteById(sightingId);
        assertTrue(result.isPresent());
        assertEquals(sightingId, result.get());
    }

    @Test
    public void testDeleteSightingWhenSightingDoesNotExist() {
        // Given
        Long sightingId = 1L;
        when(sightingRepository.findById(sightingId)).thenReturn(Optional.empty());

        // When
        Optional<Long> result = sightingService.deleteSighting(sightingId);

        // Then
        verify(sightingRepository, times(0)).deleteById(sightingId);
        assertTrue(result.isEmpty());
    }

}
