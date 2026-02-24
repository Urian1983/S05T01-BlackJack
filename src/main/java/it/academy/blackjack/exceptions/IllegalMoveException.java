package it.academy.blackjack.exceptions;

public class IllegalMoveException extends RuntimeException {
    public IllegalMoveException(String message) {
        super(message);
    }
}
