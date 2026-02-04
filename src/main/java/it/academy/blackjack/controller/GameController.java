package it.academy.blackjack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.academy.blackjack.dto.GameResponseDTO;
import it.academy.blackjack.service.mongodb.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
@Tag(name="Game", description = "API for managing a Blackjack game using MongoDB and Spring Boot")
public class GameController {

    private final GameService gameService;

    @ResponseStatus(HttpStatus.CREATED) // 201
    @Operation(summary = "Creates a new blackjack game")
    @ApiResponse(
            responseCode = "201",
            description = "New gameplay created",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )

    @PostMapping("/new")
    public Mono<GameResponseDTO> start() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    if (auth == null || !auth.isAuthenticated()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }
                    return gameService.createGame(auth.getName());
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado en el contexto")));
    }

    @PostMapping("/{id}/hit")
    @Operation(summary = "Player requests a new card")
    @ApiResponse(
            responseCode = "200",
            description = "Card dealt successfully",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> playHit(@PathVariable String id) {
        return gameService.playerHit(id);
    }

    @PostMapping("/{id}/stand")
    @Operation(summary = "Player stands, dealer starts playing")
    @ApiResponse(
            responseCode = "200",
            description = "Turn passed to the dealer",
            content = @Content(schema = @Schema(implementation = GameResponseDTO.class))
    )
    public Mono<GameResponseDTO> playStand(@PathVariable String id){
        return gameService.playerStand(id);
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete gameplay")
    @ApiResponse(
            responseCode = "204",
            description = "Gameplay deleted successfully"
    )
    public Mono<Void> deleteGame(@PathVariable String id) {
        return gameService.deleteGame(id);
    }

}
