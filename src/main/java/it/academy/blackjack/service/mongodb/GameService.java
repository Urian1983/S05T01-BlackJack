package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.dto.game.GameResponseDTO;
import reactor.core.publisher.Mono;

public interface GameService {
    Mono<GameResponseDTO> createGame(String playerName);
    Mono<GameResponseDTO> playerHit(String id);
    Mono<GameResponseDTO> playerStand(String id);
    Mono<GameResponseDTO> playerDoubleDown(String id);
    Mono<Void> deleteGame(String id);

}
