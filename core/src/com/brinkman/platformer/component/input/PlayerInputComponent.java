package com.brinkman.platformer.component.input;

/**
 * @author Austin Brinkman.
 */
public class PlayerInputComponent implements InputComponent
{
    boolean runActive;
    boolean leftActive;
    boolean rightActive;

    @Override
    public boolean isRunActive() {
        return runActive;
    }

    @Override
    public void setRunActive(boolean runActive) {
        this.runActive = runActive;
    }

    @Override
    public boolean isLeftActive() {
        return leftActive;
    }

    @Override
    public void setLeftActive(boolean leftActive) {
        this.leftActive = leftActive;
    }

    @Override
    public boolean isRightActive() {
        return rightActive;
    }

    @Override
    public void setRightActive(boolean rightActive) {
        this.rightActive = rightActive;
    }
}
