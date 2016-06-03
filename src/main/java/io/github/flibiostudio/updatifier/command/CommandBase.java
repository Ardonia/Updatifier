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

import static io.github.flibiostudio.updatifier.UpdatifierPlugin.getInjector;
import static java.util.Arrays.asList;
import static org.spongepowered.api.text.Text.of;

import io.github.flibiostudio.updatifier.UpdatifierPlugin;
import io.github.flibiostudio.updatifier.command.annotation.Aliases;
import io.github.flibiostudio.updatifier.command.annotation.Children;
import io.github.flibiostudio.updatifier.command.annotation.Description;
import io.github.flibiostudio.updatifier.command.annotation.NoExecutor;
import io.github.flibiostudio.updatifier.command.annotation.Permission;
import org.slf4j.Logger;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import javax.annotation.Nonnull;

/**
 * The base class for all commands.
 */
public abstract class CommandBase implements CommandExecutor {

    private String[] aliases;
    protected final UpdatifierPlugin plugin;
    protected final Logger logger;

    private CommandBase() {
        this(null);
    }

    protected CommandBase(UpdatifierPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public final String[] getAliases() {
        if (getClass().isAnnotationPresent(Aliases.class)) {
            return asList(getClass().getAnnotation(Aliases.class).value()).stream().filter(alias -> !alias.equals("")).toArray(String[]::new);
        }
        return new String[0];
    }

    public final CommandSpec getSpec() {
        return builder().build();
    }

    @Nonnull
    protected CommandSpec.Builder builder() {
        CommandSpec.Builder builder = CommandSpec.builder();

        if (getClass().isAnnotationPresent(Children.class)) {
            asList(getClass().getAnnotation(Children.class).value()).stream().filter(clazz -> !getClass().equals(clazz))
                    .map(clazz -> getInjector().getInstance(clazz)).forEach(base -> builder.child(base.getSpec(), base.getAliases()));
        }

        if (getClass().isAnnotationPresent(Description.class)) {
            builder.description(of(getClass().getAnnotation(Description.class).value()));
        }

        if (!getClass().isAnnotationPresent(NoExecutor.class)) {
            builder.executor(this);
        }

        if (getClass().isAnnotationPresent(Permission.class)) {
            builder.permission(getClass().getAnnotation(Permission.class).value());
        }

        return builder;
    }

}
