package it.academy.blackjack.dto.game;

import it.academy.blackjack.domain.enums.GameState;
import it.academy.blackjack.domain.model.Dealer;
import it.academy.blackjack.domain.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDTO {
    private Player player;
    private Dealer dealer;
    private GameState state;
    private String id;

}
