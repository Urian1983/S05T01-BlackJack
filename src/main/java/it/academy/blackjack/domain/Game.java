package it.academy.blackjack.domain;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Deck;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.exceptions.IllegalMoveException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Document(collection="game")
public class Game {
    private Player player;
    private Dealer dealer;
    private Deck deck;
    private GameState state;

    @Id
    private String id = UUID.randomUUID().toString();

    public Game(String playerName){
        this.deck = new Deck();
        this.player = new Player(playerName,new Hand());
        this.dealer = new Dealer(new Hand());
        this.state = GameState.IN_PROGRESS;

        start();
    }

    public void start(){
        deck.shuffle();

        player.getHand().addCard(deck.draw());
        dealer.getHand().addCard(deck.draw());
        player.getHand().addCard(deck.draw());
        dealer.getHand().addCard(deck.draw());

        if(player.getHand().calculateValue() == 21){
            resolveGame();
        }

        else{
            this.state = GameState.IN_PROGRESS;
        }

    }

    public void playerHit(){
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalMoveException("Cannot hit. The game is already over with state: " + state);
        }

        player.getHand().addCard(deck.draw());

        if(player.getHand().isBust()){
            this.state =GameState.PLAYER_BUST;
        }

    }

    public void playerStand(){
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalMoveException("Cannot stand. The game is already over.");
        }

        player.stand();
        playDealerTurn();

    }

    public void playDealerTurn(){
        while(dealer.shouldHit()){
            dealer.getHand().addCard(deck.draw());
        }

        if(dealer.getHand().isBust()){
            this.state = GameState.DEALER_BUST;
        }

        else {
            resolveGame();
        }

    }

    public void resolveGame(){
        int playerPoints = player.getHand().calculateValue();
        int dealerPoints = dealer.getHand().calculateValue();

        if (playerPoints > dealerPoints){
            this.state = GameState.PLAYER_WIN;
        }

        else if(playerPoints < dealerPoints){
            this.state = GameState.DEALER_WIN;
        }

        else{
            this.state = GameState.DRAW;
        }

    }

}
