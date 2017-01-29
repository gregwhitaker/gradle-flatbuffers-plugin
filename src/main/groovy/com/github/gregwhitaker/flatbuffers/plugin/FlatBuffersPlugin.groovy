/*
 * Copyright 2017 Greg Whitaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gregwhitaker.flatbuffers.plugin

import com.github.gregwhitaker.flatbuffers.plugin.tasks.CleanFlatBuffers
import com.github.gregwhitaker.flatbuffers.plugin.tasks.FlatBuffers
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GUtil

class FlatBuffersPlugin implements Plugin<Project> {

    public static final String GROUP = 'FlatBuffers'

    @Override
    void apply(Project project) {
        project.extensions.create('flatbuffers', FlatBuffersPluginExtension.class)

        project.afterEvaluate({
            project.tasks.withType(FlatBuffers).each {
                addCleanTask(project, it)
            }

            project.dependencies {
                compile 'com.github.davidmoten:flatbuffers-java:1.4.0.1'
            }
        })
    }

    /**
     * Automatically adds a 'clean' task for any FlatBuffers tasks in the project.
     *
     * @param project Gradle project
     * @param task FlatBuffers task
     */
    void addCleanTask(Project project, FlatBuffers task) {
        def taskName = 'clean' + GUtil.toCamelCase(task.name)
        project.tasks.create(taskName, CleanFlatBuffers) {
            outputDir = task.outputDir
        }
    }

}
