package it.academy.blackjack.dto;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;

public record CardDTO(Rank rank, Suit suit ) {
}
