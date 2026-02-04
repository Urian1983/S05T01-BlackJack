package it.academy.blackjack.domain;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.exceptions.IllegalMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game("Tester");
    }

    @Test
    void shouldStartGameWithFourCardsDistributed() {
        int totalCardsInHands = game.getPlayer().getHand().getCards().size() +
                game.getDealer().getHand().getCards().size();

        assertEquals(4, totalCardsInHands);
        assertNotNull(game.getState());
    }

    @Test
    void playerHitShouldIncreaseHandSize() {
        if (game.getState() != GameState.IN_PROGRESS) {
            game = new Game("Tester");
        }

        int initialSize = game.getPlayer().getHand().getCards().size();
        game.playerHit();

        assertEquals(initialSize + 1, game.getPlayer().getHand().getCards().size());
    }

    @Test
    void playerHitShouldResultInBustIfOver21() {
        game.getPlayer().getHand().addCard(new Card(Rank.TEN, Suit.SPADES));
        game.getPlayer().getHand().addCard(new Card(Rank.KING, Suit.HEARTS));
        game.getPlayer().getHand().addCard(new Card(Rank.JACK, Suit.CLUBS));
        game.playerHit();

        assertEquals(GameState.PLAYER_BUST, game.getState());
    }

    @Test
    void playerStandShouldFinishDealerTurn() {
        game.playerStand();

        // Al plantarse, el estado ya no puede ser IN_PROGRESS
        assertNotEquals(GameState.IN_PROGRESS, game.getState());
        assertTrue(game.getPlayer().isStay());
    }

    @Test
    void resolveGameCorrectWinner() {
        game.getPlayer().resetForNewRound();
        game.getDealer().resetForNewRound();
        game.getPlayer().getHand().addCard(new Card(Rank.TEN, Suit.SPADES));
        game.getPlayer().getHand().addCard(new Card(Rank.TEN, Suit.CLUBS));

        game.getDealer().getHand().addCard(new Card(Rank.TEN, Suit.HEARTS));
        game.getDealer().getHand().addCard(new Card(Rank.EIGHT, Suit.DIAMONDS));

        game.resolveGame();

        assertEquals(GameState.PLAYER_WIN, game.getState());
    }

    @Test
    void shouldThrowExceptionWhenHitOnFinishedGame() {

        game.playerStand();

        assertThrows(IllegalMoveException.class, () -> game.playerHit());
    }
}

