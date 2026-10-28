package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.model.Ranking;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RankingMapperImpl implements RankingMapper {
    @Override
    public RankingResponseDTO toDTO(Ranking ranking) {
        if(ranking == null){
            return null;
        }

        RankingResponseDTO dto = new RankingResponseDTO();
        dto.setPlayerName(ranking.getPlayerName());
        dto.setGamesWon(ranking.getGamesWon());

        return dto;

    }
}
