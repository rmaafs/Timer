package spigot.file.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jetbrains.annotations.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DelegateDeserialization {

    @NotNull
    Class<? extends ConfigurationSerializable> value();
}
