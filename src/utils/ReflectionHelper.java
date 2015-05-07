package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectionHelper {
    private static final Logger LOGGER = LogManager.getLogger(ReflectionHelper.class);

    public static Object createInstance(String className) {
        try {
            return Class.forName(className).newInstance();
        } catch (IllegalArgumentException | SecurityException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public static boolean isList(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            return field.getType().equals(List.class);
        } catch (NoSuchFieldException e) {
            LOGGER.error(e);
            return false;
        }
    }

    public static void setFieldValue(Object object, String fieldName, String value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType().equals(String.class)) {
                field.set(object, value);
            } else if (field.getType().equals(int.class)) {
                field.set(object, Integer.decode(value));
            }

            field.setAccessible(false);
        } catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.error(e);
        }
    }

    public static void addToList(Object object, String name, String value) {
        try {
            Field field = object.getClass().getField(name);
            Method method = field.getType().getMethod("add", Object.class);
            method.invoke(field.get(object), value);
        } catch (NoSuchFieldException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            LOGGER.error(e);
        }
    }
}
