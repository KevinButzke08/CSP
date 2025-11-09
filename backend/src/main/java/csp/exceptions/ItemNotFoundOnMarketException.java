package csp.exceptions;

public class ItemNotFoundOnMarketException extends RuntimeException{
    public ItemNotFoundOnMarketException(String name) {
        super("No Item with the name " + name + " found on the Steam market!");
    }
}
