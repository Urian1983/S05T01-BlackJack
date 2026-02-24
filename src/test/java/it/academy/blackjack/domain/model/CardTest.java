package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    @DisplayName("Debería crear una carta con el Rank y Suit correctos")
    void shouldCreateCardWithCorrectValues() {
        // Arrange
        Rank expectedRank = Rank.ACE;
        Suit expectedSuit = Suit.SPADES;

        // Act
        Card card = new Card(expectedRank, expectedSuit);

        // Assert
        assertAll("Propiedades de la carta",
                () -> assertEquals(expectedRank, card.rank(), "El Rank no coincide"),
                () -> assertEquals(expectedSuit, card.suit(), "El Suit no coincide")
        );
    }

    @Test
    @DisplayName("Debería verificar la igualdad entre dos cartas iguales (Equals/HashCode)")
    void testEqualsAndHashCode() {
        Card card1 = new Card(Rank.KING, Suit.HEARTS);
        Card card2 = new Card(Rank.KING, Suit.HEARTS);
        Card card3 = new Card(Rank.QUEEN, Suit.HEARTS);

        assertAll("Igualdad de objetos",
                () -> assertEquals(card1, card2, "Cartas con mismo rank y suit deberían ser iguales"),
                () -> assertNotEquals(card1, card3, "Cartas con diferente rank no deberían ser iguales"),
                () -> assertEquals(card1.hashCode(), card2.hashCode(), "El HashCode debería ser el mismo")
        );
    }

    @Test
    @DisplayName("El método toString debería contener información de Rank y Suit")
    void testToString() {
        Card card = new Card(Rank.JACK, Suit.DIAMONDS);
        String toString = card.toString();

        assertTrue(toString.contains("JACK"));
        assertTrue(toString.contains("DIAMONDS"));
    }
}
