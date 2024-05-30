package com.spirent.birdapp.bird;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BirdDto {
    private Long id;
    @NotEmpty
    private String name;
    private String color;
    private Double weight;
    private Double height;
}
