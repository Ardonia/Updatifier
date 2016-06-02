/*
 * This file is part of Updatifier, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 FlibioStudio <http://github.com/FlibioStudio>
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

package io.github.flibiostudio.updatifier.command;

import com.google.inject.Inject;
import io.github.flibiostudio.updatifier.UpdatifierPlugin;
import me.flibio.updatifier.UpdatifierService;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.net.MalformedURLException;
import java.net.URL;

public class GetUpdatesExecutor implements CommandExecutor {

    private final UpdatifierPlugin plugin;

    @Inject
    private GetUpdatesExecutor(UpdatifierPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            handlePlayer((Player) src);
            return CommandResult.success();
        }
        if (src instanceof ConsoleSource) {
            handleConsole();
            return CommandResult.success();
        }

        return CommandResult.empty();
    }

    private void handlePlayer(Player player) {
        UpdatifierService api = UpdatifierService.getInstance();
        plugin.getUpdates().entrySet().forEach(entry -> {
            String name = entry.getKey();
            player.sendMessage(Text.of(TextColors.YELLOW, "An update is available for ", TextColors.GREEN, name, "!"));

            String repoOwner = entry.getValue().split("/")[0];
            String repoName = entry.getValue().split("/")[1];
            if (plugin.showChangelogs()) {
                String body = api.getBody(repoOwner, repoName).replaceAll("\r", "").replaceAll("\n", "");
                if (body.contains("<!--") && body.contains("-->")) {
                    String result = body.substring(body.indexOf("<!--") + 4, body.indexOf("-->"));
                    String[] changes = result.split(";");
                    for (String change : changes) {
                        if (!change.trim().isEmpty()) {
                            player.sendMessage(Text.of(TextColors.YELLOW, "- ", TextColors.GRAY, change.trim()));
                        }
                    }
                }
            }
            if (plugin.downloadUpdates()) {
                player.sendMessage(Text.of(TextColors.YELLOW, "It will be downloaded to ", TextColors.GREEN,
                        "updates/" + api.getFileName(repoOwner, repoName)));
            } else {
                Text text;
                try {
                    text = Text.of(TextColors.GREEN,
                            "https://github.com/" + repoOwner + "/" + repoName + "/releases",
                            TextActions.openUrl(new URL("https://github.com/" + repoOwner + "/" + repoName + "/releases")));
                } catch (MalformedURLException e) {
                    text = Text.of(TextColors.GREEN,
                            "https://github.com/" + repoOwner + "/" + repoName + "/releases");
                }
                player.sendMessage(Text.of(TextColors.YELLOW, "Download it here: ", text));
            }
        });
        if (plugin.getUpdates().isEmpty()) {
            player.sendMessage(Text.of("All plugins are latest, you are good now!"));
        }
    }

    private void handleConsole() {
        Logger logger = plugin.getLogger();
        plugin.getUpdates().entrySet().forEach(entry -> {
            Task.builder().execute(task -> {
                String name = entry.getKey();
                logger.info("An update is available for " + name + "!");

                String repoOwner = entry.getValue().split("/")[0];
                String repoName = entry.getValue().split("/")[1];
                if (plugin.showChangelogs()) {
                    String body = UpdatifierService.getInstance().getBody(repoOwner, repoName).replaceAll("\r", "").replaceAll("\n", "");
                    if (body.contains("<!--") && body.contains("-->")) {
                        String result = body.substring(body.indexOf("<!--") + 4, body.indexOf("-->"));
                        String[] changes = result.split(";");
                        for (String change : changes) {
                            if (!change.trim().isEmpty()) {
                                logger.info("- " + change.trim());
                            }
                        }
                    }
                }
                if (plugin.downloadUpdates()) {
                    logger.info("It will be downloaded to 'updates/"
                            + UpdatifierService.getInstance().getFileName(repoOwner, repoName));
                } else {
                    logger.info("Download it here: " + "https://github.com/" + repoOwner + "/" + repoName
                            + "/releases");
                }
            }).submit(plugin);
        });
        if (plugin.getUpdates().isEmpty()) {
            Task.builder().execute(task -> {
                logger.info("All plugins are latest, you are good now!");
            }).submit(plugin);
        }
    }

}
