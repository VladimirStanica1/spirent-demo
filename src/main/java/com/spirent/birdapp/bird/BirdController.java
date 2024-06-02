package com.spirent.birdapp.bird;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/birds")
@AllArgsConstructor
public class BirdController {
    private final BirdService birdService;

    @Operation(summary = "Returns all birds matching the given criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the bird",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BirdDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Bird not found",
                    content = @Content)
    })

    @GetMapping()
    public ResponseEntity<List<BirdDto>> getBird(@RequestParam(required = false) String name,
            @RequestParam(required = false) String color) {
        return birdService.getBirds(name, color)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Add a new bird")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bird added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BirdDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<?> addBird(@RequestBody @Valid CreateBirdDto bird,
            @RequestParam @Parameter(description = "Specifies if the updated resource should be returned") Boolean returnResource) {
        return birdService.addBird(bird)
                .map(savedBird -> returnResource ? ResponseEntity.ok(savedBird) : ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Update an existing bird")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bird updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BirdDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping("/update")
    public ResponseEntity<?> updateBird(@RequestBody @Valid BirdDto bird,
            @RequestParam @Parameter(description = "Specifies if the updated resource should be returned") Boolean returnResource) {
        return birdService.updateBird(bird)
                .map(updatedBird -> returnResource ? ResponseEntity.ok(updatedBird) : ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Delete a bird")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bird deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Bird not found",
                    content = @Content)})
    @DeleteMapping()
    public ResponseEntity<?> deleteBird(@RequestBody @NonNull Long birdId) {
        return birdService.deleteBird(birdId)
                .map(bird -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
