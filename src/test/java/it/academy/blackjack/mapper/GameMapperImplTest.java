package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Deck;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.dto.game.GameResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameMapperImplTest {

    private final GameMapperImpl gameMapper = new GameMapperImpl();

    @Test
    void toDTO_shouldHideDealerHoleCardWhileGameIsInProgress() {
        Dealer dealer = new Dealer(new Hand());
        dealer.getHand().addCard(new Card(Rank.TEN, Suit.HEARTS));
        dealer.getHand().addCard(new Card(Rank.ACE, Suit.SPADES));

        Game game = new Game(new Player("player", new Hand()), dealer,
                new Deck(List.of()), GameState.IN_PROGRESS);

        GameResponseDTO dto = gameMapper.toDTO(game);

        assertEquals(1, dto.getDealer().getHand().getCards().size());
        assertEquals(Rank.TEN, dto.getDealer().getHand().getCards().get(0).rank());
    }

    @Test
    void toDTO_shouldRevealDealerHandWhenGameIsOver() {
        Dealer dealer = new Dealer(new Hand());
        dealer.getHand().addCard(new Card(Rank.TEN, Suit.HEARTS));
        dealer.getHand().addCard(new Card(Rank.ACE, Suit.SPADES));

        Game game = new Game(new Player("player", new Hand()), dealer,
                new Deck(List.of()), GameState.PLAYER_WIN);

        GameResponseDTO dto = gameMapper.toDTO(game);

        assertEquals(2, dto.getDealer().getHand().getCards().size());
    }
}
