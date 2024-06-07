package bird;

import com.spirent.birdapp.bird.BirdDto;
import com.spirent.birdapp.bird.CreateBirdDto;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is an end-to-end test that starts the application and connects to a real database.
 * It uses Testcontainers to start a MySQL container and override the application properties.
 * The test methods use the TestRestTemplate to make HTTP requests to the application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.spirent.birdapp.BirdAppApplication.class)
@Testcontainers
@ActiveProfiles("test")
public class BirdControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb");

    @DynamicPropertySource
    public static void registerPgProperties(DynamicPropertyRegistry registry) {
        //override application.yaml
        var jdbcUrl = mySQLContainer.getJdbcUrl();
        var username = mySQLContainer.getUsername();
        var password = mySQLContainer.getPassword();
        registry.add("spring.datasource.url", () -> jdbcUrl);
        registry.add("spring.datasource.username", () -> username);
        registry.add("spring.datasource.password", () -> password);
    }

    @BeforeEach
    public void setup() {
        jdbcTemplate.execute(
                "INSERT INTO bird (id, name, color, height, weight) VALUES (1, 'Bird 1', 'Red', 10.0, 20.0)");
        jdbcTemplate.execute(
                "INSERT INTO bird (id, name, color, height, weight) VALUES (2, 'Bird 2', 'Blue', 10.0, 20.0)");
        jdbcTemplate.execute(
                "INSERT INTO bird (id, name, color, height, weight) VALUES (3, 'Bird 3', 'Green', 10.0, 20.0)");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("DELETE FROM bird");
    }

    @Test
    public void testGetBird() {
        //When
        ResponseEntity<List<BirdDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/birds",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BirdDto>>() {
                });

        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body).isNotEmpty();
        assertThat(body.size()).isEqualTo(3);
    }

    @Test
    public void testAddBirdWithReturnResource() {
        // Given
        var birdDto = createBirdDto();

        // When
        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/birds?returnResource=true",
                birdDto,
                BirdDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(BirdDto.class);
        assertThat(((BirdDto) response.getBody()).getName()).isEqualTo(birdDto.getName());
        assertThat(((BirdDto) response.getBody()).getColor()).isEqualTo(birdDto.getColor());
        assertThat(((BirdDto) response.getBody()).getHeight()).isEqualTo(birdDto.getHeight());
        assertThat(((BirdDto) response.getBody()).getWeight()).isEqualTo(birdDto.getWeight());
    }

    @Test
    public void testAddBirdWithoutReturnResource() {
        // Given
        var birdDto = createBirdDto();

        // When
        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/birds?returnResource=false",
                birdDto,
                BirdDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void testUpdateBirdWithReturnResource() {
        // Given
        var birdDto = birdDto();
        birdDto.setId(1L);
        birdDto.setName("Updated bird");

        // When
        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/birds/update?returnResource=true",
                birdDto,
                BirdDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(BirdDto.class);
        assertThat(((BirdDto) response.getBody()).getName()).isEqualTo(birdDto.getName());
        assertThat(((BirdDto) response.getBody()).getColor()).isEqualTo(birdDto.getColor());
        assertThat(((BirdDto) response.getBody()).getHeight()).isEqualTo(birdDto.getHeight());
        assertThat(((BirdDto) response.getBody()).getWeight()).isEqualTo(birdDto.getWeight());
    }

    @Test
    public void testUpdateBirdWithoutReturnResource() {
        // Given
        var birdDto = birdDto();
        birdDto.setId(1L);
        birdDto.setName("Updated bird");

        // When
        ResponseEntity<?> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/birds/update?returnResource=false",
                birdDto,
                BirdDto.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void testDeleteBird() {
        // Given
        Long birdId = 1L;

        // When
        HttpEntity<Long> httpEntity = new HttpEntity<>(birdId);
        ResponseEntity<?> delete = restTemplate.exchange(
                "http://localhost:" + port + "/birds",
                HttpMethod.DELETE,
                httpEntity,
                Object.class);

        // Then
        ResponseEntity<List<BirdDto>> response = restTemplate.exchange(
                "http://localhost:" + port + "/birds",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BirdDto>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = response.getBody();
        assertThat(body).isNotEmpty();
        assertThat(body.size()).isEqualTo(2);
    }

    private static CreateBirdDto createBirdDto() {
        return CreateBirdDto.builder()
                .name("Test bird")
                .color("Blue")
                .height(10.0)
                .weight(20.0)
                .build();
    }

    private static BirdDto birdDto() {
        return BirdDto.builder()
                .name("Test bird")
                .color("Blue")
                .height(10.0)
                .weight(20.0)
                .build();
    }

}
