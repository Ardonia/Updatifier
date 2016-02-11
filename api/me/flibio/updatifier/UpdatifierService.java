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

public class UpdatifierService {

    protected static UpdatifierService instance = new UpdatifierService();

    private UpdatifierService() {
        instance = this;
    }

    protected UpdatifierService(Object plugin) {
        this();
        Sponge.getGame().getServiceManager().setProvider(plugin, UpdatifierService.class, this);
    }

    /**
     * Gets the instance stored in the
     * {@link org.spongepowered.api.service.ServiceManager} of Sponge API.
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
    public boolean updateAvailable(String repoOwner, String repoName, String currentVersion) {
        return false;
    }

}
