package it.academy.blackjack.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Dealer extends Player{
    public Dealer(Hand hand) {
        super("Dealer", hand);
    }
    public boolean shouldHit(){
        if(getHand().calculateValue()< 17)
        {
            return true;
        }

        else{
            return false;
        }
    }
}
