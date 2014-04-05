package org.entityapi.api.plugin;

import org.bukkit.plugin.Plugin;
import org.entityapi.api.EntityManager;
import org.entityapi.api.IBasicEntityUtil;
import org.entityapi.api.ISpawnUtil;

public class EntityAPI {

    private static IEntityAPICore CORE;

    /**
     * Several Loggers
     */
    public static final ModuleLogger LOGGER = new ModuleLogger("EntityAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");
    public static final ModuleLogger LOGGER_DATA_STORE = LOGGER_REFLECTION.getModule("Persistence");

    public static void setCore(IEntityAPICore core) {
        if (CORE != null) {
            return;
        }
        CORE = core;
    }

    public static IEntityAPICore getCore() {
        return CORE;
    }

    /**
     * Creates an EntityManager for the given Plugin.
     *
     * @param owningPlugin The plugin the EntityManager should be assigned to.
     * @return The EntityManager object for the given plugin.
     */
    public static EntityManager createManager(Plugin owningPlugin) {
        return CORE.createManager(owningPlugin);
    }

    /**
     * Creates an EntityManager for the given Plugin.
     *
     * @param owningPlugin The plugin the EntityManager should be assigned to.
     * @param keepInMemory Whether or not the EntityManager should keep every entity in memory. Even when the entity died.
     * @return The EntityManager for the given plugin.
     */
    public static EntityManager createEntityManager(Plugin owningPlugin, boolean keepInMemory) {
        return CORE.createEntityManager(owningPlugin, keepInMemory);
    }

    /**
     * Registers an EntityManager with the given name.
     *
     * @param name    The name the EntityManager will be registered with.
     * @param manager The EntityManager.
     */
    public static void registerManager(String name, EntityManager manager) {
        CORE.registerManager(name, manager);
    }

    /**
     * Returns true if the given plugin has an EntityManager.
     *
     * @param plugin The plugin that should be checked.
     * @return Whether or not the plugin has an EntityManager.
     */
    public static boolean hasEntityManager(Plugin plugin) {
        return CORE.hasEntityManager(plugin);
    }

    public static boolean hasEntityManager(String pluginName) {
        return CORE.hasEntityManager(pluginName);
    }

    public static EntityManager getManagerFor(Plugin plugin) {
        return CORE.getManagerFor(plugin);
    }

    public static EntityManager getManagerFor(String pluginName) {
        return CORE.getManagerFor(pluginName);
    }

    public static ISpawnUtil getSpawnUtil() {
        return CORE.getSpawnUtil();
    }

    public static IBasicEntityUtil getBasicEntityUtil() {
        return CORE.getBasicEntityUtil();
    }
}