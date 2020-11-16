package com.jj.haha.jrouter.plugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Objects;

public class JRouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        Objects.requireNonNull(project.getExtensions().findByType(BaseExtension.class))
                .registerTransform(new JRouterTransform());
    }
}
