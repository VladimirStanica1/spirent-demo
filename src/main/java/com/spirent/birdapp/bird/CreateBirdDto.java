package com.spirent.birdapp.bird;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBirdDto {
    @NotEmpty
    private String name;
    private String color;
    private double weight;
    private double height;
}
