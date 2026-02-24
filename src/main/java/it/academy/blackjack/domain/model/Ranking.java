package it.academy.blackjack.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("player_ranking")
public class Ranking {

    @Id
    private Long id;

    @Column("player_name")
    @Schema(description = "Name of the player", example = "John Doe")
    private String playerName = "newPlayer";

    @Column("games_won")
    @Schema(description = "Number of gameplays won by the player", example = "15")
    private Integer gamesWon;
}
