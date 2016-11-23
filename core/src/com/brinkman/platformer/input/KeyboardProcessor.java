package com.brinkman.platformer.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.brinkman.platformer.entity.Player;

/**
 * Created by Austin on 11/23/2016.
 */
public class KeyboardProcessor implements InputProcessor {
    private InputFlags inputFlags;
    private Player player;

    public KeyboardProcessor(InputFlags inputFlags, Player player) {
        this.inputFlags = inputFlags;
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            inputFlags.setLeft(true);
        } else if (keycode == Input.Keys.RIGHT) {
            inputFlags.setRight(true);
        }

        if (keycode == Input.Keys.SHIFT_LEFT) {
            inputFlags.setRun(true);
        }

        if (keycode == Input.Keys.SPACE) {
            inputFlags.setJump(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT) {
            inputFlags.setLeft(false);
        }
        if (keycode == Input.Keys.RIGHT) {
            inputFlags.setRight(false);
        }
        if (keycode == Input.Keys.SHIFT_LEFT) {
            inputFlags.setRun(false);
        }
        if (keycode == Input.Keys.SPACE) {
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
