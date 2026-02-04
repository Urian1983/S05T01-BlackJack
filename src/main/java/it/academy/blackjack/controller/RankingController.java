package it.academy.blackjack.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.academy.blackjack.domain.Game;
import it.academy.blackjack.dto.GameResponseDTO;
import it.academy.blackjack.dto.PlayerRankingDTO;
import it.academy.blackjack.dto.RenamePlayerRequest;
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
    @Operation(summary = "To get players ranking")
    @ApiResponse(
            responseCode = "200",
            description = "Ranking got succesfully",
            content = @Content(schema = @Schema(implementation = PlayerRankingDTO.class))
    )
    public Flux<PlayerRankingDTO> getRanking() {
        return rankingService.getRanking();
    }

    @PutMapping("/player/{playerId}")
    @Operation(summary = "Updates the name of the player")
    @ApiResponse(
            responseCode = "200",
            description = "Player name updated",
            content = @Content(schema = @Schema(implementation = PlayerRankingDTO.class)))
    public Mono<PlayerRankingDTO> updatePlayer(@PathVariable Long playerId, @RequestBody RenamePlayerRequest request) {
        return rankingService.renamePlayer(playerId, request.newName());
    }


    /*@PostMapping("/update-from-game")
    @Operation(summary = "Updates the ranking of a player based on a finished game")
    @ApiResponse(
            responseCode = "200",
            description = "Player ranking updated",
            content = @Content(schema = @Schema(implementation = PlayerRankingDTO.class))
    )
    public Mono<PlayerRankingDTO> updateRankingFromGame(@RequestBody GameResponseDTO gameResponse) {
        String playerName = gameResponse.getPlayerName();
        return rankingService.updateRanking(playerName);
    }*/

    @PostMapping("/update-from-game")
    @Operation(summary = "Updates the ranking of a player based on a finished game")
    @ApiResponse(
            responseCode = "200",
            description = "Player ranking updated",
            content = @Content(schema = @Schema(implementation = PlayerRankingDTO.class))
    )
    public Mono<PlayerRankingDTO> updateRankingFromGame(@RequestBody GameResponseDTO gameResponse) {
        return rankingService.updateRanking(gameResponse.getPlayerName(), gameResponse.getGameState());
    }



}
