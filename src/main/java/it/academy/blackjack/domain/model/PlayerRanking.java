package it.academy.blackjack.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="player_ranking")
public class PlayerRanking {

    @Id
    private Long id;

    @Schema(description = "Name of the player", example = "John Doe")
    private String playerName;

    @Schema(description = "Number of gameplays won by the player", example = "15")
    private int gamesWon;
}
