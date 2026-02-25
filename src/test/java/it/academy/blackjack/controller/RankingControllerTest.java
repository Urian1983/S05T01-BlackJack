package it.academy.blackjack.controller;

import it.academy.blackjack.config.OpenAPIConfig;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.dto.ranking.RenamePlayerDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.security.SecurityConfig;
import it.academy.blackjack.service.mysql.RankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(value = RankingController.class,
        properties = "spring.webflux.base-path=")
@Import({OpenAPIConfig.class, SecurityConfig.class})
class RankingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RankingService rankingService;

    private RankingResponseDTO rankingResponseDTO1;
    private RankingResponseDTO rankingResponseDTO2;

    @BeforeEach
    void setUp() {
        rankingResponseDTO1 = new RankingResponseDTO();
        rankingResponseDTO1.setPlayerName("topPlayer");
        rankingResponseDTO1.setGamesWon(10);

        rankingResponseDTO2 = new RankingResponseDTO();
        rankingResponseDTO2.setPlayerName("anotherPlayer");
        rankingResponseDTO2.setGamesWon(5);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getRanking_shouldReturn200_withAuthentication() {
        when(rankingService.getRanking()).thenReturn(Flux.just(rankingResponseDTO1, rankingResponseDTO2));

        webTestClient.get()
                .uri("/player_ranking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RankingResponseDTO.class)
                .hasSize(2)
                .contains(rankingResponseDTO1, rankingResponseDTO2);
    }

    @Test
    void getRanking_shouldReturn401_withoutAuthentication() {
        webTestClient.get()
                .uri("/player_ranking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getRanking_shouldReturnEmptyList_whenNoPlayers() {
        when(rankingService.getRanking()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/player_ranking")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RankingResponseDTO.class)
                .hasSize(0);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void renamePlayer_shouldReturn200_whenAdmin() {
        RenamePlayerDTO renameRequest = new RenamePlayerDTO();
        renameRequest.setOldName("topPlayer");
        renameRequest.setNewName("renamedPlayer");

        RankingResponseDTO renamedDTO = new RankingResponseDTO();
        renamedDTO.setPlayerName("renamedPlayer");
        renamedDTO.setGamesWon(10);

        when(rankingService.renamePlayer("topPlayer", "renamedPlayer"))
                .thenReturn(Mono.just(renamedDTO));

        webTestClient.put()
                .uri("/player_ranking/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(renameRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RankingResponseDTO.class)
                .isEqualTo(renamedDTO);
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void renamePlayer_shouldReturn403_whenNotAdmin() {
        RenamePlayerDTO renameRequest = new RenamePlayerDTO();
        renameRequest.setOldName("topPlayer");
        renameRequest.setNewName("renamedPlayer");

        webTestClient.put()
                .uri("/player_ranking/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(renameRequest)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void renamePlayer_shouldReturn404_whenPlayerNotFound() {
        RenamePlayerDTO renameRequest = new RenamePlayerDTO();
        renameRequest.setOldName("nonExistent");
        renameRequest.setNewName("renamedPlayer");

        when(rankingService.renamePlayer(anyString(), anyString()))
                .thenReturn(Mono.error(new GameNotFoundException("Player not found with name: nonExistent")));

        webTestClient.put()
                .uri("/player_ranking/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(renameRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void renamePlayer_shouldReturn400_whenFieldsAreBlank() {
        RenamePlayerDTO invalidRequest = new RenamePlayerDTO();
        invalidRequest.setOldName("");
        invalidRequest.setNewName("");

        webTestClient.put()
                .uri("/player_ranking/player")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void updateRanking_shouldReturn200_withValidParams() {
        when(rankingService.updateRanking("topPlayer", GameState.PLAYER_WIN))
                .thenReturn(Mono.just(rankingResponseDTO1));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/player_ranking/result")
                        .queryParam("playerName", "topPlayer")
                        .queryParam("state", "PLAYER_WIN")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RankingResponseDTO.class)
                .isEqualTo(rankingResponseDTO1);
    }

    @Test
    @WithMockUser(username = "player", roles = "PLAYER")
    void updateRanking_shouldReturn400_whenPlayerNameIsBlank() {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/player_ranking/result")
                        .queryParam("playerName", "")
                        .queryParam("state", "PLAYER_WIN")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateRanking_shouldReturn401_whenNotAuthenticated() {
        webTestClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/player_ranking/result")
                        .queryParam("playerName", "topPlayer")
                        .queryParam("state", "PLAYER_WIN")
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
