package it.academy.blackjack.service.mysql;

import it.academy.blackjack.domain.model.PlayerRanking;
import it.academy.blackjack.dto.PlayerRankingDTO;
import it.academy.blackjack.exceptions.GameNotFoundException;
import it.academy.blackjack.mapper.GameMapper;
import it.academy.blackjack.repository.mysql.RankingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final GameMapper gameMapper;

    public Mono<PlayerRankingDTO> updateRanking(String playerName) {
        return rankingRepository.findByPlayerName(playerName)
                .flatMap(ranking -> {
                    ranking.setGamesWon(ranking.getGamesWon() + 1);
                    return rankingRepository.save(ranking);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    PlayerRanking newRanking = new PlayerRanking();
                    newRanking.setPlayerName(playerName); // USAR setPlayerName
                    newRanking.setGamesWon(1);
                    return rankingRepository.save(newRanking);
                }))
                .map(gameMapper::playerRankingResponse);
    }

    public Mono<PlayerRankingDTO> renamePlayer(Long playerId, String newName) {
        return rankingRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Player not found with ID: " + playerId)))
                .flatMap(player -> {
                    player.setPlayerName(newName);
                    return rankingRepository.save(player);
                })
                .map(gameMapper::playerRankingResponse);
    }

    public Flux<PlayerRankingDTO> getRanking() {
        return rankingRepository.findAllByOrderByGamesWonDesc()
                .map(gameMapper::playerRankingResponse);
    }


}
