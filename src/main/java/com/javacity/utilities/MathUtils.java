package com.javacity.utilities;

import com.jme3.math.Vector3f;
import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Vector3f lerp(Vector3f a, Vector3f b, float t) {
        return new Vector3f(
            lerp(a.x, b.x, t),
            lerp(a.y, b.y, t),
            lerp(a.z, b.z, t)
        );
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float angleBetween(Vector3f a, Vector3f b) {
        Vector3f aNorm = a.normalize();
        Vector3f bNorm = b.normalize();
        float dot = clamp(aNorm.dot(bNorm), -1f, 1f);
        return (float) Math.acos(dot);
    }

    public static Vector3f randomPointInRadius(Vector3f center, float radius) {
        float angle = (float) (random.nextFloat() * Math.PI * 2);
        float r = radius * (float) Math.sqrt(random.nextFloat());
        float x = r * (float) Math.cos(angle);
        float z = r * (float) Math.sin(angle);
        return new Vector3f(center.x + x, center.y, center.z + z);
    }
}
