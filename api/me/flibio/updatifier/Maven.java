package me.flibio.updatifier;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation indicating that a Sponge plugin has a update
 * site on maven.
 *
 * @author liach
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Maven {

    /**
     * Gets the repository of this artifact.
     *
     * @return The maven repository
     */
    String repo();

    /**
     * Gets the group of this artifact.
     *
     * @return The group ID
     */
    String groupId();

    String artifactId();

    String version();

    String classifier() default "";

}
