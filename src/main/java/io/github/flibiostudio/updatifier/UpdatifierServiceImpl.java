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

package io.github.flibiostudio.updatifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.flibio.updatifier.UpdatifierService;
import org.spongepowered.api.Sponge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UpdatifierServiceImpl implements UpdatifierService {

    protected HashMap<String, ReleaseData> releases = new HashMap<>();

    UpdatifierServiceImpl(UpdatifierPlugin plugin) {
        Sponge.getServiceManager().setProvider(plugin, UpdatifierService.class, this);
    }

    @Override
    public boolean updateAvailable(String repoOwner, String repoName, String currentVersion) {
        String latestRelease = HttpUtils.requestData("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/latest");
        String taggedRelease = HttpUtils.requestData("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/tags/"
                + currentVersion);
        if (latestRelease.toLowerCase().contains("\"message\": \"not found\"")
                || taggedRelease.toLowerCase().contains("\"message\": \"not found\"")) {
            // An error ocurred
            return false;
        }
        // Attempt to get the time released
        Gson gson = new GsonBuilder().create();
        ReleaseData lReleaseData = gson.fromJson(latestRelease, ReleaseData.class);
        ReleaseData tReleaseData = gson.fromJson(taggedRelease, ReleaseData.class);
        if (lReleaseData == null || tReleaseData == null || lReleaseData.publishedAt() == null || tReleaseData.publishedAt() == null) {
            return false;
        }
        releases.put(repoOwner + "/" + repoName, lReleaseData);
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

    /**
     * Gets the download URL for the latest release of the specified GitHub
     * repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The download URL of the latest release
     */
    @Override
    public String getDownloadUrl(String repoOwner, String repoName) {
        if (!releases.containsKey(repoOwner + "/" + repoName)) {
            return "";
        }
        ReleaseData releaseData = releases.get(repoOwner + "/" + repoName);
        if (releaseData.assets().length < 1) {
            return "";
        }
        return releaseData.assets()[0].browserDownloadUrl();
    }

    /**
     * Gets the file name for the latest release of the specified GitHub
     * repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The file name of the latest release
     */
    @Override
    public String getFileName(String repoOwner, String repoName) {
        if (!releases.containsKey(repoOwner + "/" + repoName)) {
            return "";
        }
        ReleaseData releaseData = releases.get(repoOwner + "/" + repoName);
        if (releaseData.assets().length < 1) {
            return "";
        }
        return releaseData.assets()[0].name();
    }

    /**
     * Gets the tag for the latest release of the specified GitHub repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The tag of the latest release
     */
    @Override
    public String getTag(String repoOwner, String repoName) {
        if (!releases.containsKey(repoOwner + "/" + repoName)) {
            return "";
        }
        ReleaseData releaseData = releases.get(repoOwner + "/" + repoName);
        return releaseData.getName();
    }

    /**
     * Gets the body for the latest release of the specified GitHub repository.
     *
     * @param repoOwner The owner of the repository
     * @param repoName The name of the repository
     * @return The body of the latest release
     */
    @Override
    public String getBody(String repoOwner, String repoName) {
        if (!releases.containsKey(repoOwner + "/" + repoName)) {
            return "";
        }
        ReleaseData releaseData = releases.get(repoOwner + "/" + repoName);
        return releaseData.body();
    }

}
