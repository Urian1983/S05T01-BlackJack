package it.academy.blackjack.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Deck;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.exceptions.IllegalMoveException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@NoArgsConstructor
@Data
@Document(collection="game")
@AllArgsConstructor
public class Game {
    private Player player;
    private Dealer dealer;
    @JsonIgnore
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

        boolean playerBlackjack = player.getHand().isBlackJack();
        boolean dealerBlackjack = dealer.getHand().isBlackJack();

        if (playerBlackjack && dealerBlackjack) {
            this.state = GameState.DRAW;
        } else if (playerBlackjack) {
            this.state = GameState.PLAYER_WIN;
        } else {
            this.state = GameState.IN_PROGRESS;
        }

    }

    public void playerHit(){
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalMoveException("Cannot hit. The game is already over with state: " + state);
        }

        player.getHand().addCard(deck.draw());

        if (player.getHand().isBust()) {
            this.state = GameState.PLAYER_BUST;
        }
    }

    public void playerDoubleDown() {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalMoveException("Cannot double down. The game is already over.");
        }

        if (player.getHand().getCards().size() != 2) {
            throw new IllegalMoveException("Double down is only allowed with the initial two cards.");
        }

        player.getHand().addCard(deck.draw());

        if (player.getHand().isBust()) {
            this.state = GameState.PLAYER_BUST;
        } else {
            player.stand();
            playDealerTurn();
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
