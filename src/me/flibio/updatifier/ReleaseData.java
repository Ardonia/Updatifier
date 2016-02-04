package me.flibio.updatifier;

public class ReleaseData {
	
	private String tag_name;
	private boolean prerelease;
	private String html_url;
	private String published_at;
	
	public String getName() {
		return tag_name;
	}
	
	public String getUrl() {
		return html_url;
	}
	
	public String publishedAt() {
		return published_at;
	}
	
	public boolean isPreRelease() {
		return prerelease;
	}
	
}
