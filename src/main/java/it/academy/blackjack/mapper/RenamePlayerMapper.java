package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.model.Player;
import it.academy.blackjack.dto.ranking.RenamePlayerDTO;

public interface RenamePlayerMapper {

    Player toEntity(RenamePlayerDTO renamePlayerDTO);
}
