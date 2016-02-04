package me.flibio.updatifier;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import java.lang.annotation.Annotation;
import java.util.HashMap;

@Plugin(id = "Updatifier", name = "Updatifier", version = "1.0.0")
@Updatifier(repoName = "Updatifier", repoOwner = "Flibio", version = "v1.0.0")
public class UpdatifierPlugin {
	
	private HashMap<String, String> updates = new HashMap<String, String>();
	
	@Inject
	Logger logger;
	
	private UpdatifierAPI api = new UpdatifierAPI();
	
	private Scheduler scheduler;
	
	@Listener
	public void onInitialize(GameInitializationEvent event) {
		//Register the Updatifier API
		Sponge.getGame().getServiceManager().setProvider(this, UpdatifierAPI.class, api);
		//Set the scheduler
		this.scheduler = Sponge.getScheduler();
	}
	
	@Listener
	public void started(GameStartedServerEvent event) {
		for(PluginContainer pluginC : Sponge.getPluginManager().getPlugins()) {
			if(pluginC.getInstance().isPresent()) {
				if(pluginC.getInstance().get().getClass().isAnnotationPresent(Updatifier.class)) {
					Annotation annotation = pluginC.getInstance().get().getClass().getAnnotation(Updatifier.class);
					Updatifier info = (Updatifier) annotation;
					scheduler.createTaskBuilder().execute(r -> {
						boolean available = api.updateAvailable(info.repoOwner(), info.repoName(), info.version());
						if(available) {
							//Add the plugin to the HashMap
							updates.put(pluginC.getName(), info.repoOwner()+"/"+info.repoName());
							//Log the messages on the main thread (Makes the messages shorter in length)
							scheduler.createTaskBuilder().execute(c -> {
								logger.info("An update is available for "+pluginC.getName()+"!");
								logger.info("Download it here: "+"https://github.com/"+info.repoOwner()+"/"+info.repoName()+"/releases");
							}).submit(this);
						}
					}).async().submit(this);
				}
			}
		}
	}
	
	@Listener
	public void onJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();
		if(player.hasPermission("updatifier.notify")) {
			for(String name : updates.keySet()) {
				player.sendMessage(Text.of(TextColors.YELLOW,"An update is available for ",TextColors.GREEN,name,"!"));
				player.sendMessage(Text.of(TextColors.GRAY,"https://github.com/"+updates.get(name)+"/releases"));
			}
		}
	}
}
