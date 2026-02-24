package it.academy.blackjack.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String name = "New Player";
    private Hand hand;
    private boolean stay = false;

    public Player(String name, Hand hand) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name can't be empty");
        }
        this.name = name;
        this.hand = hand != null ? hand : new Hand();
    }
    public void stand() {
        this.stay = true;
    }
    @JsonIgnore
    public int getScore() {
        return hand.calculateValue();
    }
    @JsonIgnore
    public boolean isBust() {
        return hand.isBust();
    }

}
