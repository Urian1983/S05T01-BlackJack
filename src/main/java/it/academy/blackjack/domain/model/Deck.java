package it.academy.blackjack.domain.model;

import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck(){
        Arrays.stream(Suit.values())
                .flatMap(suit -> Arrays.stream(Rank.values())
                        .map(rank -> new Card(rank, suit)))
                .forEach(cards::add);
    }

    public void shuffle(){
        Collections.shuffle(cards);

    }
    public Card draw(){
        if(cards.isEmpty()){
            throw new IllegalStateException("No cards in this deck");
        }
        return cards.remove(cards.size() -1);
    }

    public int size(){
        return cards.size();

    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

}
