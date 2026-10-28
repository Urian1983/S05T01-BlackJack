package it.academy.blackjack.service.mongodb;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Deck;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.exceptions.GameAccessDeniedException;
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

import java.util.List;

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
        game = new Game(new Player("testPlayer", new Hand()), new Dealer(new Hand()),
                new Deck(List.of(new Card(Rank.TWO, Suit.CLUBS))), GameState.IN_PROGRESS);
        game.getPlayer().getHand().addCard(new Card(Rank.TEN, Suit.HEARTS));
        game.getPlayer().getHand().addCard(new Card(Rank.FIVE, Suit.SPADES));

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
        verifyNoInteractions(rankingService);
    }

    @Test
    void createGame_shouldUpdateRanking_whenPlayerHasNaturalBlackjack() {
        Game blackjackGame = new Game(new Player("testPlayer", new Hand()), new Dealer(new Hand()),
                new Deck(List.of()), GameState.PLAYER_WIN);
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(blackjackGame));
        when(rankingService.updateRanking("testPlayer", GameState.PLAYER_WIN)).thenReturn(Mono.empty());
        when(gameMapper.toDTO(blackjackGame)).thenReturn(gameResponseDTO);

        StepVerifier.create(gameService.createGame("testPlayer"))
                .expectNext(gameResponseDTO)
                .verifyComplete();

        verify(rankingService).updateRanking("testPlayer", GameState.PLAYER_WIN);
    }

    @Test
    void getGame_shouldReturnDTO_whenGameExistsAndBelongsToPlayer() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameMapper.toDTO(game)).thenReturn(gameResponseDTO);

        StepVerifier.create(gameService.getGame(game.getId(), "testPlayer"))
                .expectNext(gameResponseDTO)
                .verifyComplete();
    }

    @Test
    void getGame_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());
        StepVerifier.create(gameService.getGame("nonExistentId", "testPlayer"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void getGame_shouldThrowAccessDenied_whenGameBelongsToAnotherPlayer() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.getGame(game.getId(), "anotherPlayer"))
                .expectError(GameAccessDeniedException.class)
                .verify();
    }

    @Test
    void playerHit_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerHit("nonExistentId", "testPlayer"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerHit_shouldThrowAccessDenied_whenGameBelongsToAnotherPlayer() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.playerHit(game.getId(), "anotherPlayer"))
                .expectError(GameAccessDeniedException.class)
                .verify();
    }

    @Test
    void playerStand_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerStand("nonExistentId", "testPlayer"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void playerDoubleDown_shouldThrowGameNotFoundException_whenGameNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.playerDoubleDown("nonExistentId", "testPlayer"))
                .expectError(GameNotFoundException.class)
                .verify();
    }

    @Test
    void deleteGame_shouldDeleteGame_whenItBelongsToPlayer() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));
        when(gameRepository.delete(game)).thenReturn(Mono.empty());

        StepVerifier.create(gameService.deleteGame(game.getId(), "testPlayer"))
                .verifyComplete();

        verify(gameRepository).delete(game);
    }

    @Test
    void deleteGame_shouldThrowGameNotFoundException_whenNotFound() {
        when(gameRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(gameService.deleteGame("nonExistentId", "testPlayer"))
                .expectError(GameNotFoundException.class)
                .verify();

        verify(gameRepository, never()).delete(any(Game.class));
    }

    @Test
    void deleteGame_shouldThrowAccessDenied_whenGameBelongsToAnotherPlayer() {
        when(gameRepository.findById(game.getId())).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.deleteGame(game.getId(), "anotherPlayer"))
                .expectError(GameAccessDeniedException.class)
                .verify();

        verify(gameRepository, never()).delete(any(Game.class));
    }
}
