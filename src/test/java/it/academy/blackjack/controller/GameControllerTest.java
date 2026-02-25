package it.academy.blackjack.controller;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.dto.game.GameResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.exceptions.IllegalMoveException;
import it.academy.blackjack.service.mongodb.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(GameController.class)
@Import(GameControllerTest.TestSecurityConfig.class)
class GameControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GameService gameService;

    private GameResponseDTO gameResponseDTO;

    @BeforeEach
    void setUp() {
        gameResponseDTO = new GameResponseDTO();
        gameResponseDTO.setId("test-game-id");
        gameResponseDTO.setState(GameState.IN_PROGRESS);
        gameResponseDTO.setPlayer(new Player("testPlayer", new Hand()));
        gameResponseDTO.setDealer(new Dealer(new Hand()));
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/new: debería crear una nueva partida y devolver 201")
    void createGame_shouldReturn201_whenAuthenticated() {
        when(gameService.createGame("player")).thenReturn(Mono.just(gameResponseDTO));

        webTestClient.post()
                .uri("/game/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-game-id")
                .jsonPath("$.state").isEqualTo("IN_PROGRESS")
                .jsonPath("$.player.name").isEqualTo("testPlayer");
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /game/new: sin autenticación devuelve error")
    void createGame_shouldReturnError_whenNotAuthenticated() {
        webTestClient.post()
                .uri("/game/new")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("GET /game/{id}: debería devolver la partida cuando existe")
    void getGame_shouldReturn200_whenGameExists() {
        when(gameService.getGame("test-game-id")).thenReturn(Mono.just(gameResponseDTO));

        webTestClient.get()
                .uri("/game/test-game-id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-game-id")
                .jsonPath("$.state").isEqualTo("IN_PROGRESS")
                .jsonPath("$.player.name").isEqualTo("testPlayer");
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("GET /game/{id}: debería devolver 404 cuando la partida no existe")
    void getGame_shouldReturn404_whenGameNotFound() {
        when(gameService.getGame(anyString()))
                .thenReturn(Mono.error(new GameNotFoundException("Game not found")));

        webTestClient.get()
                .uri("/game/nonExistentId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/hit: debería devolver 200 y el estado actualizado")
    void playerHit_shouldReturn200_withUpdatedGame() {
        when(gameService.playerHit("test-game-id")).thenReturn(Mono.just(gameResponseDTO));

        webTestClient.post()
                .uri("/game/test-game-id/hit")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-game-id")
                .jsonPath("$.state").isEqualTo("IN_PROGRESS")
                .jsonPath("$.player.name").isEqualTo("testPlayer");
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/hit: debería devolver 400 si la partida ya ha terminado")
    void playerHit_shouldReturn400_whenGameIsOver() {
        when(gameService.playerHit(anyString()))
                .thenReturn(Mono.error(new IllegalMoveException("Cannot hit. The game is already over.")));

        webTestClient.post()
                .uri("/game/test-game-id/hit")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/stand: debería devolver 200 y el estado final")
    void playerStand_shouldReturn200_withFinalState() {
        gameResponseDTO.setState(GameState.PLAYER_WIN);
        when(gameService.playerStand("test-game-id")).thenReturn(Mono.just(gameResponseDTO));

        webTestClient.post()
                .uri("/game/test-game-id/stand")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-game-id")
                .jsonPath("$.state").isEqualTo("PLAYER_WIN")
                .jsonPath("$.player.name").isEqualTo("testPlayer");
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/stand: debería devolver 400 si la partida ya ha terminado")
    void playerStand_shouldReturn400_whenGameIsOver() {
        when(gameService.playerStand(anyString()))
                .thenReturn(Mono.error(new IllegalMoveException("Cannot stand. The game is already over.")));

        webTestClient.post()
                .uri("/game/test-game-id/stand")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/doubledown: debería devolver 200 y el estado actualizado")
    void playerDoubleDown_shouldReturn200_withUpdatedGame() {
        when(gameService.playerDoubleDown("test-game-id")).thenReturn(Mono.just(gameResponseDTO));

        webTestClient.post()
                .uri("/game/test-game-id/doubledown")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("test-game-id")
                .jsonPath("$.state").isEqualTo("IN_PROGRESS")
                .jsonPath("$.player.name").isEqualTo("testPlayer");
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("POST /game/{id}/doubledown: debería devolver 400 si no se puede hacer double down")
    void playerDoubleDown_shouldReturn400_whenNotAllowed() {
        when(gameService.playerDoubleDown(anyString()))
                .thenReturn(Mono.error(new IllegalMoveException("Double down is only allowed with the initial two cards.")));

        webTestClient.post()
                .uri("/game/test-game-id/doubledown")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("DELETE /game/{id}/delete: debería devolver 204 cuando la partida existe")
    void deleteGame_shouldReturn204_whenGameExists() {
        when(gameService.deleteGame("test-game-id")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/game/test-game-id/delete")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    @DisplayName("DELETE /game/{id}/delete: debería devolver 404 cuando la partida no existe")
    void deleteGame_shouldReturn404_whenGameNotFound() {
        when(gameService.deleteGame(anyString()))
                .thenReturn(Mono.error(new GameNotFoundException("Cannot delete. Game not found.")));

                webTestClient.delete()
                .uri("/game/nonExistentId/delete")
                .exchange()
                .expectStatus().isNotFound();
    }
}