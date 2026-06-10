package com.badlogic.tankstars;

import java.io.Serializable;
import java.util.Random;

public class Terrain implements Serializable {
    private static final long serialVersionUID = 1L;

    private float worldWidth;
    private float minHeight;
    private float maxHeight;
    private float[] heights;

    public Terrain() {
    }

    public Terrain(float worldWidth, int samples, float minHeight, float maxHeight, long seed) {
        this.worldWidth = worldWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.heights = new float[samples];
        generate(seed);
    }

    private void generate(long seed) {
        Random random = new Random(seed);
        for (int i = 0; i < heights.length; i++) {
            float t = i / (float) (heights.length - 1);
            float wave = (float) (Math.sin(t * Math.PI * 2.2f) * 38f + Math.sin(t * Math.PI * 5.1f) * 18f);
            float noise = (random.nextFloat() - 0.5f) * 20f;
            heights[i] = clamp(175f + wave + noise, minHeight, maxHeight);
        }
        smooth(2);
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float[] copyHeights() {
        float[] copy = new float[heights.length];
        System.arraycopy(heights, 0, copy, 0, heights.length);
        return copy;
    }

    public void setHeights(float worldWidth, float minHeight, float maxHeight, float[] heights) {
        this.worldWidth = worldWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.heights = new float[heights.length];
        System.arraycopy(heights, 0, this.heights, 0, heights.length);
    }

    public int getSampleCount() {
        return heights.length;
    }

    public float getHeightAt(float x) {
        if (heights == null || heights.length == 0) {
            return minHeight;
        }
        float clampedX = clamp(x, 0f, worldWidth);
        float scaled = clampedX / worldWidth * (heights.length - 1);
        int left = (int) Math.floor(scaled);
        int right = Math.min(heights.length - 1, left + 1);
        float alpha = scaled - left;
        return heights[left] * (1f - alpha) + heights[right] * alpha;
    }

    public float getSlopeAt(float x) {
        float step = worldWidth / (heights.length - 1);
        float h1 = getHeightAt(x - step);
        float h2 = getHeightAt(x + step);
        return (float) Math.atan2(h2 - h1, step * 2f);
    }

    public boolean createCrater(float centerX, float radius, float depth) {
        boolean changed = false;
        for (int i = 0; i < heights.length; i++) {
            float x = i / (float) (heights.length - 1) * worldWidth;
            float dist = Math.abs(x - centerX);
            if (dist <= radius) {
                float factor = 1f - (dist / radius);
                float next = clamp(heights[i] - depth * factor, minHeight * 0.5f, maxHeight);
                changed = changed || Math.abs(next - heights[i]) > 0.01f;
                heights[i] = next;
            }
        }
        smooth(1);
        return changed;
    }

    public float clampX(float x, float tankHalfWidth) {
        return clamp(x, tankHalfWidth, worldWidth - tankHalfWidth);
    }

    private void smooth(int passes) {
        for (int pass = 0; pass < passes; pass++) {
            float[] copy = copyHeights();
            for (int i = 1; i < heights.length - 1; i++) {
                heights[i] = (copy[i - 1] + copy[i] * 2f + copy[i + 1]) / 4f;
            }
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
