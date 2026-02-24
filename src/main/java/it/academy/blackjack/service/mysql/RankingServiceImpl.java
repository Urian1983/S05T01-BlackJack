package it.academy.blackjack.service.mysql;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Ranking;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.RankingMapper;
import it.academy.blackjack.repository.mysql.RankingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;
    private final RankingMapper rankingMapper;

    public RankingServiceImpl(RankingRepository rankingRepository, RankingMapper rankingMapper) {
        this.rankingRepository = rankingRepository;
        this.rankingMapper = rankingMapper;
    }

    @Override
    public Mono<RankingResponseDTO> updateRanking(String playerName, GameState state) {
        boolean playerWon = state == GameState.PLAYER_WIN || state == GameState.DEALER_BUST;

        if (!playerWon) {
            return rankingRepository.findByPlayerName(playerName)
                    .map(rankingMapper::toDTO)
                    .switchIfEmpty(Mono.defer(() -> {
                        Ranking newRanking = new Ranking();
                        newRanking.setPlayerName(playerName);
                        newRanking.setGamesWon(0);
                        return rankingRepository.save(newRanking)
                                .map(rankingMapper::toDTO);
                    }));
        }

        return rankingRepository.findByPlayerName(playerName)
                .flatMap(ranking -> {
                    ranking.setGamesWon(ranking.getGamesWon() + 1);
                    return rankingRepository.save(ranking);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Ranking newRanking = new Ranking();
                    newRanking.setPlayerName(playerName);
                    newRanking.setGamesWon(1);
                    return rankingRepository.save(newRanking);
                }))
                .map(rankingMapper::toDTO);
    }

    @Override
    public Mono<RankingResponseDTO> renamePlayer(String oldName, String newName) {
        return rankingRepository.findByPlayerName(oldName)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Player not found with name: " + oldName)))
                .flatMap(ranking -> {
                    ranking.setPlayerName(newName);
                    return rankingRepository.save(ranking);
                })
                .map(rankingMapper::toDTO);
    }

    @Override
    public Flux<RankingResponseDTO> getRanking() {
        return rankingRepository.findAllByOrderByGamesWonDesc()
                .map(rankingMapper::toDTO);
    }
}
