package it.academy.blackjack.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Suit {
    HEARTS("♥", "Red"),
    DIAMONDS("♦", "Red"),
    CLUBS("♣", "Black"),
    SPADES("♠", "Black");

    private final String symbol;
    private final String color;
}
