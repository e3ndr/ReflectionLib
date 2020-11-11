package xyz.e3ndr.reflectionlib.mapping;

import org.jetbrains.annotations.Nullable;

import lombok.NonNull;

public interface FieldMapper {

    public @Nullable Object get(@NonNull MappedObject object) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;

    public void set(@NonNull MappedObject object, @Nullable Object value) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException;

}
