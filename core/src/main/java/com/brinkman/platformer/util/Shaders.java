package com.brinkman.platformer.util;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;

public final class Shaders {

    public static final String PIXEL_SHADER_LOCATION = "com/brinkman/platformer/shaders/PixelShader.glsl";
    public static final String VERTEX_SHADER_LOCATION = "/com/brinkman/platformer/shaders/VertexShader.glsl";

    private Shaders() { }

    public static ShaderProgram load() throws IOException {
        URL vertexURL = Resources.getResource(VERTEX_SHADER_LOCATION);
        URL pixelURL = Resources.getResource(PIXEL_SHADER_LOCATION);

        return load(pixelURL, vertexURL);
    }

    public static ShaderProgram load(URL vertexUrl, URL pixelUrl) throws IOException {
        String vertexShader;
        String pixelShader;

        try {
            vertexShader = Resources.toString(vertexUrl, Charsets.UTF_8);
            pixelShader = Resources.toString(pixelUrl, Charsets.UTF_8);
        } catch (IOException e) {
            throw e;
        }

        return new ShaderProgram(vertexShader, pixelShader);
    }
}


