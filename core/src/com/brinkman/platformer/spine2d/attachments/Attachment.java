package com.brinkman.platformer.spine2d.attachments;

/**
 * Created by Austin on 1/26/2017.
 */
/** The base class for all attachments. */
abstract public class Attachment {
    String name;

    public Attachment (String name) {
        if (name == null) throw new IllegalArgumentException("name cannot be null.");
        this.name = name;
    }

    /** The attachment's name. */
    public String getName () {
        return name;
    }

    public String toString () {
        return getName();
    }
}
