package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.GameMapper;
import it.academy.blackjack.repository.mongodb.GameRepository;
import it.academy.blackjack.service.mysql.RankingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final RankingService rankingService;
    private final GameMapper gameMapper;

    public GameServiceImpl(GameRepository gameRepository, RankingService rankingService, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.rankingService = rankingService;
        this.gameMapper = gameMapper;
    }

    @Override
    public Mono<GameResponseDTO> createGame(String playerName) {
        Game newGame = new Game(playerName);
        return gameRepository.save(newGame)
                .map(gameMapper::toDTO);
    }

    public Mono<GameResponseDTO> playerHit(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    return Mono.fromCallable(() -> {
                                game.playerHit();
                                return game;
                            })
                            .flatMap(this::handleGameEnd);
                });
    }

    @Override
    public Mono<GameResponseDTO> getGame(String id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDTO)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: "+id))
                );
    }

    @Override
    public Mono<GameResponseDTO> playerStand(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    return Mono.fromCallable(() -> {
                                game.playerStand();
                                return game;
                            })
                            .flatMap(this::handleGameEnd);
                });
    }

    @Override
    public Mono<GameResponseDTO> playerDoubleDown(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    return Mono.fromCallable(() -> {
                                game.playerDoubleDown();
                                return game;
                            })
                            .flatMap(this::handleGameEnd);
                });
    }

    @Override
    public Mono<Void> deleteGame(String id) {
        return gameRepository.existsById(id)
                .flatMap(exists -> exists
                        ? gameRepository.deleteById(id)
                        : Mono.error(new GameNotFoundException("Cannot delete. Game not found: " + id)));
    }

    private Mono<GameResponseDTO> handleGameEnd(Game game) {
        return gameRepository.save(game)
                .flatMap(savedGame -> {
                    if (savedGame.getState() == GameState.PLAYER_WIN || savedGame.getState() == GameState.DEALER_BUST) {
                        return rankingService.updateRanking(savedGame.getPlayer().getName(), savedGame.getState())
                                .thenReturn(savedGame);
                    } else if (savedGame.getState() == GameState.DEALER_WIN || savedGame.getState() == GameState.PLAYER_BUST) {
                        return Mono.just(savedGame);
                    } else {
                        return Mono.just(savedGame);
                    }
                })
                .map(gameMapper::toDTO);
    }

}
