package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;
    private final String PLAYER_NAME = "TestPlayer";

    @BeforeEach
    void setUp() {
        player = new Player(PLAYER_NAME, new Hand());
    }

    @Test
    void shouldInitializeCorrectly() {
        assertEquals(PLAYER_NAME, player.getName());
        assertNotNull(player.getHand());
        assertFalse(player.isStay(), "El jugador no debería estar en 'stay' al inicio");
    }

    @Test
    void shouldChangeStatusWhenStandIsCalled() {
        player.stand();
        assertTrue(player.isStay(), "El atributo stay debería ser true");
    }

    @Test
    void shouldGetScoreFromHand() {
        player.addCardToHand(new Card(Rank.TEN, Suit.SPADES));
        player.addCardToHand(new Card(Rank.SEVEN, Suit.HEARTS));

        assertEquals(17, player.getScore(), "El score debería ser la suma de la mano");
    }

    @Test
    void shouldDetectBust() {
        player.addCardToHand(new Card(Rank.TEN, Suit.SPADES));
        player.addCardToHand(new Card(Rank.TEN, Suit.DIAMONDS));
        player.addCardToHand(new Card(Rank.FIVE, Suit.CLUBS));

        assertTrue(player.isBust(), "Con 25 puntos el jugador debería estar 'Bust'");
    }

    @Test
    void shouldResetPlayer() {
        player.addCardToHand(new Card(Rank.ACE, Suit.HEARTS));
        player.stand();

        player.resetForNewRound();

        assertEquals(0, player.getHand().getCards().size(), "La mano debería estar vacía tras el reset");
        assertFalse(player.isStay(), "El estado stay debería ser false tras el reset");
    }

    @Test
    void shouldHandleNullHandInConstructor() {
        Player playerWithNullHand = new Player("Ghost", null);
        assertNotNull(playerWithNullHand.getHand(), "El constructor debería haber inicializado una mano vacía");
    }

}