package it.academy.blackjack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.service.mongodb.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
@Tag(name="Game", description = "API for managing a Blackjack game using MongoDB and Spring Boot")
public class GameController {

    private final GameService gameService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a new blackjack game")
    @ApiResponse(
            responseCode = "201",
            description = "New gameplay created",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> start(Principal principal) {
        return gameService.createGame(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blackjack game by its ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Game found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GameResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Game not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Game belongs to another player"
            )
    })
    public Mono<GameResponseDTO> getGame(
            @PathVariable String id,
            Principal principal) {

        return gameService.getGame(id, principal.getName());
    }


    @PostMapping("/{id}/hit")
    @Operation(summary = "Player requests a new card")
    @ApiResponse(
            responseCode = "200",
            description = "Card dealt successfully",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> playHit(@PathVariable String id, Principal principal) {
        return gameService.playerHit(id, principal.getName());
    }

    @PostMapping("/{id}/stand")
    @Operation(summary = "Player stands, dealer starts playing")
    @ApiResponse(
            responseCode = "200",
            description = "Turn passed to the dealer",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> playStand(@PathVariable String id, Principal principal) {
        return gameService.playerStand(id, principal.getName());
    }

    @PostMapping("/{id}/doubledown")
    @Operation(summary = "Player doubles down, receives one card and stands automatically")
    @ApiResponse(
            responseCode = "200",
            description = "Double down executed successfully",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> playDoubleDown(@PathVariable String id, Principal principal) {
        return gameService.playerDoubleDown(id, principal.getName());
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete gameplay")
    @ApiResponse(
            responseCode = "204",
            description = "Gameplay deleted successfully"
    )
    public Mono<Void> deleteGame(@PathVariable String id, Principal principal) {
        return gameService.deleteGame(id, principal.getName());
    }
}
