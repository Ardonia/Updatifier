/**
 * This file is part of Updatifier, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 Flibio <http://github.com/Flibio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.flibio.updatifier;

import static me.flibio.updatifier.PluginInfo.DEPENDENCIES;
import static me.flibio.updatifier.PluginInfo.ID;
import static me.flibio.updatifier.PluginInfo.NAME;
import static me.flibio.updatifier.PluginInfo.VERSION;
import net.minecrell.mcstats.SpongeStatsLite;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

<<<<<<< HEAD
import java.nio.file.Path;
=======
import com.google.inject.Inject;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
>>>>>>> refs/remotes/Flibio/master
import java.util.HashMap;

@Plugin(id = ID, name = NAME, version = VERSION, dependencies = DEPENDENCIES)
@Updatifier(repoName = "Updatifier", repoOwner = "Flibio", version = "v" + VERSION)
public class UpdatifierPlugin {

<<<<<<< HEAD
    private static FileManager fileManager;

    @Inject @ConfigDir(sharedRoot = false) Path configDir;
    @Inject
    private Logger logger;
    @Inject
    private SpongeStatsLite statsLite;
    private HashMap<String, String> updates = new HashMap<>();
    private UpdatifierService api = new UpdatifierServiceImpl(this);

    @Listener
    public void onPreInitialize(GamePreInitializationEvent event) {
        this.statsLite.start();
        fileManager = new FileManager(logger, configDir, "config.conf");
    }

    @Listener
    public void started(GameStartedServerEvent event) {
        Sponge.getPluginManager().getPlugins().forEach(pluginC -> {
            if (pluginC.getInstance().isPresent()) {
                if (pluginC.getInstance().get().getClass().isAnnotationPresent(Updatifier.class)) {
                    if (!fileManager.getOrDefault("Blocked-Plugins." + pluginC.getId(), Boolean.TYPE, false)) {
                        Updatifier info = pluginC.getInstance().get().getClass().getAnnotation(Updatifier.class);
                        Task.builder().execute(task -> {
                            boolean available = api.updateAvailable(info.repoOwner(), info.repoName(), info.version());
                            if (available) {
                                // Add the plugin to the HashMap
                                updates.put(pluginC.getName(), info.repoOwner() + "/" + info.repoName());
                                // Log the messages on the main thread
                                Task.builder().execute(c -> {
                                    logger.info("An update is available for " + pluginC.getName() + "!");
                                    logger.info("Download it here: " + "https://github.com/" + info.repoOwner() + "/" + info.repoName()
                                            + "/releases");
                                }).submit(this);
                            }
                        }).async().submit(this);
                    }
                }
            }
        });
    }

    @Listener
=======
    @Inject private Logger logger;

    @Inject private SpongeStatsLite statsLite;

    private HashMap<String, String> updates = new HashMap<>();
    private UpdatifierService api = new UpdatifierService();
    private static FileManager fileManager;
    private boolean downloadUpdates = false;
    private boolean showChangelogs = false;

    @Listener
    public void onPreInitialize(GamePreInitializationEvent event) {
        // Register the Updatifier API
        Sponge.getGame().getServiceManager().setProvider(this, UpdatifierService.class, api);
        this.statsLite.start();
        fileManager = new FileManager(logger);
        fileManager.generateFolder("config/Updatifier");
        fileManager.generateFolder("updates");
        fileManager.generateFile("config/Updatifier/config.conf");
        fileManager.loadConfigFile();
        fileManager.testDefault("Download-Updates", false);
        fileManager.testDefault("Show-Changelogs", true);
        fileManager.testDefault("Blocked-Plugins.Updatifier", false);
        downloadUpdates = fileManager.getConfigValue("Download-Updates").contains("true");
        showChangelogs = fileManager.getConfigValue("Show-Changelogs").contains("true");
    }

    @Listener
    public void started(GameStartedServerEvent event) {
        Sponge.getPluginManager().getPlugins().forEach(pluginC -> {
            if (pluginC.getInstance().isPresent()) {
                if (pluginC.getInstance().get().getClass().isAnnotationPresent(Updatifier.class)) {
                    fileManager.testDefault("Blocked-Plugins." + pluginC.getId(), false);
                    if (!fileManager.getConfigValue("Blocked-Plugins." + pluginC.getId()).toLowerCase().contains("true")) {
                        Updatifier info = pluginC.getInstance().get().getClass().getAnnotation(Updatifier.class);
                        Task.builder().execute(task -> {
                            boolean available = api.updateAvailable(info.repoOwner(), info.repoName(), info.version());
                            if (available) {
                                // Add the plugin to the HashMap
                                updates.put(pluginC.getName(), info.repoOwner() + "/" + info.repoName());
                                // Log the messages on the main thread
                                Task.builder().execute(c -> {
                                    logger.info("An update is available for " + pluginC.getName() + "!");
                                    if (showChangelogs) {
                                        String body = api.getBody(info.repoOwner(), info.repoName()).replaceAll("\r", "").replaceAll("\n", "");
                                        if (body.indexOf("<!--") != -1 || body.indexOf("-->") != -1) {
                                            String result = body.substring(body.indexOf("<!--") + 4, body.indexOf("-->"));
                                            String[] changes = result.split(";");
                                            for (String change : changes) {
                                                if (!change.trim().isEmpty()) {
                                                    logger.info("- " + change.trim());
                                                }
                                            }
                                        }
                                    }
                                    if (downloadUpdates) {
                                        logger.info("It will be downloaded to 'updates/" + api.getFileName(info.repoOwner(), info.repoName()));
                                    } else {
                                        logger.info("Download it here: " + "https://github.com/" + info.repoOwner() + "/" + info.repoName()
                                                + "/releases");
                                    }
                                }).submit(this);
                                if (downloadUpdates) {
                                    // Download the latest release asset
                                    String url = api.getDownloadUrl(info.repoOwner(), info.repoName());
                                    if (!url.isEmpty()) {
                                        try {
                                            URL toDownload = new URL(url);
                                            ReadableByteChannel rbc = Channels.newChannel(toDownload.openStream());
                                            FileOutputStream fos = new FileOutputStream("updates/"
                                                    + api.getFileName(info.repoOwner(), info.repoName()));
                                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                                            fos.close();
                                        } catch (Exception e) {
                                            logger.error(e.toString());
                                        }
                                    }
                                }
                            }
                        }).async().submit(this);
                    }
                }
            }
        });
    }

    @Listener
>>>>>>> refs/remotes/Flibio/master
    public void onJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (player.hasPermission("updatifier.notify")) {
            for (String name : updates.keySet()) {
                player.sendMessage(Text.of(TextColors.YELLOW, "An update is available for ", TextColors.GREEN, name, "!"));
<<<<<<< HEAD
                player.sendMessage(Text.of(TextColors.GRAY, "https://github.com/" + updates.get(name) + "/releases"));
=======
                String repoOwner = updates.get(name).split("/")[0];
                String repoName = updates.get(name).split("/")[1];
                if (showChangelogs) {
                    String body = api.getBody(repoOwner, repoName).replaceAll("\r", "").replaceAll("\n", "");
                    if (body.indexOf("<!--") != -1 || body.indexOf("-->") != -1) {
                        String result = body.substring(body.indexOf("<!--") + 4, body.indexOf("-->"));
                        String[] changes = result.split(";");
                        for (String change : changes) {
                            if (!change.trim().isEmpty()) {
                                player.sendMessage(Text.of(TextColors.YELLOW, "- ", TextColors.GRAY, change.trim()));
                            }
                        }
                    }
                }
                if (downloadUpdates) {
                    player.sendMessage(Text.of(TextColors.YELLOW, "It will be downloaded to ", TextColors.GREEN,
                            "updates/" + api.getFileName(repoOwner, repoName)));
                } else {
                    player.sendMessage(Text.of(TextColors.YELLOW, "Download it here: ", TextColors.GREEN, "https://github.com/" + repoOwner + "/"
                            + repoName + "/releases"));
                }
>>>>>>> refs/remotes/Flibio/master
            }
        }
    }
}
