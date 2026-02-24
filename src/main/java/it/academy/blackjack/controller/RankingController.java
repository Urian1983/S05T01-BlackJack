package it.academy.blackjack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.service.mysql.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/player/{playerId}")
    @Operation(summary = "Updates the name of the player")
    @ApiResponse(
            responseCode = "200",
            description = "Player name updated",
            content = @Content(schema = @Schema(implementation = RankingResponseDTO.class))
    )
    public Mono<RankingResponseDTO> updatePlayer(@PathVariable Long playerId, @RequestParam String newName) {
        return rankingService.renamePlayer(playerId, newName);
    }
    }








