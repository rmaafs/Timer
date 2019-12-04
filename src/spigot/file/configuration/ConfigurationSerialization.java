package spigot.file.configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigurationSerialization {
    public static final String SERIALIZED_TYPE_KEY = "==";
    private final Class<? extends ConfigurationSerializable> clazz;
    private static Map<String, Class<? extends ConfigurationSerializable>> aliases = new HashMap();

    protected ConfigurationSerialization(@NotNull Class<? extends ConfigurationSerializable> clazz) {
        this.clazz = clazz;
    }

    @Nullable
    protected Method getMethod(@NotNull String name, boolean isStatic) {
        try {
            Method method = this.clazz.getDeclaredMethod(name, Map.class);
            if (!ConfigurationSerializable.class.isAssignableFrom(method.getReturnType())) {
                return null;
            } else {
                return Modifier.isStatic(method.getModifiers()) != isStatic ? null : method;
            }
        } catch (NoSuchMethodException var4) {
            return null;
        } catch (SecurityException var5) {
            return null;
        }
    }

    @Nullable
    protected Constructor<? extends ConfigurationSerializable> getConstructor() {
        try {
            return this.clazz.getConstructor(Map.class);
        } catch (NoSuchMethodException var2) {
            return null;
        } catch (SecurityException var3) {
            return null;
        }
    }

    @Nullable
    protected ConfigurationSerializable deserializeViaMethod(@NotNull Method method, @NotNull Map<String, ?> args) {
        try {
            ConfigurationSerializable result = (ConfigurationSerializable)method.invoke((Object)null, args);
            if (result != null) {
                return result;
            }

            Logger.getLogger(ConfigurationSerialization.class.getName()).log(Level.SEVERE, "Could not call method '" + method.toString() + "' of " + this.clazz + " for deserialization: method returned null");
        } catch (Throwable var4) {
            Logger.getLogger(ConfigurationSerialization.class.getName()).log(Level.SEVERE, "Could not call method '" + method.toString() + "' of " + this.clazz + " for deserialization", var4 instanceof InvocationTargetException ? var4.getCause() : var4);
        }

        return null;
    }

    @Nullable
    protected ConfigurationSerializable deserializeViaCtor(@NotNull Constructor<? extends ConfigurationSerializable> ctor, @NotNull Map<String, ?> args) {
        try {
            return (ConfigurationSerializable)ctor.newInstance(args);
        } catch (Throwable var4) {
            Logger.getLogger(ConfigurationSerialization.class.getName()).log(Level.SEVERE, "Could not call constructor '" + ctor.toString() + "' of " + this.clazz + " for deserialization", var4 instanceof InvocationTargetException ? var4.getCause() : var4);
            return null;
        }
    }

    @Nullable
    public ConfigurationSerializable deserialize(@NotNull Map<String, ?> args) {
//        Validate.notNull(args, "Args must not be null");
        ConfigurationSerializable result = null;
        Method method = null;
        if (result == null) {
            method = this.getMethod("deserialize", true);
            if (method != null) {
                result = this.deserializeViaMethod(method, args);
            }
        }

        if (result == null) {
            method = this.getMethod("valueOf", true);
            if (method != null) {
                result = this.deserializeViaMethod(method, args);
            }
        }

        if (result == null) {
            Constructor<? extends ConfigurationSerializable> constructor = this.getConstructor();
            if (constructor != null) {
                result = this.deserializeViaCtor(constructor, args);
            }
        }

        return result;
    }

    @Nullable
    public static ConfigurationSerializable deserializeObject(@NotNull Map<String, ?> args, @NotNull Class<? extends ConfigurationSerializable> clazz) {
        return (new ConfigurationSerialization(clazz)).deserialize(args);
    }

    @Nullable
    public static ConfigurationSerializable deserializeObject(@NotNull Map<String, ?> args) {
        Class<? extends ConfigurationSerializable> clazz = null;
        if (args.containsKey("==")) {
            try {
                String alias = (String)args.get("==");
                if (alias == null) {
                    throw new IllegalArgumentException("Cannot have null alias");
                }

                clazz = getClassByAlias(alias);
                if (clazz == null) {
                    throw new IllegalArgumentException("Specified class does not exist ('" + alias + "')");
                }
            } catch (ClassCastException var3) {
                var3.fillInStackTrace();
                throw var3;
            }

            return (new ConfigurationSerialization(clazz)).deserialize(args);
        } else {
            throw new IllegalArgumentException("Args doesn't contain type key ('==')");
        }
    }

    public static void registerClass(@NotNull Class<? extends ConfigurationSerializable> clazz) {
        DelegateDeserialization delegate = (DelegateDeserialization)clazz.getAnnotation(DelegateDeserialization.class);
        if (delegate == null) {
            registerClass(clazz, getAlias(clazz));
            registerClass(clazz, clazz.getName());
        }

    }

    public static void registerClass(@NotNull Class<? extends ConfigurationSerializable> clazz, @NotNull String alias) {
        aliases.put(alias, clazz);
    }

    public static void unregisterClass(@NotNull String alias) {
        aliases.remove(alias);
    }

    public static void unregisterClass(@NotNull Class<? extends ConfigurationSerializable> clazz) {
        while(aliases.values().remove(clazz)) {
        }

    }

    @Nullable
    public static Class<? extends ConfigurationSerializable> getClassByAlias(@NotNull String alias) {
        return (Class)aliases.get(alias);
    }

    @NotNull
    public static String getAlias(@NotNull Class<? extends ConfigurationSerializable> clazz) {
        DelegateDeserialization delegate = (DelegateDeserialization)clazz.getAnnotation(DelegateDeserialization.class);
        if (delegate != null) {
            if (delegate.value() != null && delegate.value() != clazz) {
                return getAlias(delegate.value());
            }

            delegate = null;
        }

        if (delegate == null) {
            SerializableAs alias = (SerializableAs)clazz.getAnnotation(SerializableAs.class);
            if (alias != null && alias.value() != null) {
                return alias.value();
            }
        }

        return clazz.getName();
    }
}