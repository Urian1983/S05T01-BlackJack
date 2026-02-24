package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
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
        dto.setDealer(game.getDealer());
        dto.setPlayer(game.getPlayer());
        dto.setState(game.getState());
        return dto;
    }
}
