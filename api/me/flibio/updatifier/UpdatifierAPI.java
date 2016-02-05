/**
 * This file is part of Updatifier, licensed under the MIT License (MIT).
 * <p>
 * Copyright (c) Flibio <http://github.com/Flibio>
 * Copyright (c) contributors
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.flibio.updatifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.spongepowered.api.Sponge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class UpdatifierAPI {

    protected UpdatifierAPI() {
    }

    /**
     * Gets the instance stored in the {@link org.spongepowered.api.service.ServiceManager} of Sponge API.
     *
     * @return The instance of {@link UpdatifierAPI}
     */
    public static UpdatifierAPI getInstance() {
        Optional<UpdatifierAPI> optInstance = Sponge.getGame().getServiceManager().provide(UpdatifierAPI.class);
        assert optInstance.isPresent() : "Updatifier API has not initialized yet";
        return optInstance.get();
    }

    /**
     * Checks if an update is available for the specified GitHub repository.
     * Connects to GitHub, so it should be run in an async thread.
     *
     * @param repoOwner      The owner of the repository
     * @param repoName       The name of the repository
     * @param currentVersion The current plugin version to check against the latest release tag
     * @return If an update is available or not
     */
    public boolean updateAvailable(String repoOwner, String repoName, String currentVersion) {
        String latestRelease = HttpUtils.requestData("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/latest");
        String taggedRelease = HttpUtils.requestData("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/tags/" + currentVersion);
        if (latestRelease.toLowerCase().contains("\"message\": \"not found\"") ||
                taggedRelease.toLowerCase().contains("\"message\": \"not found\"")) {
            //An error ocurred
            return false;
        }
        //Attempt to get the time released
        Gson gson = new GsonBuilder().create();
        ReleaseData lReleaseData = gson.fromJson(latestRelease, ReleaseData.class);
        ReleaseData tReleaseData = gson.fromJson(taggedRelease, ReleaseData.class);
        if (lReleaseData == null || tReleaseData == null ||
                lReleaseData.publishedAt() == null || tReleaseData.publishedAt() == null) {
            return false;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String latestTime = lReleaseData.publishedAt();
        String taggedTime = tReleaseData.publishedAt();
        Date latestDate, taggedDate;
        try {
            latestDate = formatter.parse(latestTime.replaceAll("T", " ").replaceAll("Z", ""));
            taggedDate = formatter.parse(taggedTime.replaceAll("T", " ").replaceAll("Z", ""));
            return taggedDate.before(latestDate);
        } catch (ParseException e) {
            return false;
        }
    }

}
