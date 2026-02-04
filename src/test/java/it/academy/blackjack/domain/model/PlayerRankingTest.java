package it.academy.blackjack.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRankingTest {
    @Test
    void shouldCreatePlayerRankingWithAllArgsConstructor() {
        Long id = 1L;
        String name = "John Doe";
        Integer won = 15;

        PlayerRanking ranking = new PlayerRanking(id, name, won);

        assertEquals(id, ranking.getId());
        assertEquals(name, ranking.getPlayerName());
        assertEquals(won, ranking.getGamesWon());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        PlayerRanking ranking = new PlayerRanking();

        ranking.setId(10L);
        ranking.setPlayerName("Jane Doe");
        ranking.setGamesWon(5);

        assertAll(
                () -> assertEquals(10L, ranking.getId()),
                () -> assertEquals("Jane Doe", ranking.getPlayerName()),
                () -> assertEquals(5, ranking.getGamesWon())
        );
    }

    @Test
    void testEqualsAndHashCode() {
        PlayerRanking r1 = new PlayerRanking(1L, "Player", 10);
        PlayerRanking r2 = new PlayerRanking(1L, "Player", 10);
        PlayerRanking r3 = new PlayerRanking(2L, "Other", 5);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        PlayerRanking ranking = new PlayerRanking(1L, "John", 10);
        String result = ranking.toString();

        assertTrue(result.contains("John"));
        assertTrue(result.contains("gamesWon=10"));
    }

}