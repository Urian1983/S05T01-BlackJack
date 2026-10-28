CREATE TABLE IF NOT EXISTS player_ranking (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                player_name VARCHAR(100) NOT NULL UNIQUE,
                                games_won INT NOT NULL DEFAULT 0
);
