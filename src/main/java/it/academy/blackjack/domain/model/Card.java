package it.academy.blackjack.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cards")
@AllArgsConstructor
@Getter
public class Card {
    @Schema(description = "Value of the card", example = "ACE")
    private final Rank rank;
    @Schema(description = "Suit of the card", example = "HEARTS")
    private final Suit suit;
}
