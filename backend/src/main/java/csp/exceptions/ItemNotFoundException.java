package csp.exceptions;

public class ItemNotFoundException extends RuntimeException{
    public ItemNotFoundException(Long id) {
        super("No Item with the id " + id + " found!");
    }
}
