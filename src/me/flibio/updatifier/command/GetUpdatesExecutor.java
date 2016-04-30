/**
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

package me.flibio.updatifier.command;

import me.flibio.updatifier.UpdatifierPlugin;
import me.flibio.updatifier.UpdatifierService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.net.MalformedURLException;
import java.net.URL;

public class GetUpdatesExecutor implements CommandExecutor {

    private static final GetUpdatesExecutor INSTANCE = new GetUpdatesExecutor();
    private static final UpdatifierPlugin pluginInstance = UpdatifierPlugin.getInstance();

    private GetUpdatesExecutor() {
    }

    public static GetUpdatesExecutor getInstance() {
        return GetUpdatesExecutor.INSTANCE;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        UpdatifierService api = UpdatifierService.getInstance();
        pluginInstance.getUpdates().entrySet().forEach(entry -> {
            String name = entry.getKey();
            src.sendMessage(Text.of(TextColors.YELLOW, "An update is available for ", TextColors.GREEN, name, "!"));

            String repoOwner = entry.getValue().split("/")[0];
            String repoName = entry.getValue().split("/")[1];
            if (pluginInstance.showChangelogs()) {
                String body = api.getBody(repoOwner, repoName).replaceAll("\r", "").replaceAll("\n", "");
                if (body.contains("<!--") && body.contains("-->")) {
                    String result = body.substring(body.indexOf("<!--") + 4, body.indexOf("-->"));
                    String[] changes = result.split(";");
                    for (String change : changes) {
                        if (!change.trim().isEmpty()) {
                            src.sendMessage(Text.of(TextColors.YELLOW, "- ", TextColors.GRAY, change.trim()));
                        }
                    }
                }
            }
            if (pluginInstance.downloadUpdates()) {
                src.sendMessage(Text.of(TextColors.YELLOW, "It will be downloaded to ", TextColors.GREEN,
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
                src.sendMessage(Text.of(TextColors.YELLOW, "Download it here: ", text));
            }
        });
        if (pluginInstance.getUpdates().isEmpty()) {
            src.sendMessage(Text.of("All plugins are latest, you are good now!"));
        }
        return CommandResult.success();
    }

}
