package xyz.e3ndr.reflectionlib.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DebugFormat {
    public static String INDENT = "    ";

    private static final String ANNOTATION = "@%s";

    private static final String FIELD = "%s%s%s %s;";

    private static final String METHOD = "%s%s%s %s(%s)%s";
    private static final String METHOD_ENDING = " {\n%s%s// Code\n%s}";
    private static final String ABSTRACT_METHOD_ENDING = ";";

    private static final String ENUM_CONSTANT = ",\n%s%s";

    private static final String CONSTRUCTOR = "%s%s%s(%s)" + METHOD_ENDING;

    private static final String PARAMETERIZED_TYPE = "%s<%s>";

    private static final String PARAMETER = "%s %s";
    private static final String PARAMETER_VARARGS = "%s... %s";

    public static String stringParameters(Parameter[] parameters, boolean useSimpleName) {
        if (parameters.length == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            for (Parameter parameter : parameters) {
                String format = parameter.isVarArgs() ? PARAMETER_VARARGS : PARAMETER;
                String declaration = String.format(format, getNameForType(parameter.getParameterizedType(), useSimpleName), parameter.getName());

                sb.append(", ").append(stringAnnotations(parameter.getAnnotations(), " ", useSimpleName)).append(declaration);
            }

            return sb.substring(2);
        }
    }

    public static String getNameForType(Type type, boolean useSimpleName) {
        if (useSimpleName) {
            if (type instanceof Class<?>) {
                return ((Class<?>) type).getSimpleName();
            } else if (type instanceof ParameterizedType) {
                return stringParameterizedType((ParameterizedType) type, true);
            } else {
                return type.toString();
            }
        } else {
            return type.getTypeName();
        }
    }

    public static String stringParameterizedType(ParameterizedType type, boolean useSimpleName) {
        String clazz = getNameForType(type.getRawType(), useSimpleName);
        StringBuilder types = new StringBuilder();

        for (Type arg : type.getActualTypeArguments()) {
            types.append(' ').append(getNameForType(arg, useSimpleName));
        }

        return String.format(PARAMETERIZED_TYPE, clazz, types.substring(1));
    }

    public static String stringAnnotations(Annotation[] annotations, String separator, boolean useSimpleName) {
        StringBuilder sb = new StringBuilder();

        for (Annotation annotation : annotations) {
            sb.append(String.format(ANNOTATION, getNameForType(annotation.annotationType(), useSimpleName))).append(separator);
        }

        return sb.toString();
    }

    public static String stringModifiers(int modifiers) {
        StringBuilder sb = new StringBuilder();
        List<String> mods = ModifiersHelper.getModifiers(modifiers);

        if (mods.isEmpty()) {
            return "";
        } else {
            for (String mod : mods) {
                sb.append(mod.toLowerCase()).append(" ");
            }

            return sb.toString();
        }
    }

    public static String stringMethod(Method method, boolean useSimpleName) {
        return stringMethod0(method, useSimpleName, false);
    }

    private static String stringMethod0(Method method, boolean useSimpleName, boolean addIndent) {
        String ending = Modifier.isAbstract(method.getModifiers()) ? ABSTRACT_METHOD_ENDING : String.format(METHOD_ENDING, INDENT, INDENT, INDENT);
        String annotations = stringAnnotations(method.getAnnotations(), "\n", useSimpleName);
        String parameters = stringParameters(method.getParameters(), useSimpleName);
        String modifiers = stringModifiers(method.getModifiers());

        if (method.getDeclaringClass().isInterface()) {
            modifiers = modifiers.replace("abstract", "");
        }

        if (!annotations.isEmpty() && addIndent) {
            annotations += INDENT; // Add indent, as the annotations are on another line.
        }

        return String.format(METHOD, annotations, modifiers, getNameForType(method.getReturnType(), useSimpleName), method.getName(), parameters, ending);

    }

    public static String stringField(Field field, boolean useSimpleName) {
        return stringField0(field, useSimpleName, false);
    }

    private static String stringField0(Field field, boolean useSimpleName, boolean addIndent) {
        String annotations = stringAnnotations(field.getAnnotations(), "\n", useSimpleName);
        String modifiers = stringModifiers(field.getModifiers());

        if (!annotations.isEmpty() && addIndent) {
            annotations += INDENT; // Add indent, as the annotations are on another line.
        }

        return String.format(FIELD, annotations, modifiers, getNameForType(field.getType(), useSimpleName), field.getName());
    }

    public static String stringConstructor(Constructor<?> constructor, boolean useSimpleName) {
        return stringConstructor0(constructor, useSimpleName, false);
    }

    private static String stringConstructor0(Constructor<?> constructor, boolean useSimpleName, boolean addIndent) {
        String annotations = stringAnnotations(constructor.getAnnotations(), "\n", useSimpleName);
        String parameters = stringParameters(constructor.getParameters(), useSimpleName);
        String modifiers = stringModifiers(constructor.getModifiers());

        if (!annotations.isEmpty() && addIndent) {
            annotations += INDENT; // Add indent, as the annotations are on another line.
        }

        return String.format(CONSTRUCTOR, annotations, modifiers, getNameForType(constructor.getDeclaringClass(), useSimpleName), parameters, INDENT, INDENT, INDENT);
    }

    public static String stringSupers(Class<?> clazz, boolean useSimpleName) {
        StringBuilder sb = new StringBuilder();

        if (clazz.getGenericSuperclass() != null) {
            Type superClass = clazz.getGenericSuperclass();

            if (!(superClass == Annotation.class) && !(superClass == Object.class) && !(superClass == Enum.class)) { // Prevent "extends Object"
                sb.append(" extends ").append(getNameForType(clazz.getGenericSuperclass(), useSimpleName));
            }
        }

        // Annotations cannot be extended
        if (!clazz.isAnnotation()) {
            if (clazz.getGenericInterfaces().length != 0) {
                sb.append(" implements ");

                for (Type implemented : clazz.getGenericInterfaces()) {
                    sb.append(getNameForType(implemented, useSimpleName)).append(", ");
                }

                return sb.substring(0, sb.length() - 2); // Remove the last comma
            }
        }

        return sb.toString();
    }

    public static String stringClass(Class<?> clazz, boolean useSimpleName) {
        return stringClass0(clazz, useSimpleName, 0);
    }

    private static String stringClass0(Class<?> clazz, boolean useSimpleName, int indentLevel) {
        String annotations = stringAnnotations(clazz.getAnnotations(), "\n", useSimpleName);
        String modifiers = stringModifiers(clazz.getModifiers());
        String supers = stringSupers(clazz, useSimpleName);
        List<String> excludeFields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        sb.append(annotations);

        if (clazz.isEnum()) {
            sb.append(modifiers).append("enum ");
            excludeFields.add("ENUM$VALUES");
        } else if (clazz.isAnnotation()) {
            sb.append(modifiers = modifiers.replace("interface abstract", "@interface"));
        } else if (clazz.isInterface()) {
            sb.append(modifiers.replace("abstract ", ""));
        } else {
            sb.append(modifiers).append("class ");
        }

        sb.append(getNameForType(clazz, useSimpleName)).append(supers).append(" {\n");

        if (clazz.isEnum() && (clazz.getEnumConstants().length != 0)) {
            StringBuilder enums = new StringBuilder();

            for (Object constant : clazz.getEnumConstants()) {
                enums.append(String.format(ENUM_CONSTANT, INDENT, constant));
                excludeFields.add(constant.toString());
            }

            sb.append(enums.substring(2)).append(";\n");
        }

        for (Field f : clazz.getDeclaredFields()) {
            if (!excludeFields.contains(f.getName())) {
                String field = stringField0(f, useSimpleName, true);

                sb.append(INDENT).append(field).append('\n');
            }
        }

        if (clazz.getDeclaredConstructors().length != 0) { // Prevent 2 newlines from appearing
            for (Constructor<?> c : clazz.getDeclaredConstructors()) {
                String constructor = stringConstructor0(c, useSimpleName, true);

                sb.append('\n').append(INDENT).append(constructor);
            }

            sb.append('\n');
        }

        if (clazz.getDeclaredMethods().length != 0) {
            sb.append('\n');

            for (Method m : clazz.getDeclaredMethods()) {
                String method = stringMethod0(m, useSimpleName, true);

                sb.append(INDENT).append(method).append("\n\n");
            }
        }
        for (Class<?> sub : clazz.getDeclaredClasses()) {
            String subClass = stringClass0(sub, useSimpleName, indentLevel + 1);

            sb.append(INDENT).append(subClass).append("\n\n");
        }

        sb.append("}");

        String effectiveIndent = new String(new char[indentLevel]).replace("\0", INDENT);
        String[] serialized = sb.toString().split("\n");

        return String.join("\n" + effectiveIndent, serialized); // Apply the indentLevel
    }

}
