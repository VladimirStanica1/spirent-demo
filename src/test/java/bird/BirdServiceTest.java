package bird;

import com.spirent.birdapp.bird.Bird;
import com.spirent.birdapp.bird.BirdDto;
import com.spirent.birdapp.bird.BirdRepository;
import com.spirent.birdapp.bird.BirdService;
import com.spirent.birdapp.bird.CreateBirdDto;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BirdServiceTest {
    @Mock
    private BirdRepository birdRepository;

    @InjectMocks
    private BirdService birdService;

    private Bird bird;
    private BirdDto birdDto;
    private CreateBirdDto createBirdDto;

    @BeforeEach
    public void setUp() {
         bird = Bird.builder()
                .id(1L)
                .name("Sparrow")
                .color("Grey")
                .weight(1.5)
                .height(15.0)
                .build();

        birdDto = BirdDto.builder()
                .id(1L)
                .name("Sparrow")
                .color("Grey")
                .weight(1.5)
                .height(15.0)
                .build();

        createBirdDto = CreateBirdDto.builder()
                .name("Sparrow")
                .color("Grey")
                .weight(1.5)
                .height(15.0)
                .build();
    }

    @Test
    void getBirds_whenNoBirdsExist_returnsEmptyOptional() {
        // Given
        when(birdRepository.findAllBirds(null, null)).thenReturn(Collections.emptyList());

        // When
        Optional<List<BirdDto>> result = birdService.getBirds(null, null);

        // Then
        assertEquals(Optional.empty(), result);
    }

    @Test
    void getBirds_whenBirdsExist_returnsOptionalWithListOfBirds() {
        // Given
        Bird bird = new Bird();
        bird.setName("Sparrow");
        bird.setColor("Grey");
        when(birdRepository.findAllBirds(null, null)).thenReturn(Collections.singletonList(bird));

        // When
        Optional<List<BirdDto>> result = birdService.getBirds(null, null);

        // Then
        assertEquals(1, result.get().size());
        assertEquals("Sparrow", result.get().get(0).getName());
        assertEquals("Grey", result.get().get(0).getColor());
    }

    @Test
    void addBird_whenBirdIsValid_savesAndReturnsBird() {
        // Given
        var bird = Bird.builder()
                .id(1L)
                .name("Sparrow")
                .color("Grey")
                .weight(1.5)
                .height(15.0)
                .build();
        var birdDto = BirdDto.builder()
                .id(1L)
                .name("Sparrow")
                .color("Grey")
                .weight(1.5)
                .height(15.0)
                .build();

        when(birdRepository.save(any(Bird.class))).thenReturn(bird);

        // When
        var result = birdService.addBird(createBirdDto);

        // Then
        assertEquals(Optional.of(birdDto), result);
    }

    @Test
    void addBird_whenRepositoryThrowsException_shouldPropagateException() {
        // Given
        when(birdRepository.save(any(Bird.class))).thenThrow(new RuntimeException());

        //Then
        assertThrows(RuntimeException.class, () -> birdService.addBird(createBirdDto));
    }

    @Test
    void updateBird_whenBirdExists_updatesAndReturnsBird() {
        // Given
        when(birdRepository.findById(1L)).thenReturn(Optional.of(bird));
        when(birdRepository.save(any(Bird.class))).thenReturn(bird);

        // When
        var result = birdService.updateBird(birdDto);

        // Then
        assertEquals(Optional.of(birdDto), result);
    }

    @Test
    void updateBird_whenBirdDoesNotExist_returnsEmptyOptional() {
        // Given
        when(birdRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        var result = birdService.updateBird(birdDto);

        // Then
        assertEquals(Optional.empty(), result);
    }

    @Test
    void updateBird_whenRepositoryThrowsException_shouldPropagateException() {
        // Arrange
        when(birdRepository.findById(1L)).thenReturn(Optional.of(new Bird()));
        when(birdRepository.save(any(Bird.class))).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> birdService.updateBird(birdDto));
    }

    @Test
    public void deleteBird_whenBirdExists_deletesBirdAndReturnsBirdId() {
        // Given
        var birdId = 1L;
        when(birdRepository.findById(birdId)).thenReturn(Optional.of(bird));
        doNothing().when(birdRepository).deleteById(birdId);

        // When
        var result = birdService.deleteBird(birdId);

        // Then
        verify(birdRepository, times(1)).deleteById(birdId);
        assertEquals(Optional.of(birdId), result);
    }

    @Test
    public void deleteBird_whenBirdDoesNotExist_returnsEmptyOptional() {
        // Given
        var birdId = 1L;
        when(birdRepository.findById(birdId)).thenReturn(Optional.empty());

        // When
        var result = birdService.deleteBird(birdId);

        // Then
        verify(birdRepository, times(0)).deleteById(birdId);
        assertEquals(Optional.empty(), result);
    }

}
