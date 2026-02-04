package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {
    private Hand hand;

    @BeforeEach
    void setUp() {
        hand = new Hand();
    }

    @Test
    @DisplayName("Debería calcular el valor simple de las cartas sin Ases")
    void shouldCalculateSimpleValueWithoutAces() {
        hand.addCard(new Card(Rank.TEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.FIVE, Suit.SPADES));

        assertEquals(15, hand.calculateValue(), "10 + 5 debería ser 15");
    }

    @Test
    @DisplayName("El As debería valer 11 si no supera 21")
    void aceShouldBeElevenWhenNotBusting() {
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        hand.addCard(new Card(Rank.NINE, Suit.DIAMONDS));

        assertEquals(20, hand.calculateValue(), "As (11) + 9 debería ser 20");
    }

    @Test
    @DisplayName("El As debería valer 1 si 11 causaría un 'Bust'")
    void aceShouldBeOneWhenElevenWouldBust() {
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        hand.addCard(new Card(Rank.TEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.FIVE, Suit.SPADES));
        assertEquals(16, hand.calculateValue(), "El As debe valer 1 para no pasarse");
    }

    @Test
    @DisplayName("Debería detectar Blackjack (As + 10/Figura)")
    void shouldDetectBlackjack() {
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.KING, Suit.CLUBS));

        assertTrue(hand.isBlackJack(), "As y Rey debería ser Blackjack");
        assertEquals(21, hand.calculateValue());
    }

    @Test
    @DisplayName("No debería ser Blackjack si suma 21 con más de dos cartas")
    void shouldNotBeBlackjackWithThreeCards() {
        hand.addCard(new Card(Rank.SEVEN, Suit.SPADES));
        hand.addCard(new Card(Rank.SEVEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.SEVEN, Suit.CLUBS));

        assertEquals(21, hand.calculateValue());
        assertFalse(hand.isBlackJack(), "21 con tres cartas no es Blackjack");
    }

    @Test
    @DisplayName("Debería detectar cuando el jugador se pasa (Bust)")
    void shouldDetectBust() {
        hand.addCard(new Card(Rank.TEN, Suit.SPADES));
        hand.addCard(new Card(Rank.JACK, Suit.HEARTS));
        hand.addCard(new Card(Rank.TWO, Suit.CLUBS));

        assertTrue(hand.isBust(), "22 debería ser Bust");
    }

    @Test
    @DisplayName("No debería añadir cartas nulas")
    void shouldNotAddNullCard() {
        hand.addCard(null);
        assertEquals(0, hand.getCards().size(), "La lista de cartas debería seguir vacía");
    }

}