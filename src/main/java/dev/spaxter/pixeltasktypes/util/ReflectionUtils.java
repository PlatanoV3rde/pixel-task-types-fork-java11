package dev.spaxter.pixeltasktypes.util;

/**
 * Utilidades de reflection sin inicializar clases.
 */
public final class ReflectionUtils {
    private ReflectionUtils() {}

    /**
     * Comprueba si una clase existe en classpath sin inicializarla.
     *
     * @param className FQCN a comprobar
     * @return true si la clase existe, false en caso contrario
     */
    public static boolean classExists(String className) {
        try {
            Class.forName(className, false, ReflectionUtils.class.getClassLoader());
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
