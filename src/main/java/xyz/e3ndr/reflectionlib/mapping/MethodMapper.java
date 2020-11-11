package xyz.e3ndr.reflectionlib.mapping;

import java.lang.reflect.InvocationTargetException;

import org.jetbrains.annotations.Nullable;

import lombok.NonNull;

public interface MethodMapper {

    public @Nullable Object invoke(@NonNull MappedObject object, @Nullable Object[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
