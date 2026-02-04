package it.academy.blackjack.config;

import it.academy.blackjack.repository.mongodb.GameRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackageClasses = GameRepository.class)
public class MongoConfig {
}