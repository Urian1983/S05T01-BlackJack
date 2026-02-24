package it.academy.blackjack.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    @DisplayName("Debería inicializar el mazo con 52 cartas únicas")
    void shouldInitializeWith52UniqueCards() {
        assertEquals(52, deck.size(), "Un mazo estándar debe tener 52 cartas");

        Set<Card> uniqueCards = new HashSet<>(deck.getCards());
        assertEquals(52, uniqueCards.size(), "Debería haber 52 cartas únicas (sin duplicados)");
    }

    @Test
    @DisplayName("Debería reducir el tamaño del mazo al sacar una carta")
    void shouldReduceSizeWhenCardIsDrawn() {
        int initialSize = deck.size();
        Card drawnCard = deck.draw();

        assertNotNull(drawnCard);
        assertEquals(initialSize - 1, deck.size(), "El tamaño debería disminuir en 1");
    }

    @Test
    @DisplayName("Debería lanzar excepción si se intenta sacar una carta de un mazo vacío")
    void shouldThrowExceptionWhenDeckIsEmpty() {
        // Vaciamos el mazo
        while (!deck.isEmpty()) {
            deck.draw();
        }

        assertTrue(deck.isEmpty());
        assertThrows(IllegalStateException.class, () -> deck.draw(),
                "Debería lanzar IllegalStateException al sacar de un mazo vacío");
    }

    @Test
    @DisplayName("Shuffle debería cambiar el orden de las cartas")
    void shuffleShouldChangeOrder() {
        // Creamos una copia del orden original
        Card firstCardBefore = deck.getCards().get(0);
        Card lastCardBefore = deck.getCards().get(51);

        deck.shuffle();

        Card firstCardAfter = deck.getCards().get(0);
        Card lastCardAfter = deck.getCards().get(51);
        assertFalse(firstCardBefore.equals(firstCardAfter) && lastCardBefore.equals(lastCardAfter),
                "El orden de las cartas debería haber cambiado tras el shuffle");
    }

}