package it.academy.blackjack.domain;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Deck;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.exceptions.IllegalMoveException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void start_shouldDealerWin_whenDealerHasNaturalBlackjack() {
        // Deck order is reversed because draw() removes from the end:
        // player TEN, dealer ACE, player NINE, dealer KING.
        Deck deck = fixedDeck(
                new Card(Rank.KING, Suit.CLUBS),
                new Card(Rank.NINE, Suit.HEARTS),
                new Card(Rank.ACE, Suit.SPADES),
                new Card(Rank.TEN, Suit.DIAMONDS));

        Game game = new Game(new Player("player", new Hand()), new Dealer(new Hand()), deck,
                GameState.IN_PROGRESS);
        game.start();

        assertEquals(GameState.DEALER_WIN, game.getState());
    }

    @Test
    void start_shouldPlayerWin_whenPlayerHasNaturalBlackjack() {
        // player ACE, dealer NINE, player KING, dealer SEVEN.
        Deck deck = fixedDeck(
                new Card(Rank.SEVEN, Suit.CLUBS),
                new Card(Rank.KING, Suit.HEARTS),
                new Card(Rank.NINE, Suit.SPADES),
                new Card(Rank.ACE, Suit.DIAMONDS));

        Game game = new Game(new Player("player", new Hand()), new Dealer(new Hand()), deck,
                GameState.IN_PROGRESS);
        game.start();

        assertEquals(GameState.PLAYER_WIN, game.getState());
    }

    @Test
    void playerHit_shouldBustPlayer_whenCardExceedsTwentyOne() {
        Player player = playerWithCards(Rank.TEN, Rank.NINE);
        Game game = gameWith(player, dealerWithCards(Rank.SEVEN), new Card(Rank.KING, Suit.CLUBS));

        game.playerHit();

        assertEquals(GameState.PLAYER_BUST, game.getState());
    }

    @Test
    void playerDoubleDown_shouldDrawOneCardAndResolveGame() {
        Player player = playerWithCards(Rank.TEN, Rank.FIVE);
        Game game = gameWith(player, dealerWithCards(Rank.TEN, Rank.SEVEN), new Card(Rank.TWO, Suit.CLUBS));

        game.playerDoubleDown();

        assertEquals(3, player.getHand().getCards().size());
        assertTrue(player.isStay());
        assertEquals(GameState.DRAW, game.getState());
    }

    @Test
    void playerDoubleDown_shouldFail_whenPlayerHasMoreThanTwoCards() {
        Player player = playerWithCards(Rank.TEN, Rank.FIVE, Rank.TWO);
        Game game = gameWith(player, dealerWithCards(Rank.SEVEN), new Card(Rank.THREE, Suit.CLUBS));

        assertThrows(IllegalMoveException.class, game::playerDoubleDown);
    }

    @Test
    void playerStand_shouldDealerHitUntilSeventeen() {
        Player player = playerWithCards(Rank.TEN, Rank.KING);
        // Dealer starts with TEN and draws TWO then FIVE, reaching 17.
        Game game = gameWith(player, dealerWithCards(Rank.TEN),
                new Card(Rank.FIVE, Suit.HEARTS), new Card(Rank.TWO, Suit.CLUBS));

        game.playerStand();

        assertEquals(3, game.getDealer().getHand().getCards().size());
        assertEquals(17, game.getDealer().getHand().calculateValue());
        assertEquals(GameState.PLAYER_WIN, game.getState());
    }

    @Test
    void playerHit_shouldFail_whenDeckIsEmpty() {
        Player player = playerWithCards(Rank.TEN, Rank.NINE);
        Game game = gameWith(player, dealerWithCards(Rank.SEVEN));

        IllegalMoveException exception = assertThrows(IllegalMoveException.class, game::playerHit);

        assertEquals("No cards left in the deck", exception.getMessage());
    }

    private Game gameWith(Player player, Dealer dealer, Card... cards) {
        return new Game(player, dealer, fixedDeck(cards), GameState.IN_PROGRESS);
    }

    private Player playerWithCards(Rank... ranks) {
        Player player = new Player("player", new Hand());
        for (Rank rank : ranks) {
            player.getHand().addCard(new Card(rank, Suit.CLUBS));
        }
        return player;
    }

    private Dealer dealerWithCards(Rank... ranks) {
        Dealer dealer = new Dealer(new Hand());
        for (Rank rank : ranks) {
            dealer.getHand().addCard(new Card(rank, Suit.DIAMONDS));
        }
        return dealer;
    }

    private Deck fixedDeck(Card... cards) {
        return new Deck(List.of(cards)) {
            @Override
            public void shuffle() {
                // Keep the test deck in a deterministic order.
            }
        };
    }
}
