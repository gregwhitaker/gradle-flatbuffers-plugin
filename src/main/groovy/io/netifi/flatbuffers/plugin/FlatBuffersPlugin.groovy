/*
 * Copyright 2017 - 2018 Netifi Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.netifi.flatbuffers.plugin

import groovy.transform.CompileStatic
import io.netifi.flatbuffers.plugin.tasks.FlatBuffers
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Delete
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.util.GUtil

import static org.gradle.api.plugins.JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME

@CompileStatic
class FlatBuffersPlugin implements Plugin<Project> {

    public static final String GROUP = 'FlatBuffers'
    public static final String FLAT_BUFFERS_EXTENSION_NAME = 'flatbuffers'

    private FlatBuffersPluginExtension extension
    private Project project

    @Override
    void apply(Project project) {
        this.project = project
        configureProject()
    }

    void configureProject() {
        extension = project.extensions.create(FLAT_BUFFERS_EXTENSION_NAME, FlatBuffersPluginExtension)
        project.pluginManager.apply(BasePlugin)

        project.afterEvaluate {
            project.tasks.withType(FlatBuffers).each {
                applySourceSets(it)
                reconfigurePlugins(it)
                addCleanTask(it)
            }
            applyDependencies(project)
        }
    }

    /**
     * Adds a 'clean' flatBuffers for any FlatBuffers tasks in the project.
     *
     * @param flatBuffersTask {@link FlatBuffers} flatBuffers
     */
    void addCleanTask(FlatBuffers flatBuffersTask) {
        def taskName = "clean${GUtil.toCamelCase(flatBuffersTask.name)}"
        project.tasks.create(name: taskName, type: Delete) { Delete task ->
            task.delete flatBuffersTask.outputDir
        }
    }

    /**
     * Adds source sets for the FlatBuffers input and output directories.
     *
     * @param task {@link FlatBuffers} task
     */
    void applySourceSets(FlatBuffers task) {
        project.pluginManager.withPlugin('java') {
            def javaPlugin = project.convention.getPlugin(JavaPluginConvention)
            def sourceSets = javaPlugin.sourceSets
            sourceSets.getByName(task.sourceSet).java { SourceDirectorySet java ->
                java.srcDirs task.inputDir, task.outputDir
            }
        }
    }

    /**
     * Reconfigures certain plugins to know about the FlatBuffers project structure.
     *
     * @param task {@link FlatBuffers} task
     */
    void reconfigurePlugins(FlatBuffers task) {
        // Intellij specific configurations
        project.pluginManager.withPlugin('idea') {
            def idea = project.extensions.getByType(IdeaModel)
            idea.module.generatedSourceDirs += task.outputDir
        }
    }

    /**
     * Adds FlatBuffers dependencies to the project.
     *
     * @param project Gradle project
     */
    void applyDependencies(Project project) {
        // Java specific dependencies
        project.pluginManager.withPlugin('java') {
            project.configurations.getByName(IMPLEMENTATION_CONFIGURATION_NAME) { Configuration config ->
                def flatBufferVersion = "com.google.flatbuffers:flatbuffers-java:${extension.flatBuffersVersion ?: '1.10.0'}"
                config.dependencies.add(project.dependencies.create(flatBufferVersion))
            }
        }
    }
}
