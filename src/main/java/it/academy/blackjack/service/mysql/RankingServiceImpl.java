package it.academy.blackjack.service.mysql;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Ranking;
import it.academy.blackjack.dto.ranking.RankingResponseDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.RankingMapper;
import it.academy.blackjack.repository.mysql.RankingRepository;
import org.springframework.dao.DataIntegrityViolationException;
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
                    .switchIfEmpty(Mono.defer(() -> createRanking(playerName, 0)
                            .onErrorResume(DataIntegrityViolationException.class,
                                    e -> rankingRepository.findByPlayerName(playerName))
                            .map(rankingMapper::toDTO)));
        }

        return rankingRepository.incrementGamesWon(playerName)
                .flatMap(updated -> updated > 0
                        ? rankingRepository.findByPlayerName(playerName)
                        : createRanking(playerName, 1)
                                .onErrorResume(DataIntegrityViolationException.class,
                                        e -> rankingRepository.incrementGamesWon(playerName)
                                                .then(rankingRepository.findByPlayerName(playerName))))
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

    private Mono<Ranking> createRanking(String playerName, int gamesWon) {
        Ranking newRanking = new Ranking();
        newRanking.setPlayerName(playerName);
        newRanking.setGamesWon(gamesWon);
        return rankingRepository.save(newRanking);
    }
}
