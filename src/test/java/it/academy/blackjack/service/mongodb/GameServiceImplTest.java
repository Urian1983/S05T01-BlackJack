package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.GameMapper;
import it.academy.blackjack.repository.mongodb.GameRepository;
import it.academy.blackjack.service.mysql.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RankingService rankingService;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameServiceImpl gameService;

    private Game game;
    private GameResponseDTO gameResponseDTO;

    @BeforeEach
    void setUp() {
        game = new Game("testPlayer");
        gameResponseDTO = new GameResponseDTO();
        gameResponseDTO.setId(game.getId());
        gameResponseDTO.setState(game.getState());
        gameResponseDTO.setPlayer(game.getPlayer());
        gameResponseDTO.setDealer(game.getDealer());
    }

    @Test
    void createGame_shouldSaveAndReturnDTO() {
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(game)).thenReturn(gameResponseDTO);

        StepVerifier.create(gameService.createGame("testPlayer"))
                .expectNext(gameResponseDTO)
                .verifyComplete();

        verify(gameRepository, times(1)).save(any(Game.class));
        verify(gameMapper, times(1)).toDTO(game);
    }

    @Test
    void getGame_shouldReturnDTO_whenGameExists() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(game)).thenReturn(gameResponseDTO);

        StepVerifier.create(gameService.getGame(game.getId()))
                .expectNext(gameResponseDTO)
                .verifyComplete();
    }

    @Test
    void getGame_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());
        // Nota: no usar game.getId() en el when() si la llamada usa un ID distinto

        StepVerifier.create(gameService.getGame("nonExistentId"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerHit_shouldUpdateGameAndReturnDTO() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(any(Game.class))).thenReturn(gameResponseDTO);
        when(rankingService.updateRanking(anyString(), any(GameState.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerHit(game.getId()))
                .expectNext(gameResponseDTO)
                .verifyComplete();

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void playerHit_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerHit("nonExistentId"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerStand_shouldPlayDealerTurnAndReturnDTO() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(any(Game.class))).thenReturn(gameResponseDTO);
        when(rankingService.updateRanking(anyString(), any(GameState.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerStand(game.getId()))
                .expectNext(gameResponseDTO)
                .verifyComplete();
    }

    @Test
    void playerStand_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerStand("nonExistentId"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerDoubleDown_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerDoubleDown("nonExistentId"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerDoubleDown_shouldUpdateGameAndReturnDTO() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(any(Game.class))).thenReturn(gameResponseDTO);
        when(rankingService.updateRanking(anyString(), any(GameState.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerDoubleDown(game.getId()))
                .expectNext(gameResponseDTO)
                .verifyComplete();
    }

    @Test
    void deleteGame_shouldDeleteGame_whenExists() {
        when(gameRepository.existsById(game.getId())).thenReturn(Mono.just(true));
        when(gameRepository.deleteById(game.getId())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.deleteGame(game.getId()))
                .verifyComplete();

        verify(gameRepository).deleteById(game.getId());
    }

    @Test
    void deleteGame_shouldThrowGameNotFoundException_whenNotFound() {
        when(gameRepository.existsById(anyString())).thenReturn(Mono.just(false));

        StepVerifier.create(gameService.deleteGame("nonExistentId"))
                .expectError(GameNotFoundException.class)
                .verify();

        verify(gameRepository, never()).deleteById(anyString());
    }
}