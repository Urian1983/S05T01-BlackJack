package it.academy.blackjack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.dto.ranking.RenamePlayerDTO;
import it.academy.blackjack.service.mysql.RankingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping ("player_ranking")
@RequiredArgsConstructor
@Tag(name = "Ranking", description = "API of players ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    @Operation(summary = "Get players ranking")
    @ApiResponse(
            responseCode = "200",
            description = "Ranking retrieved successfully",
            content = @Content(schema = @Schema(implementation = RankingResponseDTO.class))
    )
    public Flux<RankingResponseDTO> getRanking() {
        return rankingService.getRanking();
    }

    @PutMapping("/player")
    public Mono<RankingResponseDTO> updatePlayer(
            @Valid @RequestBody Mono<RenamePlayerDTO> requestMono) {

        return requestMono
                .flatMap(request -> rankingService.renamePlayer(
                        request.getOldName(),
                        request.getNewName()
                ));
    }

    @PutMapping("/result")
    @Operation(summary = "Update ranking after game result")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RankingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public Mono<RankingResponseDTO> updateRanking(
            @RequestParam @NotBlank String playerName,
            @RequestParam GameState state) {

        return rankingService.updateRanking(playerName, state)
                .onErrorResume(e -> Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage())));
    }
}







