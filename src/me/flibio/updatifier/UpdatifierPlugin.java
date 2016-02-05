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

import com.google.inject.Inject;
import net.minecrell.mcstats.SpongeStatsLite;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;

@Plugin(id = ID, name = NAME, version = VERSION, dependencies = DEPENDENCIES)
@Updatifier(repoName = "Updatifier", repoOwner = "Flibio", version = "v" + VERSION)
public class UpdatifierPlugin {

	@Inject
	private Logger logger;

	@Inject
	private SpongeStatsLite statsLite;

	private HashMap<String, String> updates = new HashMap<>();
	private UpdatifierService api = new UpdatifierService();
	private static FileManager fileManager;

	@Listener
	public void onPreInitialize(GamePreInitializationEvent event) {
		// Register the Updatifier API
		Sponge.getGame().getServiceManager().setProvider(this, UpdatifierService.class, api);
		this.statsLite.start();
		fileManager = new FileManager(logger);
		fileManager.generateFolder("config/Updatifier");
		fileManager.generateFile("config/Updatifier/config.conf");
		fileManager.loadConfigFile();
		fileManager.testDefault("Blocked-Plugins.Updatifier", false);
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
	public void onJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		if (player.hasPermission("updatifier.notify")) {
			for (String name : updates.keySet()) {
				player.sendMessage(Text.of(TextColors.YELLOW, "An update is available for ", TextColors.GREEN, name, "!"));
				player.sendMessage(Text.of(TextColors.GRAY, "https://github.com/" + updates.get(name) + "/releases"));
			}
		}
	}
}