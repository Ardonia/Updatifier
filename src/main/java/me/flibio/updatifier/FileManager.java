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

package me.flibio.updatifier;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class FileManager {

    private final Logger logger;
    private final ConfigurationLoader<CommentedConfigurationNode> manager;
    private ConfigurationNode configRoot;

    public FileManager(Logger logger, ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.logger = logger;
        this.manager = loader;
        try {
            this.configRoot = this.manager.load();
        } catch (IOException e) {
            this.configRoot = this.manager.createEmptyNode();
        }
    }

    public FileManager(Logger logger, Path path, String configName) {
        this(logger, HoconConfigurationLoader.builder().setPath(path.toAbsolutePath().resolve(configName)).build());
    }

    public <T> void testDefault(String path, Class<T> type, T value) {
        try {
            if (configRoot.getNode((Object[]) path.split("\\.")).getValue(TypeToken.of(type)) == null) {
                set(path, type, value);
            }
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public <T> void set(String path, Class<T> type, T value) {
        ConfigurationNode node = configRoot.getNode((Object[]) path.split("\\."));
        try {
            node.setValue(TypeToken.of(type), value);
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage(), e);
        }
        try {
            manager.save(configRoot);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public <T> Optional<T> getConfigValue(String path, Class<T> type) {
        ConfigurationNode node = configRoot.getNode((Object[]) path.split("\\."));
        try {
            T value = node.getValue(TypeToken.of(type));
            return Optional.ofNullable(value);
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public <T> T getOrDefault(String path, Class<T> type, T value) {
        Optional<T> optT = getConfigValue(path, type);
        if (optT.isPresent()) {
            return optT.get();
        }
        set(path, type, value);
        return value;
    }

}
