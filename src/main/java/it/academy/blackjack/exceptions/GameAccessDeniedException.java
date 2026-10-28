package it.academy.blackjack.exceptions;

public class GameAccessDeniedException extends RuntimeException {
    public GameAccessDeniedException(String message) {
        super(message);
    }
}
