package it.academy.blackjack.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankingTest {
    @Test
    void shouldCreatePlayerRankingWithAllArgsConstructor() {
        Long id = 1L;
        String name = "John Doe";
        Integer won = 15;

        Ranking ranking = new Ranking(id, name, won);

        assertEquals(id, ranking.getId());
        assertEquals(name, ranking.getPlayerName());
        assertEquals(won, ranking.getGamesWon());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Ranking ranking = new Ranking();

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
        Ranking r1 = new Ranking(1L, "Player", 10);
        Ranking r2 = new Ranking(1L, "Player", 10);
        Ranking r3 = new Ranking(2L, "Other", 5);

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        Ranking ranking = new Ranking(1L, "John", 10);
        String result = ranking.toString();

        assertTrue(result.contains("John"));
        assertTrue(result.contains("gamesWon=10"));
    }

}