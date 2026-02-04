package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.GameResponseDTO;
import it.academy.blackjack.dto.PlayerRankingDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.GameMapper;
import it.academy.blackjack.repository.mongodb.GameRepository;
import it.academy.blackjack.service.mysql.RankingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class GameService {
    /*private final GameRepository gameRepository;
    private final RankingService rankingService;
    private final GameMapper gameMapper;

    public Mono<GameResponseDTO> createGame(String playerName) {
        Game newGame = new Game(playerName);
        return gameRepository.save(newGame)
                .map(gameMapper::toDTO);
    }
    public Mono<GameResponseDTO> playerHit(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    game.playerHit();
                    return handleGameEnd(game);
                });
    }

    public Mono<GameResponseDTO> playerStand(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    game.playerStand();
                    return handleGameEnd(game);
                });
    }

    public Mono<Void> deleteGame(String id) {
        return gameRepository.existsById(id)
                .flatMap(exists -> exists
                        ? gameRepository.deleteById(id)
                        : Mono.error(new GameNotFoundException("Cannot delete. Game not found: " + id)));
    }

    private Mono<GameResponseDTO> handleGameEnd(Game game) {
        Mono<Game> savedGame = gameRepository.save(game);
        if (game.getState() == GameState.PLAYER_WIN || game.getState() == GameState.DEALER_BUST) {
            return rankingService.updateRanking(game.getPlayer().getName())
                    .then(savedGame)
                    .map(gameMapper::toDTO);
        }
        return savedGame.map(gameMapper::toDTO);
    }

    public Mono<GameResponseDTO> finishGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + gameId)))
                .flatMap(game -> {
                    game.resolveGame();
                    return gameRepository.save(game);
                })
                .doOnNext(game -> {
                    if (game.getState() == GameState.PLAYER_WIN) {
                        rankingService.updateRanking(game.getPlayer().getName())
                                .subscribe();
                    }
                   else if(game.getState() == GameState.DEALER_WIN) {
                       rankingService.updateRanking(game.getDealer().getName())
                               .subscribe();
                    }
                })
                .map(gameMapper::toDTO);
    }
*/
    private final GameRepository gameRepository;
    private final RankingService rankingService;
    private final GameMapper gameMapper;

    public Mono<GameResponseDTO> createGame(String playerName) {
        Game newGame = new Game(playerName);
        return gameRepository.save(newGame)
                .map(gameMapper::toDTO);
    }

    public Mono<GameResponseDTO> playerHit(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    game.playerHit();
                    return handleGameEnd(game);
                });
    }

    public Mono<GameResponseDTO> playerStand(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    game.playerStand();
                    return handleGameEnd(game);
                });
    }

    public Mono<Void> deleteGame(String id) {
        return gameRepository.existsById(id)
                .flatMap(exists -> exists
                        ? gameRepository.deleteById(id)
                        : Mono.error(new GameNotFoundException("Cannot delete. Game not found: " + id)));
    }

    private Mono<GameResponseDTO> handleGameEnd(Game game) {
        return gameRepository.save(game)
                .flatMap(savedGame -> {
                    if (savedGame.getState() == GameState.PLAYER_WIN) {
                        return rankingService.updateRanking(savedGame.getPlayer().getName())
                                .thenReturn(savedGame);
                    } else if (savedGame.getState() == GameState.DEALER_WIN || savedGame.getState() == GameState.DEALER_BUST) {
                        return rankingService.updateRanking(savedGame.getDealer().getName())
                                .thenReturn(savedGame);
                    } else {
                        return Mono.just(savedGame);
                    }
                })
                .map(gameMapper::toDTO);
    }

}
