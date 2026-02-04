DROP TABLE IF EXISTS player_ranking;
CREATE TABLE player_ranking (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                player_name VARCHAR(100) NOT NULL,
                                games_won INT NOT NULL DEFAULT 0
);