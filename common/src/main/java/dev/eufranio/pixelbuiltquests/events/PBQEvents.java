package dev.eufranio.pixelbuiltquests.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.eufranio.pixelbuiltquests.registry.BaseType;
import dev.eufranio.pixelbuiltquests.registry.PBQRegistry;
import dev.eufranio.pixelbuiltquests.registry.RegistryModule;

public interface PBQEvents {

    Event<RegisterModules> REGISTER_MODULES = EventFactory.createLoop();

    Event<RegisterDefaultTypes<?>> REGISTER_DEFAULTS = EventFactory.createLoop();

    Event<RegisterTypes<?>> REGISTER_TYPES = EventFactory.createLoop();

    Event<PostRegisterTypes<?>> POST_REGISTER = EventFactory.createLoop();

    Event<Reload> RELOAD = EventFactory.createLoop();

    @FunctionalInterface
    interface RegisterModules {

        /**
         * Fired when PixelBuiltQuests is initializing its {@link RegistryModule}s. This is the
         * point when you should register your custom {@link RegistryModule} implementations.
         */
        void registerModules(PBQRegistry registry);

    }

    @FunctionalInterface
    interface RegisterDefaultTypes<T extends BaseType> {

        /**
         * Fired when PixelBuiltQuests is gathering all default {@link BaseType} implementations to
         * register within the {@link RegistryModule}s. This is fired before normal registrations.
         */
        void registerDefaults(Class<T> type, PBQRegistry registry, RegistryModule<T> module);

    }

    @FunctionalInterface
    interface RegisterTypes<T extends BaseType> {

        /**
         * Fired when PixelBuiltQuests is gathering all {@link BaseType} implementations to
         * register within the {@link RegistryModule}s. This is fired **before** the default
         * types registration.
         */
        void registerTypes(Class<T> type, PBQRegistry registry, RegistryModule<T> module);

    }

    @FunctionalInterface
    interface PostRegisterTypes<T extends BaseType> {

        /**
         * Fired when PixelBuiltQuests is gathering all {@link BaseType} implementations to
         * register within the {@link RegistryModule}s. This is fired **after** the normal
         * types have been registered.
         * <p>
         * This is useful for cases where you want to register a default type, or in case no other
         * types have been registered (so there is always at least one type registered).
         */
        void postRegister(Class<T> type, PBQRegistry registry, RegistryModule<T> module);

    }

    interface Reload {

        /**
         * Fired when the /pbq reload command is called.
         */
        void reload();

    }

}
