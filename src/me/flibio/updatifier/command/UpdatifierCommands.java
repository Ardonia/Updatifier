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
package me.flibio.updatifier.command;

import me.flibio.updatifier.PluginInfo;
import me.flibio.updatifier.UpdatifierPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility command class for Updatifier plugin.
 */
public final class UpdatifierCommands {

    private static final UpdatifierCommands INSTANCE = new UpdatifierCommands();
    private CommandSpec commandGetUpdates;
    private UpdatifierPlugin pluginInstance = UpdatifierPlugin.getInstance();

    private UpdatifierCommands() {
    }

    public static UpdatifierCommands getInstance() {
        return UpdatifierCommands.INSTANCE;
    }

    public CommandSpec getCommandGetUpdates() {
        return this.commandGetUpdates;
    }

    public void init() {
        commandGetUpdates = CommandSpec.builder()
                .executor(GetUpdatesExecutor.getInstance())
                .permission(PluginInfo.PERM_NOTIFY)
                .description(Text.of("Get available plugin updates."))
                .arguments()
                .build();
    }

    public void registerAll() {
        Sponge.getCommandManager().register(pluginInstance, commandGetUpdates, "getUpdates", "updates");
    }

}
