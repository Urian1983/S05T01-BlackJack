package it.academy.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDTO {
    @Schema(description = "Exclusive id of the current gameplay", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Name of the player", example = "John Doe")
    private String playerName;

    @Schema(description = "Cards in player's hand")
    private List<CardDTO> playerHand;

    @Schema(description = "Total value of player's hand", example = "19")
    private int playerValue;

    @Schema(description = "Cards in the hands of the dealer")
    private List<CardDTO> dealerHand;

    @Schema(description = "Total value of dealer's hand", example = "17")
    private int dealerValue;

    @Schema(description = "Current state of the gameplay", example = "IN_PROGRESS")
    private String status;
}
