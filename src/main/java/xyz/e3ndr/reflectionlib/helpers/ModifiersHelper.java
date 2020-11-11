package xyz.e3ndr.reflectionlib.helpers;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import xyz.e3ndr.reflectionlib.ReflectionLib;

public class ModifiersHelper {
    private static final String[] MOD_FIELDS = new String[] {
            "PUBLIC",
            "PRIVATE",
            "PROTECTED",
            "STATIC",
            "FINAL",
            "SYNCHRONIZED",
            "VOLATILE",
            "TRANSIENT",
            "NATIVE",
            "INTERFACE",
            "ABSTRACT",
            "STRICT"
    };

    private static List<Mod> mods = new ArrayList<>();

    static {
        for (String mod : MOD_FIELDS) {
            try {
                mods.add(new Mod(mod, ReflectionLib.getStaticValue(Modifier.class, mod)));
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getModifiers(int modifiers) {
        List<String> ret = new ArrayList<>();

        for (Mod mod : mods) {
            if (mod.has(modifiers)) {
                if (!mod.name.equals("STRICT")) {
                    ret.add(mod.name);
                } else {
                    ret.add("STRICTFP");
                }
            }
        }

        return ret;
    }

    @AllArgsConstructor
    private static class Mod {
        private String name;
        private int mask;

        public boolean has(int modifiers) {
            return (modifiers & this.mask) != 0;
        }
    }

}
