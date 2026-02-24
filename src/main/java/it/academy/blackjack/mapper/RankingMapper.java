package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.model.Ranking;
import it.academy.blackjack.dto.ranking.RankingRequestDTO;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;

public interface RankingMapper {

    RankingResponseDTO toDTO(Ranking ranking);
    Ranking toEntity(RankingRequestDTO rankingRequestDTO);

}
