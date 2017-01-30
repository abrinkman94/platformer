package com.brinkman.platformer.spine2d.attachments;

/**
 * Created by Austin on 1/26/2017.
 */

import com.brinkman.platformer.spine2d.Skin;

/** The interface which can be implemented to customize creating and populating attachments.
 * <p>
 * See <a href='http://esotericsoftware.com/spine-loading-skeleton-data#AttachmentLoader'>Loading skeleton data</a> in the Spine
 * Runtimes Guide. */
public interface AttachmentLoader {
    /** @return May be null to not load the attachment. */
    public RegionAttachment newRegionAttachment (Skin skin, String name, String path);

    /** @return May be null to not load the attachment. */
    public MeshAttachment newMeshAttachment (Skin skin, String name, String path);

    /** @return May be null to not load the attachment. */
    public BoundingBoxAttachment newBoundingBoxAttachment (Skin skin, String name);

    /** @return May be null to not load the attachment. */
    public PathAttachment newPathAttachment (Skin skin, String name);
}
