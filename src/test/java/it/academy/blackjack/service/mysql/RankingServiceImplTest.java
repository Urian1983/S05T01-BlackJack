package it.academy.blackjack.service.mysql;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Ranking;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.RankingMapper;
import it.academy.blackjack.repository.mysql.RankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceImplTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private RankingMapper rankingMapper;

    @InjectMocks
    private RankingServiceImpl rankingService;

    private Ranking ranking;
    private RankingResponseDTO rankingResponseDTO;

    @BeforeEach
    void setUp() {
        ranking = new Ranking(1L, "testPlayer", 5);

        rankingResponseDTO = new RankingResponseDTO();
        rankingResponseDTO.setPlayerName("testPlayer");
        rankingResponseDTO.setGamesWon(5);
    }

    @Test
    @DisplayName("getRanking: debería devolver todos los jugadores ordenados por victorias")
    void getRanking_shouldReturnAllPlayersSortedByWins() {
        Ranking ranking2 = new Ranking(2L, "anotherPlayer", 3);
        RankingResponseDTO dto2 = new RankingResponseDTO();
        dto2.setPlayerName("anotherPlayer");
        dto2.setGamesWon(3);

        when(rankingRepository.findAllByOrderByGamesWonDesc()).thenReturn(Flux.just(ranking, ranking2));
        when(rankingMapper.toDTO(ranking)).thenReturn(rankingResponseDTO);
        when(rankingMapper.toDTO(ranking2)).thenReturn(dto2);

        StepVerifier.create(rankingService.getRanking())
                .expectNext(rankingResponseDTO)
                .expectNext(dto2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getRanking: debería devolver un Flux vacío si no hay jugadores")
    void getRanking_shouldReturnEmptyFlux_whenNoPlayers() {
        when(rankingRepository.findAllByOrderByGamesWonDesc()).thenReturn(Flux.empty());

        StepVerifier.create(rankingService.getRanking())
                .verifyComplete();
    }

    @Test
    @DisplayName("updateRanking: debería incrementar gamesWon cuando el jugador gana (PLAYER_WIN)")
    void updateRanking_shouldIncrementWins_whenPlayerWins() {
        RankingResponseDTO updatedDTO = new RankingResponseDTO();
        updatedDTO.setPlayerName("testPlayer");
        updatedDTO.setGamesWon(6);

        when(rankingRepository.findByPlayerName("testPlayer")).thenReturn(Mono.just(ranking));
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(ranking));
        when(rankingMapper.toDTO(ranking)).thenReturn(updatedDTO);

        StepVerifier.create(rankingService.updateRanking("testPlayer", GameState.PLAYER_WIN))
                .expectNextMatches(dto -> dto.getGamesWon() == 6)
                .verifyComplete();

        verify(rankingRepository).save(argThat(r -> r.getGamesWon() == 6));
    }

    @Test
    @DisplayName("updateRanking: debería incrementar gamesWon cuando el dealer se pasa (DEALER_BUST)")
    void updateRanking_shouldIncrementWins_whenDealerBusts() {
        when(rankingRepository.findByPlayerName("testPlayer")).thenReturn(Mono.just(ranking));
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(ranking));
        when(rankingMapper.toDTO(ranking)).thenReturn(rankingResponseDTO);

        StepVerifier.create(rankingService.updateRanking("testPlayer", GameState.DEALER_BUST))
                .expectNext(rankingResponseDTO)
                .verifyComplete();

        verify(rankingRepository).save(argThat(r -> r.getGamesWon() == 6));
    }

    @Test
    @DisplayName("updateRanking: debería crear un nuevo jugador con 1 victoria si no existe (PLAYER_WIN)")
    void updateRanking_shouldCreateNewPlayerWithOneWin_whenPlayerNotFound() {
        Ranking newRanking = new Ranking(null, "newPlayer", 1);
        RankingResponseDTO newDTO = new RankingResponseDTO();
        newDTO.setPlayerName("newPlayer");
        newDTO.setGamesWon(1);

        when(rankingRepository.findByPlayerName("newPlayer")).thenReturn(Mono.empty());
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(newRanking));
        when(rankingMapper.toDTO(newRanking)).thenReturn(newDTO);

        StepVerifier.create(rankingService.updateRanking("newPlayer", GameState.PLAYER_WIN))
                .expectNextMatches(dto -> dto.getGamesWon() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("updateRanking: no debería modificar gamesWon cuando el jugador pierde (DEALER_WIN)")
    void updateRanking_shouldNotIncrementWins_whenPlayerLoses() {
        when(rankingRepository.findByPlayerName("testPlayer")).thenReturn(Mono.just(ranking));
        when(rankingMapper.toDTO(ranking)).thenReturn(rankingResponseDTO);

        StepVerifier.create(rankingService.updateRanking("testPlayer", GameState.DEALER_WIN))
                .expectNext(rankingResponseDTO)
                .verifyComplete();

        verify(rankingRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateRanking: debería crear un nuevo jugador con 0 victorias si no existe (PLAYER_BUST)")
    void updateRanking_shouldCreateNewPlayerWithZeroWins_whenPlayerNotFoundAndLoses() {
        Ranking newRanking = new Ranking(null, "newPlayer", 0);
        RankingResponseDTO newDTO = new RankingResponseDTO();
        newDTO.setPlayerName("newPlayer");
        newDTO.setGamesWon(0);

        when(rankingRepository.findByPlayerName("newPlayer")).thenReturn(Mono.empty());
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(newRanking));
        when(rankingMapper.toDTO(newRanking)).thenReturn(newDTO);

        StepVerifier.create(rankingService.updateRanking("newPlayer", GameState.PLAYER_BUST))
                .expectNextMatches(dto -> dto.getGamesWon() == 0)
                .verifyComplete();
    }

    @Test
    @DisplayName("renamePlayer: debería actualizar el nombre del jugador correctamente")
    void renamePlayer_shouldUpdatePlayerName() {
        RankingResponseDTO renamedDTO = new RankingResponseDTO();
        renamedDTO.setPlayerName("newName");
        renamedDTO.setGamesWon(5);

        when(rankingRepository.findByPlayerName("testPlayer")).thenReturn(Mono.just(ranking));
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(ranking));
        when(rankingMapper.toDTO(ranking)).thenReturn(renamedDTO);

        StepVerifier.create(rankingService.renamePlayer("testPlayer", "newName"))
                .expectNextMatches(dto -> dto.getPlayerName().equals("newName"))
                .verifyComplete();

        verify(rankingRepository).save(argThat(r -> r.getPlayerName().equals("newName")));
    }

    @Test
    @DisplayName("renamePlayer: debería lanzar GameNotFoundException si el jugador no existe")
    void renamePlayer_shouldThrowGameNotFoundException_whenPlayerNotFound() {
        when(rankingRepository.findByPlayerName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(rankingService.renamePlayer("nonExistent", "newName"))
                .expectError(GameNotFoundException.class)
                .verify();

        verify(rankingRepository, never()).save(any());
    }
}