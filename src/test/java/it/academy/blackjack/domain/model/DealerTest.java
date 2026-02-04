package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {
    private Dealer dealer;

    @BeforeEach
    void setUp() {
        dealer = new Dealer(new Hand());
    }

    @Test
    void shouldHitWhenScoreIsLessThan17() {
        dealer.addCardToHand(new Card(Rank.TEN, Suit.SPADES));
        dealer.addCardToHand(new Card(Rank.SIX, Suit.HEARTS)); // Total 16

        assertTrue(dealer.shouldHit());
    }

    @Test
    void shouldNotHitWhenScoreIsExactly17() {
        dealer.addCardToHand(new Card(Rank.TEN, Suit.SPADES));
        dealer.addCardToHand(new Card(Rank.SEVEN, Suit.HEARTS)); // Total 17

        assertFalse(dealer.shouldHit());
    }

    @Test
    void shouldNotHitWhenScoreIsGreaterThan17() {
        dealer.addCardToHand(new Card(Rank.TEN, Suit.SPADES));
        dealer.addCardToHand(new Card(Rank.NINE, Suit.HEARTS)); // Total 19

        assertFalse(dealer.shouldHit());
    }

    @Test
    void shouldHaveNameDealerByDefault() {
        assertEquals("Dealer", dealer.getName());
    }

    @Test
    void shouldHandleSoft17() {
        dealer.addCardToHand(new Card(Rank.ACE, Suit.SPADES));
        dealer.addCardToHand(new Card(Rank.SIX, Suit.HEARTS)); // Total 17 (As como 11)

        assertEquals(17, dealer.getScore());
        assertFalse(dealer.shouldHit());
    }

}