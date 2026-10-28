package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.dto.game.GameResponseDTO;
import reactor.core.publisher.Mono;

public interface GameService {
    Mono<GameResponseDTO> createGame(String playerName);
    Mono<GameResponseDTO> playerHit(String id, String playerName);
    Mono<GameResponseDTO> getGame(String id, String playerName);
    Mono<GameResponseDTO> playerStand(String id, String playerName);
    Mono<GameResponseDTO> playerDoubleDown(String id, String playerName);
    Mono<Void> deleteGame(String id, String playerName);

}
