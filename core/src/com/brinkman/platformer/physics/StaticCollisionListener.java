package com.brinkman.platformer.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.brinkman.platformer.component.ControlledPhysicsComponent;
import com.brinkman.platformer.entity.Entity;

import static com.brinkman.platformer.util.Constants.GRAVITY;

/**
 * @author Caleb Brinkman
 */
public class StaticCollisionListener<T extends Entity> implements CollisionListener<T>{
    private static final Vector2 TEMP_VECTOR_1 = new Vector2();
    private static final Vector2 TEMP_VECTOR_2 = new Vector2();
    private final ControlledBody body;

    public StaticCollisionListener(ControlledBody body) { this.body = body; }

    @Override
    public void onCollision(T other) {
        Body otherBody = other.getComponents().getInstance(ControlledPhysicsComponent.class);
        if ((body != null) && (otherBody != null)) {
            ((Rectangle) body.getBounds()).getCenter(TEMP_VECTOR_1);
            ((Rectangle) otherBody.getBounds()).getCenter(TEMP_VECTOR_2);
            // Get the absolute value of horizontal overlap between the entity and map tile
            // Save signed value of distance for later
            float horizontalDistance = TEMP_VECTOR_2.x - TEMP_VECTOR_1.x;
            float entityHalfWidth = ((Rectangle) body.getBounds()).width / 2;
            float mapHalfWidth = ((Rectangle) otherBody.getBounds()).width / 2;
            float horizontalOverlap = (entityHalfWidth + mapHalfWidth) - Math.abs(horizontalDistance);

            // Get the absolute value of the vertical overlap between the entity and the map time
            // Save signed value of distance for later
            float verticalDistance = TEMP_VECTOR_2.y - TEMP_VECTOR_1.y;
            float entityHalfHeight = ((Rectangle) body.getBounds()).height / 2;
            float mapHalfHeight = ((Rectangle) otherBody.getBounds()).height / 2;
            float verticalOverlap = (entityHalfHeight + mapHalfHeight) - Math.abs(verticalDistance);

            // Move the entity on the axis which has the least overlap.
            // The direction that the entity will move is determined by the sign of the distance between the centers
            Vector2 position = body.getPosition();
            Vector2 velocity = body.getVelocity();

            if (horizontalOverlap < verticalOverlap) {
                if (horizontalDistance > 0) {
                    body.setTouchingRightWall(true);
                    position.x -= horizontalOverlap;
                    velocity.x = 0;
                } else {
                    body.setTouchingLeftWall(true);
                    position.x += horizontalOverlap;
                    velocity.x = 0;
                }
            } else {
                if (verticalDistance > 0) {
                    position.y -= verticalOverlap;
                    velocity.y -= GRAVITY * 3;
                } else {
                    position.y += verticalOverlap;
                    velocity.y = 0;
                    body.setGrounded(true);
                }
            }
        }
    }

}
