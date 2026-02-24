package it.academy.blackjack.service.mysql;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RankingService {
    Mono<RankingResponseDTO> updateRanking(String playerName, GameState state);
    Mono<RankingResponseDTO> renamePlayer(Long playerId, String newName);
    Flux<RankingResponseDTO> getRanking();

}
