package it.academy.blackjack.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.academy.blackjack.domain.enums.Rank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hand {

    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        if (card != null) cards.add(card);
    }

    @JsonProperty("score")
    public int calculateValue() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.rank() == Rank.ACE) {
                aces++;
                total += 1;
            } else {
                total += card.rank().getValue();
            }
        }

        for (int i = 0; i < aces; i++) {
            if (total + 10 <= 21) {
                total += 10;
            }
        }

        return total;
    }

    @JsonProperty("isBust")
    public boolean isBust() {
        return calculateValue() > 21;
    }

    @JsonProperty("isBlackjack")
    public boolean isBlackJack() {
        return cards.size() == 2 && calculateValue() == 21;
    }

}


