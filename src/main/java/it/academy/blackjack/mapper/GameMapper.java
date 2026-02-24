package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.dto.game.GameResponseDTO;

public interface GameMapper {

    GameResponseDTO toDTO(Game game);
}
