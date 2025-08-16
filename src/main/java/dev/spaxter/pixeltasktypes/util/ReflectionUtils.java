package dev.spaxter.pixeltasktypes.util;

/**
 * Utilidades de reflection sin inicializar clases.
 */
public final class ReflectionUtils {
    private ReflectionUtils() {}

    public static boolean classExists(String className) {
        try {
            Class.forName(className, false, ReflectionUtils.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
