package me.flibio.updatifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdatifierAPI {
	
	public UpdatifierAPI() {
		
	}
	
	/**
	 * Checks if an update is available for the specified GitHub repository.
	 * Connects to GitHub, so it should be run in an async thread.
	 * @param repoOwner
	 * 	The owner of the repository
	 * @param repoName
	 * 	The name of the repository
	 * @param currentVersion
	 * 	The current plugin version to check against the latest release tag
	 * @param type
	 * 	How the versions will be compared
	 * @return
	 * 	If an update is available or not
	 */
	public boolean updateAvailable(String repoOwner, String repoName, String currentVersion) {
		String latestRelease = HttpUtils.requestData("https://api.github.com/repos/"+repoOwner+"/"+repoName+"/releases/latest");
		String taggedRelease = HttpUtils.requestData("https://api.github.com/repos/"+repoOwner+"/"+repoName+"/releases/tags/"+currentVersion);
		if(latestRelease.toLowerCase().contains("\"message\": \"not found\"")||
				taggedRelease.toLowerCase().contains("\"message\": \"not found\"")) {
			//An error ocurred
			return false;
		} else {
			//Attempt to get the time released
			Gson gson = new GsonBuilder().create();
			ReleaseData lReleaseData = gson.fromJson(latestRelease, ReleaseData.class);
			ReleaseData tReleaseData = gson.fromJson(taggedRelease, ReleaseData.class);
			if(lReleaseData==null||tReleaseData==null) {
				return false;
			} else {
				if(lReleaseData.publishedAt()==null||tReleaseData.publishedAt()==null) {
					return false;
				} else {
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
		}
	}

}
