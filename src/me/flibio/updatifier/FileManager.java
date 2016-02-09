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

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;

import com.typesafe.config.ConfigException;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private Logger logger;
    private ConfigurationNode configRoot;

    public FileManager(Logger logger) {
        this.logger = logger;
    }

    public void testDefault(String path, Object value) {
        if (configRoot != null) {
            // Check if the configuration file doesn't contain the path
            if (configRoot.getNode((Object[]) path.split("\\.")).getValue() == null) {
                // Set the path to the default value
                configRoot.getNode((Object[]) path.split("\\.")).setValue(value);
                saveConfigFile(configRoot);
            }
        }
    }

    public String getConfigValue(String path) {
        if (configRoot != null) {
            // Check if the configuration file contains the path
            if (configRoot.getNode((Object[]) path.split("\\.")).getValue() != null) {
                // Get the value and return it
                return configRoot.getNode((Object[]) path.split("\\.")).getString();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public void generateFolder(String path) {
        File folder = new File(path);
        try {
            if (!folder.exists()) {
                folder.mkdir();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void generateFile(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void loadConfigFile() {
        String fileName = "config.conf";
        ConfigurationLoader<?> manager = HoconConfigurationLoader.builder().setFile(new File("config/Updatifier/" + fileName)).build();
        ConfigurationNode root;
        try {
            root = manager.load();
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        } catch (ConfigException e) {
            logger.error(e.getMessage());
            return;
        }
        configRoot = root;
    }

    public ConfigurationNode getConfigFile() {
        return configRoot;
    }

    public void saveConfigFile(ConfigurationNode root) {
        String fileName = "config.conf";
        ConfigurationLoader<?> manager = HoconConfigurationLoader.builder().setFile(new File("config/Updatifier/" + fileName)).build();
        try {
            manager.save(root);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
