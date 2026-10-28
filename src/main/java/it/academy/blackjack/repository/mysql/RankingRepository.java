package it.academy.blackjack.repository.mysql;

import it.academy.blackjack.domain.model.Ranking;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RankingRepository extends R2dbcRepository<Ranking,Long> {
    Mono<Ranking> findByPlayerName(String playerName);
    Flux<Ranking> findAllByOrderByGamesWonDesc();

    @Modifying
    @Query("UPDATE player_ranking SET games_won = games_won + 1 WHERE player_name = :playerName")
    Mono<Integer> incrementGamesWon(@Param("playerName") String playerName);
}
