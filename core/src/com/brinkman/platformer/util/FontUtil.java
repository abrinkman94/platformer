package com.brinkman.platformer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/**
 * Created by Austin on 8/23/2016.
 */
public class FontUtil
{

    private static FreeTypeFontGenerator generator;

    public static BitmapFont getFont(String fontLocation, Color color, int size) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal(fontLocation));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = color;
        return generator.generateFont(parameter);
    }

    public static void dispose() {
        generator.dispose();
    }
}
