package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Hand;
import it.academy.blackjack.dto.game.GameResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class GameMapperImpl implements GameMapper {
    @Override
    public GameResponseDTO toDTO(Game game) {
        if (game == null) {
           return null;
        }
        GameResponseDTO dto = new GameResponseDTO();

        dto.setId(game.getId());
        dto.setDealer(toVisibleDealer(game.getDealer(), game.getState()));
        dto.setPlayer(game.getPlayer());
        dto.setState(game.getState());
        return dto;
    }

    private Dealer toVisibleDealer(Dealer dealer, GameState state) {
        if (dealer == null || state != GameState.IN_PROGRESS) {
            return dealer;
        }

        // The dealer's hole card must remain hidden while the game is in progress.
        Dealer visibleDealer = new Dealer(new Hand());
        if (dealer.isStay()) {
            visibleDealer.stand();
        }
        if (dealer.getHand() != null && !dealer.getHand().getCards().isEmpty()) {
            visibleDealer.getHand().addCard(dealer.getHand().getCards().get(0));
        }
        return visibleDealer;
    }
}
