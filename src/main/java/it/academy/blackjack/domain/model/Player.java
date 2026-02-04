package it.academy.blackjack.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Player {
    private final String name;
    private Hand hand;
    private boolean stay = false;

    public Player(String name, Hand hand) {
        this.name = name;
        this.hand = new Hand();
        this.stay = false;
    }

    public void addCardToHand(Card card){
        this.hand.addCard(card);
    }

    public void stand(){
        this.stay = !this.stay;
    }

    public int getScore(){
        return hand.calculateValue();
    }

    public boolean isBust(){
        return hand.isBust();
    }

    public void resetForNewRound(){
        this.hand = new Hand();
        this.stay = false;
    }

}
