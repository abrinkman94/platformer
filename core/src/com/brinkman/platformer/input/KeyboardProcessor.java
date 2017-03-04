package com.brinkman.platformer.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.brinkman.platformer.component.physics.PhysicsComponent;
import com.brinkman.platformer.entity.Entity;
import com.brinkman.platformer.physics.ControlledBody;

/**
 * Created by Austin on 11/23/2016.
 */
public class KeyboardProcessor extends AbstractInputMappings implements InputProcessor {
    private final Entity entity;

    public KeyboardProcessor(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.LEFT) {
            left = true;
        } else if (keycode == Keys.RIGHT) {
            right = true;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            run = true;
        }

        if (keycode == Keys.SPACE) {
            ((ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class)).setJumping(true);
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.LEFT) {
            left = false;
        }
        if (keycode == Keys.RIGHT) {
            right = false;
        }
        if (keycode == Keys.SHIFT_LEFT) {
            run = false;
        }
        if (keycode == Keys.SPACE) {
            ((ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class)).setJumping(false);
            ((ControlledBody) entity.getComponents().getInstance(PhysicsComponent.class)).setJustJumped(false);
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
