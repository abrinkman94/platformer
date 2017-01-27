package com.brinkman.platformer.spine;

/**
 * Created by Austin on 1/26/2017.
 */

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.*;
import com.brinkman.platformer.spine.Animation.RotateTimeline;
import com.brinkman.platformer.spine.PathConstraintData.PositionMode;
import com.brinkman.platformer.spine.PathConstraintData.RotateMode;
import com.brinkman.platformer.spine.PathConstraintData.SpacingMode;
import com.brinkman.platformer.spine.SkeletonJson.LinkedMesh;
import com.brinkman.platformer.spine.attachments.*;

import java.io.EOFException;
import java.io.IOException;

/** Loads skeleton data in the Spine binary format.
 * <p>
 * See <a href="http://esotericsoftware.com/spine-binary-format">Spine binary format</a> and
 * <a href="http://esotericsoftware.com/spine-loading-skeleton-data#JSON-and-binary-data">JSON and binary data</a> in the Spine
 * Runtimes Guide. */
public class SkeletonBinary {
    static public final int BONE_ROTATE = 0;
    static public final int BONE_TRANSLATE = 1;
    static public final int BONE_SCALE = 2;
    static public final int BONE_SHEAR = 3;

    static public final int SLOT_ATTACHMENT = 0;
    static public final int SLOT_COLOR = 1;

    static public final int PATH_POSITION = 0;
    static public final int PATH_SPACING = 1;
    static public final int PATH_MIX = 2;

    static public final int CURVE_LINEAR = 0;
    static public final int CURVE_STEPPED = 1;
    static public final int CURVE_BEZIER = 2;

    static private final Color tempColor = new Color();

    private final AttachmentLoader attachmentLoader;
    private float scale = 1;
    private Array<LinkedMesh> linkedMeshes = new Array();

    public SkeletonBinary (TextureAtlas atlas) {
        attachmentLoader = new AtlasAttachmentLoader(atlas);
    }

    public SkeletonBinary (AttachmentLoader attachmentLoader) {
        if (attachmentLoader == null) throw new IllegalArgumentException("attachmentLoader cannot be null.");
        this.attachmentLoader = attachmentLoader;
    }

    /** Scales bone positions, image sizes, and translations as they are loaded. This allows different size images to be used at
     * runtime than were used in Spine.
     * <p>
     * See <a href="http://esotericsoftware.com/spine-loading-skeleton-data#Scaling">Scaling</a> in the Spine Runtimes Guide. */
    public float getScale () {
        return scale;
    }

    public void setScale (float scale) {
        this.scale = scale;
    }

    public SkeletonData readSkeletonData (FileHandle file) {
        if (file == null) throw new IllegalArgumentException("file cannot be null.");

        float scale = this.scale;

        SkeletonData skeletonData = new SkeletonData();
        skeletonData.name = file.nameWithoutExtension();

        DataInput input = new DataInput(file.read(512)) {
            private char[] chars = new char[32];

            public String readString () throws IOException {
                int byteCount = readInt(true);
                switch (byteCount) {
                    case 0:
                        return null;
                    case 1:
                        return "";
                }
                byteCount--;
                if (chars.length < byteCount) chars = new char[byteCount];
                char[] chars = this.chars;
                int charCount = 0;
                for (int i = 0; i < byteCount;) {
                    int b = read();
                    switch (b >> 4) {
                        case -1:
                            throw new EOFException();
                        case 12:
                        case 13:
                            chars[charCount++] = (char)((b & 0x1F) << 6 | read() & 0x3F);
                            i += 2;
                            break;
                        case 14:
                            chars[charCount++] = (char)((b & 0x0F) << 12 | (read() & 0x3F) << 6 | read() & 0x3F);
                            i += 3;
                            break;
                        default:
                            chars[charCount++] = (char)b;
                            i++;
                    }
                }
                return new String(chars, 0, charCount);
            }
        };
        try {
            skeletonData.hash = input.readString();
            if (skeletonData.hash.isEmpty()) skeletonData.hash = null;
            skeletonData.version = input.readString();
            if (skeletonData.version.isEmpty()) skeletonData.version = null;
            skeletonData.width = input.readFloat();
            skeletonData.height = input.readFloat();

            boolean nonessential = input.readBoolean();

            if (nonessential) {
                skeletonData.fps = input.readFloat();
                skeletonData.imagesPath = input.readString();
                if (skeletonData.imagesPath.isEmpty()) skeletonData.imagesPath = null;
            }

            // Bones.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                String name = input.readString();
                BoneData parent = i == 0 ? null : skeletonData.bones.get(input.readInt(true));
                BoneData data = new BoneData(i, name, parent);
                data.rotation = input.readFloat();
                data.x = input.readFloat() * scale;
                data.y = input.readFloat() * scale;
                data.scaleX = input.readFloat();
                data.scaleY = input.readFloat();
                data.shearX = input.readFloat();
                data.shearY = input.readFloat();
                data.length = input.readFloat() * scale;
                data.transformMode = BoneData.TransformMode.values[input.readInt(true)];
                if (nonessential) Color.rgba8888ToColor(data.color, input.readInt());
                skeletonData.bones.add(data);
            }

            // Slots.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                String slotName = input.readString();
                BoneData boneData = skeletonData.bones.get(input.readInt(true));
                SlotData data = new SlotData(i, slotName, boneData);
                Color.rgba8888ToColor(data.color, input.readInt());
                data.attachmentName = input.readString();
                data.blendMode = BlendMode.values[input.readInt(true)];
                skeletonData.slots.add(data);
            }

            // IK constraints.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                IkConstraintData data = new IkConstraintData(input.readString());
                data.order = input.readInt(true);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++)
                    data.bones.add(skeletonData.bones.get(input.readInt(true)));
                data.target = skeletonData.bones.get(input.readInt(true));
                data.mix = input.readFloat();
                data.bendDirection = input.readByte();
                skeletonData.ikConstraints.add(data);
            }

            // Transform constraints.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                TransformConstraintData data = new TransformConstraintData(input.readString());
                data.order = input.readInt(true);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++)
                    data.bones.add(skeletonData.bones.get(input.readInt(true)));
                data.target = skeletonData.bones.get(input.readInt(true));
                data.offsetRotation = input.readFloat();
                data.offsetX = input.readFloat() * scale;
                data.offsetY = input.readFloat() * scale;
                data.offsetScaleX = input.readFloat();
                data.offsetScaleY = input.readFloat();
                data.offsetShearY = input.readFloat();
                data.rotateMix = input.readFloat();
                data.translateMix = input.readFloat();
                data.scaleMix = input.readFloat();
                data.shearMix = input.readFloat();
                skeletonData.transformConstraints.add(data);
            }

            // Path constraints.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                PathConstraintData data = new PathConstraintData(input.readString());
                data.order = input.readInt(true);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++)
                    data.bones.add(skeletonData.bones.get(input.readInt(true)));
                data.target = skeletonData.slots.get(input.readInt(true));
                data.positionMode = PositionMode.values[input.readInt(true)];
                data.spacingMode = SpacingMode.values[input.readInt(true)];
                data.rotateMode = RotateMode.values[input.readInt(true)];
                data.offsetRotation = input.readFloat();
                data.position = input.readFloat();
                if (data.positionMode == PositionMode.fixed) data.position *= scale;
                data.spacing = input.readFloat();
                if (data.spacingMode == SpacingMode.length || data.spacingMode == SpacingMode.fixed) data.spacing *= scale;
                data.rotateMix = input.readFloat();
                data.translateMix = input.readFloat();
                skeletonData.pathConstraints.add(data);
            }

            // Default skin.
            Skin defaultSkin = readSkin(input, "default", nonessential);
            if (defaultSkin != null) {
                skeletonData.defaultSkin = defaultSkin;
                skeletonData.skins.add(defaultSkin);
            }

            // Skins.
            for (int i = 0, n = input.readInt(true); i < n; i++)
                skeletonData.skins.add(readSkin(input, input.readString(), nonessential));

            // Linked meshes.
            for (int i = 0, n = linkedMeshes.size; i < n; i++) {
                LinkedMesh linkedMesh = linkedMeshes.get(i);
                Skin skin = linkedMesh.skin == null ? skeletonData.getDefaultSkin() : skeletonData.findSkin(linkedMesh.skin);
                if (skin == null) throw new SerializationException("Skin not found: " + linkedMesh.skin);
                Attachment parent = skin.getAttachment(linkedMesh.slotIndex, linkedMesh.parent);
                if (parent == null) throw new SerializationException("Parent mesh not found: " + linkedMesh.parent);
                linkedMesh.mesh.setParentMesh((MeshAttachment)parent);
                linkedMesh.mesh.updateUVs();
            }
            linkedMeshes.clear();

            // Events.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                EventData data = new EventData(input.readString());
                data.intValue = input.readInt(false);
                data.floatValue = input.readFloat();
                data.stringValue = input.readString();
                skeletonData.events.add(data);
            }

            // Animations.
            for (int i = 0, n = input.readInt(true); i < n; i++)
                readAnimation(input.readString(), input, skeletonData);

        } catch (IOException ex) {
            throw new SerializationException("Error reading skeleton file.", ex);
        } finally {
            try {
                input.close();
            } catch (IOException ignored) {
            }
        }

        skeletonData.bones.shrink();
        skeletonData.slots.shrink();
        skeletonData.skins.shrink();
        skeletonData.events.shrink();
        skeletonData.animations.shrink();
        skeletonData.ikConstraints.shrink();
        return skeletonData;
    }

    /** @return May be null. */
    private Skin readSkin (DataInput input, String skinName, boolean nonessential) throws IOException {
        int slotCount = input.readInt(true);
        if (slotCount == 0) return null;
        Skin skin = new Skin(skinName);
        for (int i = 0; i < slotCount; i++) {
            int slotIndex = input.readInt(true);
            for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
                String name = input.readString();
                Attachment attachment = readAttachment(input, skin, slotIndex, name, nonessential);
                if (attachment != null) skin.addAttachment(slotIndex, name, attachment);
            }
        }
        return skin;
    }

    private Attachment readAttachment (DataInput input, Skin skin, int slotIndex, String attachmentName, boolean nonessential)
            throws IOException {
        float scale = this.scale;

        String name = input.readString();
        if (name == null) name = attachmentName;

        AttachmentType type = AttachmentType.values[input.readByte()];
        switch (type) {
            case region: {
                String path = input.readString();
                float rotation = input.readFloat();
                float x = input.readFloat();
                float y = input.readFloat();
                float scaleX = input.readFloat();
                float scaleY = input.readFloat();
                float width = input.readFloat();
                float height = input.readFloat();
                int color = input.readInt();

                if (path == null) path = name;
                RegionAttachment region = attachmentLoader.newRegionAttachment(skin, name, path);
                if (region == null) return null;
                region.setPath(path);
                region.setX(x * scale);
                region.setY(y * scale);
                region.setScaleX(scaleX);
                region.setScaleY(scaleY);
                region.setRotation(rotation);
                region.setWidth(width * scale);
                region.setHeight(height * scale);
                Color.rgba8888ToColor(region.getColor(), color);
                region.updateOffset();
                return region;
            }
            case boundingbox: {
                int vertexCount = input.readInt(true);
                Vertices vertices = readVertices(input, vertexCount);
                int color = nonessential ? input.readInt() : 0;

                BoundingBoxAttachment box = attachmentLoader.newBoundingBoxAttachment(skin, name);
                if (box == null) return null;
                box.setWorldVerticesLength(vertexCount << 1);
                box.setVertices(vertices.vertices);
                box.setBones(vertices.bones);
                if (nonessential) Color.rgba8888ToColor(box.getColor(), color);
                return box;
            }
            case mesh: {
                String path = input.readString();
                int color = input.readInt();
                int vertexCount = input.readInt(true);
                float[] uvs = readFloatArray(input, vertexCount << 1, 1);
                short[] triangles = readShortArray(input);
                Vertices vertices = readVertices(input, vertexCount);
                int hullLength = input.readInt(true);
                short[] edges = null;
                float width = 0, height = 0;
                if (nonessential) {
                    edges = readShortArray(input);
                    width = input.readFloat();
                    height = input.readFloat();
                }

                if (path == null) path = name;
                MeshAttachment mesh = attachmentLoader.newMeshAttachment(skin, name, path);
                if (mesh == null) return null;
                mesh.setPath(path);
                Color.rgba8888ToColor(mesh.getColor(), color);
                mesh.setBones(vertices.bones);
                mesh.setVertices(vertices.vertices);
                mesh.setWorldVerticesLength(vertexCount << 1);
                mesh.setTriangles(triangles);
                mesh.setRegionUVs(uvs);
                mesh.updateUVs();
                mesh.setHullLength(hullLength << 1);
                if (nonessential) {
                    mesh.setEdges(edges);
                    mesh.setWidth(width * scale);
                    mesh.setHeight(height * scale);
                }
                return mesh;
            }
            case linkedmesh: {
                String path = input.readString();
                int color = input.readInt();
                String skinName = input.readString();
                String parent = input.readString();
                boolean inheritDeform = input.readBoolean();
                float width = 0, height = 0;
                if (nonessential) {
                    width = input.readFloat();
                    height = input.readFloat();
                }

                if (path == null) path = name;
                MeshAttachment mesh = attachmentLoader.newMeshAttachment(skin, name, path);
                if (mesh == null) return null;
                mesh.setPath(path);
                Color.rgba8888ToColor(mesh.getColor(), color);
                mesh.setInheritDeform(inheritDeform);
                if (nonessential) {
                    mesh.setWidth(width * scale);
                    mesh.setHeight(height * scale);
                }
                linkedMeshes.add(new LinkedMesh(mesh, skinName, slotIndex, parent));
                return mesh;
            }
            case path: {
                boolean closed = input.readBoolean();
                boolean constantSpeed = input.readBoolean();
                int vertexCount = input.readInt(true);
                Vertices vertices = readVertices(input, vertexCount);
                float[] lengths = new float[vertexCount / 3];
                for (int i = 0, n = lengths.length; i < n; i++)
                    lengths[i] = input.readFloat() * scale;
                int color = nonessential ? input.readInt() : 0;

                PathAttachment path = attachmentLoader.newPathAttachment(skin, name);
                if (path == null) return null;
                path.setClosed(closed);
                path.setConstantSpeed(constantSpeed);
                path.setWorldVerticesLength(vertexCount << 1);
                path.setVertices(vertices.vertices);
                path.setBones(vertices.bones);
                path.setLengths(lengths);
                if (nonessential) Color.rgba8888ToColor(path.getColor(), color);
                return path;
            }
        }
        return null;
    }

    private Vertices readVertices (DataInput input, int vertexCount) throws IOException {
        int verticesLength = vertexCount << 1;
        Vertices vertices = new Vertices();
        if (!input.readBoolean()) {
            vertices.vertices = readFloatArray(input, verticesLength, scale);
            return vertices;
        }
        FloatArray weights = new FloatArray(verticesLength * 3 * 3);
        IntArray bonesArray = new IntArray(verticesLength * 3);
        for (int i = 0; i < vertexCount; i++) {
            int boneCount = input.readInt(true);
            bonesArray.add(boneCount);
            for (int ii = 0; ii < boneCount; ii++) {
                bonesArray.add(input.readInt(true));
                weights.add(input.readFloat() * scale);
                weights.add(input.readFloat() * scale);
                weights.add(input.readFloat());
            }
        }
        vertices.vertices = weights.toArray();
        vertices.bones = bonesArray.toArray();
        return vertices;
    }

    private float[] readFloatArray (DataInput input, int n, float scale) throws IOException {
        float[] array = new float[n];
        if (scale == 1) {
            for (int i = 0; i < n; i++)
                array[i] = input.readFloat();
        } else {
            for (int i = 0; i < n; i++)
                array[i] = input.readFloat() * scale;
        }
        return array;
    }

    private short[] readShortArray (DataInput input) throws IOException {
        int n = input.readInt(true);
        short[] array = new short[n];
        for (int i = 0; i < n; i++)
            array[i] = input.readShort();
        return array;
    }

    private void readAnimation (String name, DataInput input, SkeletonData skeletonData) {
        Array<Animation.Timeline> timelines = new Array();
        float scale = this.scale;
        float duration = 0;

        try {
            // Slot timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                int slotIndex = input.readInt(true);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
                    int timelineType = input.readByte();
                    int frameCount = input.readInt(true);
                    switch (timelineType) {
                        case SLOT_COLOR: {
                            Animation.ColorTimeline timeline = new Animation.ColorTimeline(frameCount);
                            timeline.slotIndex = slotIndex;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                                float time = input.readFloat();
                                Color.rgba8888ToColor(tempColor, input.readInt());
                                timeline.setFrame(frameIndex, time, tempColor.r, tempColor.g, tempColor.b, tempColor.a);
                                if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                            }
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.ColorTimeline.ENTRIES]);
                            break;
                        }
                        case SLOT_ATTACHMENT:
                            Animation.AttachmentTimeline timeline = new Animation.AttachmentTimeline(frameCount);
                            timeline.slotIndex = slotIndex;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++)
                                timeline.setFrame(frameIndex, input.readFloat(), input.readString());
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[frameCount - 1]);
                            break;
                    }
                }
            }

            // Bone timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                int boneIndex = input.readInt(true);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
                    int timelineType = input.readByte();
                    int frameCount = input.readInt(true);
                    switch (timelineType) {
                        case BONE_ROTATE: {
                            RotateTimeline timeline = new RotateTimeline(frameCount);
                            timeline.boneIndex = boneIndex;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                                timeline.setFrame(frameIndex, input.readFloat(), input.readFloat());
                                if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                            }
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * RotateTimeline.ENTRIES]);
                            break;
                        }
                        case BONE_TRANSLATE:
                        case BONE_SCALE:
                        case BONE_SHEAR: {
                            Animation.TranslateTimeline timeline;
                            float timelineScale = 1;
                            if (timelineType == BONE_SCALE)
                                timeline = new Animation.ScaleTimeline(frameCount);
                            else if (timelineType == BONE_SHEAR)
                                timeline = new Animation.ShearTimeline(frameCount);
                            else {
                                timeline = new Animation.TranslateTimeline(frameCount);
                                timelineScale = scale;
                            }
                            timeline.boneIndex = boneIndex;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                                timeline.setFrame(frameIndex, input.readFloat(), input.readFloat() * timelineScale,
                                        input.readFloat() * timelineScale);
                                if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                            }
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.TranslateTimeline.ENTRIES]);
                            break;
                        }
                    }
                }
            }

            // IK constraint timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                int index = input.readInt(true);
                int frameCount = input.readInt(true);
                Animation.IkConstraintTimeline timeline = new Animation.IkConstraintTimeline(frameCount);
                timeline.ikConstraintIndex = index;
                for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                    timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readByte());
                    if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                }
                timelines.add(timeline);
                duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.IkConstraintTimeline.ENTRIES]);
            }

            // Transform constraint timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                int index = input.readInt(true);
                int frameCount = input.readInt(true);
                Animation.TransformConstraintTimeline timeline = new Animation.TransformConstraintTimeline(frameCount);
                timeline.transformConstraintIndex = index;
                for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                    timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat(),
                            input.readFloat());
                    if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                }
                timelines.add(timeline);
                duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.TransformConstraintTimeline.ENTRIES]);
            }

            // Path constraint timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                int index = input.readInt(true);
                PathConstraintData data = skeletonData.pathConstraints.get(index);
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
                    int timelineType = input.readByte();
                    int frameCount = input.readInt(true);
                    switch (timelineType) {
                        case PATH_POSITION:
                        case PATH_SPACING: {
                            Animation.PathConstraintPositionTimeline timeline;
                            float timelineScale = 1;
                            if (timelineType == PATH_SPACING) {
                                timeline = new Animation.PathConstraintSpacingTimeline(frameCount);
                                if (data.spacingMode == SpacingMode.length || data.spacingMode == SpacingMode.fixed) timelineScale = scale;
                            } else {
                                timeline = new Animation.PathConstraintPositionTimeline(frameCount);
                                if (data.positionMode == PositionMode.fixed) timelineScale = scale;
                            }
                            timeline.pathConstraintIndex = index;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                                timeline.setFrame(frameIndex, input.readFloat(), input.readFloat() * timelineScale);
                                if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                            }
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.PathConstraintPositionTimeline.ENTRIES]);
                            break;
                        }
                        case PATH_MIX: {
                            Animation.PathConstraintMixTimeline timeline = new Animation.PathConstraintMixTimeline(frameCount);
                            timeline.pathConstraintIndex = index;
                            for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                                timeline.setFrame(frameIndex, input.readFloat(), input.readFloat(), input.readFloat());
                                if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                            }
                            timelines.add(timeline);
                            duration = Math.max(duration, timeline.getFrames()[(frameCount - 1) * Animation.PathConstraintMixTimeline.ENTRIES]);
                            break;
                        }
                    }
                }
            }

            // Deform timelines.
            for (int i = 0, n = input.readInt(true); i < n; i++) {
                Skin skin = skeletonData.skins.get(input.readInt(true));
                for (int ii = 0, nn = input.readInt(true); ii < nn; ii++) {
                    int slotIndex = input.readInt(true);
                    for (int iii = 0, nnn = input.readInt(true); iii < nnn; iii++) {
                        VertexAttachment attachment = (VertexAttachment)skin.getAttachment(slotIndex, input.readString());
                        boolean weighted = attachment.getBones() != null;
                        float[] vertices = attachment.getVertices();
                        int deformLength = weighted ? vertices.length / 3 * 2 : vertices.length;

                        int frameCount = input.readInt(true);
                        Animation.DeformTimeline timeline = new Animation.DeformTimeline(frameCount);
                        timeline.slotIndex = slotIndex;
                        timeline.attachment = attachment;

                        for (int frameIndex = 0; frameIndex < frameCount; frameIndex++) {
                            float time = input.readFloat();
                            float[] deform;
                            int end = input.readInt(true);
                            if (end == 0)
                                deform = weighted ? new float[deformLength] : vertices;
                            else {
                                deform = new float[deformLength];
                                int start = input.readInt(true);
                                end += start;
                                if (scale == 1) {
                                    for (int v = start; v < end; v++)
                                        deform[v] = input.readFloat();
                                } else {
                                    for (int v = start; v < end; v++)
                                        deform[v] = input.readFloat() * scale;
                                }
                                if (!weighted) {
                                    for (int v = 0, vn = deform.length; v < vn; v++)
                                        deform[v] += vertices[v];
                                }
                            }

                            timeline.setFrame(frameIndex, time, deform);
                            if (frameIndex < frameCount - 1) readCurve(input, frameIndex, timeline);
                        }
                        timelines.add(timeline);
                        duration = Math.max(duration, timeline.getFrames()[frameCount - 1]);
                    }
                }
            }

            // Draw order timeline.
            int drawOrderCount = input.readInt(true);
            if (drawOrderCount > 0) {
                Animation.DrawOrderTimeline timeline = new Animation.DrawOrderTimeline(drawOrderCount);
                int slotCount = skeletonData.slots.size;
                for (int i = 0; i < drawOrderCount; i++) {
                    float time = input.readFloat();
                    int offsetCount = input.readInt(true);
                    int[] drawOrder = new int[slotCount];
                    for (int ii = slotCount - 1; ii >= 0; ii--)
                        drawOrder[ii] = -1;
                    int[] unchanged = new int[slotCount - offsetCount];
                    int originalIndex = 0, unchangedIndex = 0;
                    for (int ii = 0; ii < offsetCount; ii++) {
                        int slotIndex = input.readInt(true);
                        // Collect unchanged items.
                        while (originalIndex != slotIndex)
                            unchanged[unchangedIndex++] = originalIndex++;
                        // Set changed items.
                        drawOrder[originalIndex + input.readInt(true)] = originalIndex++;
                    }
                    // Collect remaining unchanged items.
                    while (originalIndex < slotCount)
                        unchanged[unchangedIndex++] = originalIndex++;
                    // Fill in unchanged items.
                    for (int ii = slotCount - 1; ii >= 0; ii--)
                        if (drawOrder[ii] == -1) drawOrder[ii] = unchanged[--unchangedIndex];
                    timeline.setFrame(i, time, drawOrder);
                }
                timelines.add(timeline);
                duration = Math.max(duration, timeline.getFrames()[drawOrderCount - 1]);
            }

            // Event timeline.
            int eventCount = input.readInt(true);
            if (eventCount > 0) {
                Animation.EventTimeline timeline = new Animation.EventTimeline(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    float time = input.readFloat();
                    EventData eventData = skeletonData.events.get(input.readInt(true));
                    Event event = new Event(time, eventData);
                    event.intValue = input.readInt(false);
                    event.floatValue = input.readFloat();
                    event.stringValue = input.readBoolean() ? input.readString() : eventData.stringValue;
                    timeline.setFrame(i, event);
                }
                timelines.add(timeline);
                duration = Math.max(duration, timeline.getFrames()[eventCount - 1]);
            }
        } catch (IOException ex) {
            throw new SerializationException("Error reading skeleton file.", ex);
        }

        timelines.shrink();
        skeletonData.animations.add(new Animation(name, timelines, duration));

    }

    private void readCurve (DataInput input, int frameIndex, Animation.CurveTimeline timeline) throws IOException {
        switch (input.readByte()) {
            case CURVE_STEPPED:
                timeline.setStepped(frameIndex);
                break;
            case CURVE_BEZIER:
                setCurve(timeline, frameIndex, input.readFloat(), input.readFloat(), input.readFloat(), input.readFloat());
                break;
        }
    }

    void setCurve (Animation.CurveTimeline timeline, int frameIndex, float cx1, float cy1, float cx2, float cy2) {
        timeline.setCurve(frameIndex, cx1, cy1, cx2, cy2);
    }

    static class Vertices {
        int[] bones;
        float[] vertices;
    }
}
