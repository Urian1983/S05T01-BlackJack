package it.academy.blackjack.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@NoArgsConstructor
@Setter
public class Player {
    private String name;
    private Hand hand;
    private boolean stay = false;

    public Player(String name, Hand hand) {
        this.name = name;
        this.hand = hand !=null ? hand: new Hand();
        this.stay = false;
    }

    public void addCardToHand(Card card){
        this.hand.addCard(card);
    }

    public void stand(){
        this.stay = true;
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
