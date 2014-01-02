package io.snw.entityapi;

import io.snw.entityapi.api.EntityManager;
import io.snw.entityapi.entity.ControllableBatEntity;
import io.snw.entityapi.hooks.ChunkProviderServerHook;
import io.snw.entityapi.internal.Constants;
import io.snw.entityapi.metrics.Metrics;
import io.snw.entityapi.server.*;
import io.snw.entityapi.utils.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;


public abstract class EntityAPI extends JavaPlugin {

    public static final ModuleLogger LOGGER = new ModuleLogger("EntityAPI");
    public static final ModuleLogger LOGGER_REFLECTION = LOGGER.getModule("Reflection");

    private static EntityAPI INSTANCE;
    private static Plugin p;
    private static PluginManager pm;

    public static Server SERVER;
    public static HashMap<Plugin, Integer> counters = new HashMap<>();
    public static ArrayList<Plugin> plugins = new ArrayList<>();

 
    public static List<Plugin> sameplugin(Plugin newplugin){
        if(counters.containsKey(newplugin)){
            counters.put(newplugin, counters.get(newplugin)+1);
         } else {
            counters.put(newplugin, 1);
         }
        plugins.add(newplugin);
        return plugins;
   }
//To check if another instance is already running. Don't want 2 versions of the API running.
    public static Boolean hasInstance() { // why are we using a primitive wrapper here? /captain doesn't get it ._.
            if(plugins.size() > 1){
                return hasInstance() == true;
            } else {
            return hasInstance() == false;
            }
    }

    @Override
    public void onDisable() {
        /** DEBUG */
        for(World world : Bukkit.getWorlds()) {
            ChunkProviderServerHook.unhook(world);
        }
    }

    @Override
    public void onEnable() {

        INSTANCE = this;

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        initServer();
        registerEntities();

        /** DEBUG */
        for(World world : Bukkit.getWorlds()) {
            ChunkProviderServerHook.hook(world);
        }
        
        if(hasInstance() == true){
            int index = 0;
            pm.disablePlugin(plugins.get(index));
            while(plugins.iterator().hasNext() == true){
                pm.disablePlugin(plugins.get(index++));
            }
            p.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', "&4Warning! You have two EntityAPI Libraries in Plugins Folder! Please remove one!"));
            //pm.disablePlugin(this);
            //pm.disablePlugin(INSTANCE);
        }
    }

    /**
     * Checks the server brand etc. Also some servers brands don't have the version system (eg: MCPC+) so we need
     * to know that for our reflection.
     */
    protected void initServer() {
        List<Server> servers = new ArrayList<Server>();
        servers.add(new MCPCPlusServer());
        servers.add(new SpigotServer());
        servers.add(new CraftBukkitServer());
        servers.add(new UnknownServer());

        for (Server server : servers) {
            if (server.init()) {   //the first server type that returns true on init is a valid server brand.
                this.SERVER = server;
                break;
            }
        }

        if (SERVER == null) {
            LOGGER.warning("Failed to identify the server brand! The API will not run correctly -> disabling");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            if (!SERVER.isCompatible()) {
                LOGGER.warning("This Server version may not be compatible with EntityAPI!");
            }
            LOGGER.info("Identified server brand: " + SERVER.getName());
            LOGGER.info("MC Version: " + SERVER.getMCVersion());
        }
    }

    protected void registerEntities() {
        EntityUtil.registerEntity(ControllableBatEntity.class, Constants.EntityTypes.Names.ENTITY_BAT, Constants.EntityTypes.Ids.ENTITY_BAT);
    }

    /**
     * Will Check for instance of this API running.
     * If one is found its returned otherwise if not, throws error.
     *
     * @return
     */

    public static EntityAPI getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("EntityAPI not Enabled, instance could not be found!");
        }
        return INSTANCE;
    }

    public <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Will return a unique entity manager for each plugin using this.
     * (to have some cross plugin working version stuff)
     * I'm not really experienced with this and the EntityManager is just an idea
     * you guys can do whatever you want with it since I don't really know what your original
     * idea was :/
     * (captainbern)
     *
     * @param plugin
     * @return
     */
    public EntityManager getEntityManager(Plugin plugin) {
        return null;
    }
}
