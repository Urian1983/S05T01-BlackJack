package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        if(card !=null)
            cards.add(card);
    }

    public int calculateValue(){

        int total = cards.stream()
                .mapToInt(card -> card.getRank().getValue())
                .sum();

        boolean hasAce = cards.stream()
                .anyMatch(card -> card.getRank() == Rank.ACE);

        if (hasAce && total + 10 <= 21) {
            total += 10;
        }

        return total;
    }

    public boolean isBust() {
        return calculateValue() > 21;
    }

    public boolean isBlackJack(){
        return cards.size() == 2 && calculateValue() == 21;
    }

    public int getValue() {
        return calculateValue();
    }
}


