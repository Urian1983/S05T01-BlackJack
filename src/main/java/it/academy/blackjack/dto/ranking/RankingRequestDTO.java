package it.academy.blackjack.dto.ranking;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingRequestDTO
{
    @NotBlank
    private String playerName;
    @NotNull
    private int gamesWon;
}
