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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileManager {

    private static final Map<TypeToken<?>, TypeSerializer<?>> serializers = new HashMap<>();

    private final Logger logger;
    private final File configFile;
    private final HoconConfigurationLoader manager;
    private final ConfigurationNode configRoot;
    private final TypeSerializerCollection collection;

    public FileManager(Logger logger, Path path, String configName) {
        this.logger = logger;
        this.configFile = path.toAbsolutePath().resolve(configName).toFile();
        this.manager = HoconConfigurationLoader.builder().setFile(configFile).build();
        try {
            this.configRoot = this.manager.load();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        this.collection = this.configRoot.getOptions().getSerializers();
    }

    public <T> void addType(Class<T> type, TypeSerializer<? super T> serializer) {
        this.collection.registerType(TypeToken.of(type), serializer);
    }

    public <T> void testDefault(String path, Class<T> type, T value) {
        try {
            if (configRoot.getNode(path.split("\\.")).getValue(TypeToken.of(type)) == null) {
                set(path, type, value);
            }
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage());
        }
    }

    public <T> void set(String path, Class<T> type, T value) {
        ConfigurationNode node = configRoot.getNode(path.split("\\."));
        node.getOptions().setAcceptedTypes(null).setSerializers(collection);
        try {
            node.setValue(TypeToken.of(type), value);
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage());
        }
        saveConfigFile(configRoot);
    }

    public <T> Optional<T> getConfigValue(String path, Class<T> type) {
        ConfigurationNode node = configRoot.getNode(path.split("\\."));
        node.getOptions().setAcceptedTypes(null).setSerializers(collection);
        try {
            T value = node.getValue(TypeToken.of(type));
            return Optional.ofNullable(value);
        } catch (ObjectMappingException e) {
            logger.error(e.getMessage());
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

    public void saveConfigFile(ConfigurationNode root) {
        try {
            manager.save(root);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
