package com.brinkman.platformer.spine2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.brinkman.platformer.spine2d.attachments.Attachment;
import com.brinkman.platformer.spine2d.attachments.MeshAttachment;
import com.brinkman.platformer.spine2d.attachments.RegionAttachment;
import com.brinkman.platformer.spine2d.attachments.SkeletonAttachment;

/**
 * Created by Austin on 1/26/2017.
 */
public class SkeletonRenderer<T extends Batch> {
    boolean premultipliedAlpha;

    public void draw (T batch, Skeleton skeleton) {
        boolean premultipliedAlpha = this.premultipliedAlpha;

        Array<Slot> drawOrder = skeleton.drawOrder;
        for (int i = 0, n = drawOrder.size; i < n; i++) {
            Slot slot = drawOrder.get(i);
            Attachment attachment = slot.attachment;
            if (attachment instanceof RegionAttachment) {
                RegionAttachment regionAttachment = (RegionAttachment)attachment;
                float[] vertices = regionAttachment.updateWorldVertices(slot, premultipliedAlpha);
                BlendMode blendMode = slot.data.getBlendMode();
                batch.setBlendFunction(blendMode.getSource(premultipliedAlpha), blendMode.getDest());
                batch.draw(regionAttachment.getRegion().getTexture(), vertices, 0, 20);

            } else if (attachment instanceof MeshAttachment) {
                throw new RuntimeException("SkeletonMeshRenderer is required to render meshes.");

            } else if (attachment instanceof SkeletonAttachment) {
                Skeleton attachmentSkeleton = ((SkeletonAttachment)attachment).getSkeleton();
                if (attachmentSkeleton == null) continue;
                Bone bone = slot.getBone();
                Bone rootBone = attachmentSkeleton.getRootBone();
                float oldScaleX = rootBone.getScaleX();
                float oldScaleY = rootBone.getScaleY();
                float oldRotation = rootBone.getRotation();
                attachmentSkeleton.setPosition(bone.getWorldX(), bone.getWorldY());
                // rootBone.setScaleX(1 + bone.getWorldScaleX() - oldScaleX);
                // rootBone.setScaleY(1 + bone.getWorldScaleY() - oldScaleY);
                // Set shear.
                rootBone.setRotation(oldRotation + bone.getWorldRotationX());
                attachmentSkeleton.updateWorldTransform();

                draw(batch, attachmentSkeleton);

                attachmentSkeleton.setX(0);
                attachmentSkeleton.setY(0);
                rootBone.setScaleX(oldScaleX);
                rootBone.setScaleY(oldScaleY);
                rootBone.setRotation(oldRotation);
            }
        }
    }

    public void setPremultipliedAlpha (boolean premultipliedAlpha) {
        this.premultipliedAlpha = premultipliedAlpha;
    }
}