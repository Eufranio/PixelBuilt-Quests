package dev.eufranio.pixelbuiltquests.registry;

import java.util.Collection;
import java.util.Optional;

public interface RegistryModule<T extends BaseType> {

    default void reset() {
        // should unregister any other registrations that have been done, like listeners
    }

    void registerAdditionalCatalog(T extra);

    Optional<T> getById(String id);

    Collection<T> getAll();

    void registerDefaults();

    Class<T> typeClass();

}
