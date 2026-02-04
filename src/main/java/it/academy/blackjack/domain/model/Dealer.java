package it.academy.blackjack.domain.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
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
