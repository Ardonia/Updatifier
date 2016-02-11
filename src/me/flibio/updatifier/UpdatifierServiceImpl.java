package me.flibio.updatifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdatifierServiceImpl extends UpdatifierService {

    protected UpdatifierServiceImpl(UpdatifierPlugin plugin) {
        super(plugin);
    }

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
