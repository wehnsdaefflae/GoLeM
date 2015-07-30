package net.sophomatics.util;

/**
 * Created by mark on 14.07.15.
 */
public abstract class Identifiable {
    private final int id;

    public Identifiable(int id) {
        this.id = id;
    }

    public int getId(){
        return this.id;
    }
}
