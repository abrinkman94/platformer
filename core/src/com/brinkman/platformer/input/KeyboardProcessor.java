package com.brinkman.platformer.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.brinkman.platformer.entity.actor.Player;

/**
 * Created by Austin on 11/23/2016.
 */
public class KeyboardProcessor implements InputProcessor {
    private final InputFlags inputFlags;
    private final Player player;

    public KeyboardProcessor(InputFlags inputFlags, Player player) {
        this.inputFlags = inputFlags;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.LEFT) {
            inputFlags.setLeft(true);
        } else if (keycode == Keys.RIGHT) {
            inputFlags.setRight(true);
        }

        if (keycode == Keys.SHIFT_LEFT) {
            inputFlags.setRun(true);
        }

        if (keycode == Keys.SPACE) {
            inputFlags.setJump(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.LEFT) {
            inputFlags.setLeft(false);
        }
        if (keycode == Keys.RIGHT) {
            inputFlags.setRight(false);
        }
        if (keycode == Keys.SHIFT_LEFT) {
            inputFlags.setRun(false);
        }
        if (keycode == Keys.SPACE) {
            inputFlags.setJump(false);
            player.setJustJumped(false);
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
