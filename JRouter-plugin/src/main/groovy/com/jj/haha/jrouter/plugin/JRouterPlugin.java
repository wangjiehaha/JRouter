package com.jj.haha.jrouter.plugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Objects;

public class JRouterPlugin implements Plugin<Project> {

    private IServiceGenerator stubServiceGenerator = new StubServiceGenerator();
    private static final String DISPATCHER_EXTENSION_NAME = "dispatcher";

    @Override
    public void apply(Project project) {
        project.getExtensions().create(DISPATCHER_EXTENSION_NAME, DispatcherExtension.class);
        stubServiceGenerator.injectStubServiceToManifest(project);
        Objects.requireNonNull(project.getExtensions().findByType(BaseExtension.class))
                .registerTransform(new JRouterTransform());
    }
}
