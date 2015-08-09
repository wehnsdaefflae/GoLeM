package net.sophomatics;

import net.sophomatics.hierarchy.Hierarchy;

import java.util.Set;

/**
 * Agent class
 *
 * @author mark
 * @version 1.0
 * @since 2015-08-10
 *
 */
public class Agent<Sensor, Motor> {
    private final Hierarchy<Sensor, Motor> h;
    private final Set<Motor> actions;

    public Agent(Set<Motor> actions) {
        this.h = new Hierarchy<>(1f);
        this.actions = actions;
    }
}
