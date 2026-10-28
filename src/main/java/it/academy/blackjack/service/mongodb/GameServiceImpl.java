package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.exceptions.GameAccessDeniedException;
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
        return handleGameEnd(new Game(playerName));
    }

    @Override
    public Mono<GameResponseDTO> playerHit(String id, String playerName) {
        return findOwnedGame(id, playerName)
                .flatMap(game -> Mono.fromCallable(() -> {
                            game.playerHit();
                            return game;
                        })
                        .flatMap(this::handleGameEnd));
    }

    @Override
    public Mono<GameResponseDTO> getGame(String id, String playerName) {
        return findOwnedGame(id, playerName)
                .map(gameMapper::toDTO);
    }

    @Override
    public Mono<GameResponseDTO> playerStand(String id, String playerName) {
        return findOwnedGame(id, playerName)
                .flatMap(game -> Mono.fromCallable(() -> {
                            game.playerStand();
                            return game;
                        })
                        .flatMap(this::handleGameEnd));
    }

    @Override
    public Mono<GameResponseDTO> playerDoubleDown(String id, String playerName) {
        return findOwnedGame(id, playerName)
                .flatMap(game -> Mono.fromCallable(() -> {
                            game.playerDoubleDown();
                            return game;
                        })
                        .flatMap(this::handleGameEnd));
    }

    @Override
    public Mono<Void> deleteGame(String id, String playerName) {
        return findOwnedGame(id, playerName)
                .flatMap(gameRepository::delete);
    }

    private Mono<Game> findOwnedGame(String id, String playerName) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found with ID: " + id)))
                .flatMap(game -> {
                    if (game.getPlayer() != null && playerName != null
                            && playerName.equals(game.getPlayer().getName())) {
                        return Mono.just(game);
                    }
                    return Mono.error(new GameAccessDeniedException(
                            "You are not allowed to access this game"));
                });
    }

    private Mono<GameResponseDTO> handleGameEnd(Game game) {
        return gameRepository.save(game)
                .flatMap(savedGame -> {
                    GameState state = savedGame.getState();
                    if (state == null || state == GameState.IN_PROGRESS || state == GameState.DRAW) {
                        return Mono.just(savedGame);
                    }
                    return rankingService.updateRanking(savedGame.getPlayer().getName(), state)
                            .thenReturn(savedGame);
                })
                .map(gameMapper::toDTO);
    }

}
