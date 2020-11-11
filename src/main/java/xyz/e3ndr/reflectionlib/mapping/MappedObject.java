package xyz.e3ndr.reflectionlib.mapping;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import xyz.e3ndr.reflectionlib.ReflectionLib;

@RequiredArgsConstructor
public class MappedObject {
    protected Map<String, MethodMapper> methodMappings = new ConcurrentHashMap<>();
    protected Map<String, FieldMapper> fieldMappings = new ConcurrentHashMap<>();
    protected @Getter @NonNull Object instance;

    public void addMethodMapping(@NonNull String name, @NonNull MethodMapper mapper) {
        this.methodMappings.put(name, mapper);
    }

    public void addFieldMapping(@NonNull String name, @NonNull FieldMapper mapper) {
        this.fieldMappings.put(name, mapper);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getField(@NonNull String name) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        FieldMapper mapper = this.fieldMappings.get(name);

        if (mapper != null) {
            return (T) mapper.get(this);
        } else {
            return ReflectionLib.getValue(this.instance, name);
        }
    }

    public void setField(@NonNull String name, @Nullable Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        FieldMapper mapper = this.fieldMappings.get(name);

        if (mapper != null) {
            mapper.set(this, value);
        } else {
            ReflectionLib.setValue(this.instance, name, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T invokeMethod(@NonNull String name, @Nullable Object... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        MethodMapper mapper = this.methodMappings.get(name);

        if (mapper != null) {
            return (T) mapper.invoke(this, args);
        } else {
            return ReflectionLib.invokeMethod(this.instance, name, args);
        }
    }

}
