package com.spirent.birdapp.sighting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sightings")
@AllArgsConstructor
public class SightingController {

    private final SightingService sightingService;

    @Operation(summary = "Get all sightings")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the sightings",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class, type = "array"))})})
    @GetMapping
    public ResponseEntity<List<SightingDto>> getAllSightings() {
        return ResponseEntity.ok().body(sightingService.getAllSightings());
    }

    @Operation(summary = "Get sightings by location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the sightings",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class, type = "array"))})})
    @GetMapping("/location/{location}")
    public List<SightingDto> getAllSightingsByLocation(@PathVariable String location) {
        return sightingService.getAllSightingsByLocation(location);
    }

    @Operation(summary = "Get sightings by bird name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the sightings",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class, type = "array"))})})
    @GetMapping("/birdname/{birdName}")
    public ResponseEntity<List<SightingDto>> getAllSightingsByBirdName(@PathVariable String birdName) {
        return ResponseEntity.ok().body(sightingService.getAllSightingsByBirdName(birdName));
    }

    @Operation(summary = "Get sightings between two dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the sightings",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class, type = "array"))})})
    @GetMapping("/datetime/{startDateTime}/{endDateTime}")
    public List<SightingDto> getAllSightingsByDateTimeBetween(
            @PathVariable @DateTimeFormat(pattern = "yyy-MM-dd") LocalDateTime startDateTime,
            @PathVariable @DateTimeFormat(pattern = "yyy-MM-dd") LocalDateTime endDateTime) {
        return sightingService.getAllSightingsByDateTimeBetween(startDateTime, endDateTime);
    }

    @Operation(summary = "Add a new sighting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sighting added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping
    public ResponseEntity<?> addSighting(
            @RequestBody CreateSightingDto sighting,
            @Parameter(description = "Specifies if the updated resource should be returned") @RequestParam Boolean returnResource) {
        return sightingService.addSighting(sighting)
                .map(savedSighting -> returnResource ? ResponseEntity.ok(savedSighting) : ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Update a sighting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sighting updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SightingDto.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @PostMapping("/update")
    public ResponseEntity<?> updateSighting(
            @RequestBody SightingDto sighting,
            @Parameter(description = "Specifies if the updated resource should be returned") @RequestParam Boolean returnResource) {
        return sightingService.updateSighting(sighting)
                .map(updatedBird -> returnResource ? ResponseEntity.ok(updatedBird) : ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @Operation(summary = "Delete a sighting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sighting deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)})
    @DeleteMapping
    public ResponseEntity<?> deleteSighting(@RequestBody @NonNull Long sightingId) {
        return sightingService.deleteSighting(sightingId)
                .map(sighting -> ResponseEntity.noContent().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
