package xyz.e3ndr.reflectionlib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xyz.e3ndr.reflectionlib.helpers.AccessHelper;

public class ReflectionLib {

    public static Field deepFieldSearch(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot find field: " + fieldName);
        } else {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                return deepFieldSearch(clazz.getSuperclass(), fieldName);
            }
        }
    }

    public static Method deepMethodSearch(Class<?> clazz, String methodName, Class<?>[] parameters) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot find field: " + methodName);
        } else {
            try {
                return clazz.getDeclaredMethod(methodName, parameters);
            } catch (NoSuchMethodException e) {
                return deepMethodSearch(clazz.getSuperclass(), methodName, parameters);
            }
        }
    }

    public static void setValue(Object instance, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = deepFieldSearch(instance.getClass(), fieldName);

        AccessHelper.makeAccessible(field);

        field.set(instance, value);
    }

    public static void setStaticValue(Class<?> clazz, String fieldName, Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = deepFieldSearch(clazz, fieldName);

        AccessHelper.makeAccessible(field);

        field.set(null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Object instance, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = deepFieldSearch(instance.getClass(), fieldName);

        AccessHelper.makeAccessible(field);

        return (T) field.get(instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getStaticValue(Class<?> clazz, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = deepFieldSearch(clazz, fieldName);

        AccessHelper.makeAccessible(field);

        return (T) field.get(null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Object instance, String methodName, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] parameters = new Class<?>[args.length];

        for (int i = 0; i != args.length; i++) {
            parameters[i] = args[i].getClass();
        }

        Method method = deepMethodSearch(instance.getClass(), methodName, parameters);

        AccessHelper.makeAccessible(method);

        return (T) method.invoke(instance, args);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeStaticMethod(Class<?> clazz, String methodName, Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?>[] parameters = new Class<?>[args.length];

        for (int i = 0; i != args.length; i++) {
            parameters[i] = args[i].getClass();
        }

        Method method = deepMethodSearch(clazz, methodName, parameters);

        AccessHelper.makeAccessible(method);

        return (T) method.invoke(null, args);
    }

}
