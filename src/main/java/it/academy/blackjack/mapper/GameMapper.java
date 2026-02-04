package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.domain.model.PlayerRanking;
import it.academy.blackjack.dto.CardDTO;
import it.academy.blackjack.dto.GameResponseDTO;
import it.academy.blackjack.dto.PlayerRankingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(target = "playerName", source = "player.name")
    @Mapping(target = "playerHand", source = "player.hand.cards")
    @Mapping(target = "playerValue", source = "player.hand.value")
    @Mapping(target = "dealerHand", source = "dealer.hand.cards")
    @Mapping(target = "dealerValue", source = "dealer.hand.value")
    @Mapping(target = "status", source = "state") // 'state' en dominio -> 'status' en DTO [cite: 8]
    GameResponseDTO toDTO(Game game);

    PlayerRankingDTO playerRankingResponse(PlayerRanking ranking);
    CardDTO toCardDTO(Card card);
}
