package it.cosenonjaviste.daggermock;

import java.lang.annotation.*;

import org.junit.jupiter.api.extension.ExtendWith;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DaggerMockExtension.class)
public @interface DaggerMockTest {

    Class<?> value();
}
