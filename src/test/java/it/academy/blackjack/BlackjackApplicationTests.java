package it.academy.blackjack;

import it.academy.blackjack.repository.mongodb.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BlackjackApplicationTests {

	@MockitoBean
	private GameRepository gameRepository;

	@Test
	void contextLoads() {
	}

}
