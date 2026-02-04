package it.academy.blackjack.mapper;

import it.academy.blackjack.domain.Game;
import it.academy.blackjack.domain.enums.Rank;
import it.academy.blackjack.domain.enums.Suit;
import it.academy.blackjack.domain.model.Card;
import it.academy.blackjack.domain.model.PlayerRanking;
import it.academy.blackjack.dto.CardDTO;
import it.academy.blackjack.dto.GameResponseDTO;
import it.academy.blackjack.dto.PlayerRankingDTO;
import it.academy.blackjack.dto.RenamePlayerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    private GameMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(GameMapper.class);
    }

    @Test
    void shouldMapGameToGameResponseDTO() {
        // Arrange
        Game game = new Game("PlayerTest");

        // Act
        GameResponseDTO dto = mapper.toDTO(game);

        // Assert
        assertNotNull(dto);
        assertEquals(game.getId(), dto.getId());
        assertEquals(game.getPlayer().getName(), dto.getPlayerName());
        assertEquals(game.getState(), dto.getGameState());
        assertEquals(game.getPlayer().getHand().getCards().size(), dto.getPlayerHand().size());
        assertEquals(game.getDealer().getHand().getCards().size(), dto.getDealerHand().size());
        assertEquals(game.getPlayer().getHand().calculateValue(), dto.getPlayerValue());
        assertEquals(game.getDealer().getHand().calculateValue(), dto.getDealerValue());
    }

    @Test
    void shouldMapCardToCardDTO() {
        Card card = new Card(Rank.ACE, Suit.SPADES);
        CardDTO dto = mapper.toCardDTO(card);
        assertNotNull(dto);
        assertEquals(Rank.ACE, dto.rank());
        assertEquals(Suit.SPADES, dto.suit());
    }

    @Test
    void shouldMapPlayerRankingToPlayerRankingDTO() {
        PlayerRanking ranking = new PlayerRanking(1L, "Winner", 10);
        PlayerRankingDTO dto = mapper.playerRankingResponse(ranking);
        assertNotNull(dto);
        assertEquals("Winner", dto.getPlayerName());
        assertEquals(10, dto.getGamesWon());
    }

    @Test
    void shouldHandleRenamePlayerRequestRecord() {
        RenamePlayerRequest request = new RenamePlayerRequest("NewName");
        assertEquals("NewName", request.newName());
    }

    @Test
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(mapper.toDTO(null));
        assertNull(mapper.toCardDTO(null));
        assertNull(mapper.playerRankingResponse(null));
    }

}