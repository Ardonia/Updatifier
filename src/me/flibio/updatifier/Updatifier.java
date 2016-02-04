package me.flibio.updatifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Updatifier {
	
	/**
	 * Gets the owner of the GitHub repository that the releases are hosted on.
	 * @return
	 * 	The owner of the GitHub repository that the releases are hosted on.
	 */
	String repoOwner();
	
	/**
	 * Gets the name of the GitHub repository that the releases are hosted on.
	 * @return
	 * 	The name of the GitHub repository that the releases are hosted on.
	 */
	String repoName();
	
	/**
	 * Gets the current version of the plugin. Must be exactly the same as the
	 * corresponding tag on GitHub.
	 * @return
	 * 	The current version of the plugin
	 */
	String version();
}
