package com.brinkman.platformer.input;

/**
 * Created by Austin on 11/22/2016.
 */
public class InputFlags {
    private boolean left;
    private boolean right;
    private boolean run;
    private boolean jump;

    public void setLeft(boolean left) { this.left = left; }

    public boolean left() { return left; }

    public void setRight(boolean right) { this.right = right; }

    public boolean right() { return right; }

    public void setRun(boolean run) { this.run = run; }

    public boolean run() { return run; }

    public void setJump(boolean jump) { this.jump = jump; }

    public boolean jump() { return jump; }
}
