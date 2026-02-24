package it.academy.blackjack.dto.ranking;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RenamePlayerDTO {

    @NotBlank
    private String oldName;
    @NotBlank
    private String newName;
}
