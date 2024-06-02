package com.spirent.birdapp.sighting;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateSightingDto {
    private Long birdId;
    private String birdName;
    private String location;
    private LocalDateTime dateTime;
}
