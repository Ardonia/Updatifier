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

import org.spongepowered.api.Sponge;

public abstract class UpdatifierService {

    protected static UpdatifierService instance = new UpdatifierService() {
        @Override
        public boolean updateAvailable(String repoOwner, String repoName, String currentVersion) {
            return false;
        }

        @Override
        public String getDownloadUrl(String repoOwner, String repoName) {
            return "";
        }

        @Override
        public String getFileName(String repoOwner, String repoName) {
            return "";
        }

        @Override
        public String getTag(String repoOwner, String repoName) {
            return "";
        }

        @Override
        public String getBody(String repoOwner, String repoName) {
            return "";
        }
    };

    private UpdatifierService() {
        instance = this;
    }

    protected UpdatifierService(Object plugin) {
        this();
        Sponge.getGame().getServiceManager().setProvider(plugin, UpdatifierService.class, this);
    }

    /**
     * Gets the instance of {@link UpdatifierService}.
     *
     * @return The instance of {@link UpdatifierService}
     */
    public static UpdatifierService getInstance() {
        return UpdatifierService.instance;
    }

    /**
     * Checks if an update is available for the specified GitHub repository.
     * Connects to GitHub, so it should be run in an async thread.
     *
     * @param repoOwner
     *        The owner of the repository
     * @param repoName
     *        The name of the repository
     * @param currentVersion
     *        The current plugin version to check against the latest release tag
     * @return If an update is available or not
     */
    public abstract boolean updateAvailable(String repoOwner, String repoName, String currentVersion);

    /**
     * Gets the download URL for the latest release of the specified GitHub
     * repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The download URL of the latest release
     */
    public abstract String getDownloadUrl(String repoOwner, String repoName);

    /**
     * Gets the file name for the latest release of the specified GitHub
     * repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The file name of the latest release
     */
    public abstract String getFileName(String repoOwner, String repoName);

    /**
     * Gets the tag for the latest release of the specified GitHub repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The tag of the latest release
     */
    public abstract String getTag(String repoOwner, String repoName);

    /**
     * Gets the body for the latest release of the specified GitHub repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The body of the latest release
     */
    public abstract String getBody(String repoOwner, String repoName);

}
