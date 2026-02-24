package it.academy.blackjack.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class Dealer extends Player {
    public Dealer(Hand hand) {
        super("Dealer", hand);
    }

    public boolean shouldHit() {
        return getHand().calculateValue() < 17;
    }

}
