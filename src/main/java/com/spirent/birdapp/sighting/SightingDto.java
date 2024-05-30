package com.spirent.birdapp.sighting;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class SightingDto {
    private Long id;
    @NonNull
    private String birdName;
    private String location;
    private LocalDateTime dateTime;
}
