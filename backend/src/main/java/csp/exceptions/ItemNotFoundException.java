package csp.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("No Item with the id " + id + " found!");
    }

    public ItemNotFoundException(String name) {
        super("No Item with the name " + name + " found!");
    }
}
