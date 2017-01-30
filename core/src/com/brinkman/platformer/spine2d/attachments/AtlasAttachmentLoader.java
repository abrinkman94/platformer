package com.brinkman.platformer.spine2d.attachments;

/**
 * Created by Austin on 1/26/2017.
 */

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.brinkman.platformer.spine2d.Skin;

/** An {@link AttachmentLoader} that configures attachments using texture regions from an {@link Atlas}.
 * <p>
 * See <a href='http://esotericsoftware.com/spine-loading-skeleton-data#JSON-and-binary-data'>Loading skeleton data</a> in the
 * Spine Runtimes Guide. */
@SuppressWarnings("javadoc")
public class AtlasAttachmentLoader implements AttachmentLoader {
    private TextureAtlas atlas;

    public AtlasAttachmentLoader (TextureAtlas atlas) {
        if (atlas == null) throw new IllegalArgumentException("atlas cannot be null.");
        this.atlas = atlas;
    }

    public RegionAttachment newRegionAttachment (Skin skin, String name, String path) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(path);
        if (region == null) throw new RuntimeException("Region not found in atlas: " + path + " (region attachment: " + name + ")");
        RegionAttachment attachment = new RegionAttachment(name);
        attachment.setRegion(region);
        return attachment;
    }

    public MeshAttachment newMeshAttachment (Skin skin, String name, String path) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(path);
        if (region == null) throw new RuntimeException("Region not found in atlas: " + path + " (mesh attachment: " + name + ")");
        MeshAttachment attachment = new MeshAttachment(name);
        attachment.setRegion(region);
        return attachment;
    }

    public BoundingBoxAttachment newBoundingBoxAttachment (Skin skin, String name) {
        return new BoundingBoxAttachment(name);
    }

    public PathAttachment newPathAttachment (Skin skin, String name) {
        return new PathAttachment(name);
    }
}