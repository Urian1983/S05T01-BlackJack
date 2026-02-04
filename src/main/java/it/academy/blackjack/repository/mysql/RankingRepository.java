package it.academy.blackjack.repository.mysql;

import it.academy.blackjack.domain.model.PlayerRanking;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RankingRepository extends R2dbcRepository<PlayerRanking,Long> {
    Mono<PlayerRanking> findByPlayerName(String playerName);
    Flux<PlayerRanking> findAllByOrderByGamesWonDesc();
}
