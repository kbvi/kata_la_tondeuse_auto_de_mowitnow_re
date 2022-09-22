package mowitnow.infra;

import org.junit.jupiter.api.function.ThrowingSupplier;

import java.util.Optional;

final public class OptionalFromThrowableHelper {
    static public <T> Optional<T> ofThrowableAndNullable(final ThrowingSupplier<T> unsure) {
        try {
            return Optional.ofNullable(unsure.get());
        } catch (Throwable ignored) { }
        return Optional.empty();
    }
}
