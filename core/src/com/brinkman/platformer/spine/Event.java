package com.brinkman.platformer.spine;

/**
 * Created by Austin on 1/26/2017.
 */
/** Stores the current pose values for an {@link Event}.
 * <p>
 * See Timeline {@link Animation.Timeline#apply(Skeleton, float, float, com.badlogic.gdx.utils.Array, float, boolean, boolean)},
 * AnimationStateListener {@link AnimationState.AnimationStateListener#event(AnimationState.TrackEntry, Event)}, and
 * <a href="http://esotericsoftware.com/spine-events">Events</a> in the Spine User Guide. */
public class Event {
    final private EventData data;
    int intValue;
    float floatValue;
    String stringValue;
    final float time;

    public Event (float time, EventData data) {
        if (data == null) throw new IllegalArgumentException("data cannot be null.");
        this.time = time;
        this.data = data;
    }

    public int getInt () {
        return intValue;
    }

    public void setInt (int intValue) {
        this.intValue = intValue;
    }

    public float getFloat () {
        return floatValue;
    }

    public void setFloat (float floatValue) {
        this.floatValue = floatValue;
    }

    public String getString () {
        return stringValue;
    }

    public void setString (String stringValue) {
        this.stringValue = stringValue;
    }

    /** The animation time this event was keyed. */
    public float getTime () {
        return time;
    }

    /** The events's setup pose data. */
    public EventData getData () {
        return data;
    }

    public String toString () {
        return data.name;
    }
}