package it.academy.blackjack.config;

import it.academy.blackjack.repository.mysql.RankingRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackageClasses = RankingRepository.class)
public class R2dbcConfig {
}
