package me.flibio.updatifier;

import org.spongepowered.api.plugin.PluginContainer;

import java.net.URL;

/**
 * Update information of Sponge plugins.
 *
 * @author liach
 */
public interface UpdateInfo {

    /**
     * Get the Sponge plugin for this update info.
     *
     * @return The plugin container of the Sponge plugin
     */
    PluginContainer getPlugin();

    boolean hasUpdate();

    String getCurrentVersion();

    String getLatestVersion();

    long getLastCheckTime();

    URL getDownloadUrl();

}
