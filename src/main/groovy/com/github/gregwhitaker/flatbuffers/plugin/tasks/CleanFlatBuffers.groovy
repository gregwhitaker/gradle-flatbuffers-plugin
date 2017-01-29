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

package com.github.gregwhitaker.flatbuffers.plugin.tasks

import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class CleanFlatBuffers extends DefaultTask {

    @Input
    File outputDir

    @TaskAction
    void run() {
        if (outputDir) {
            if (outputDir.exists()) {
                getLogger().debug("Cleaning output directory '{}'.", outputDir.absolutePath)
                outputDir.deleteDir()
            }
        } else {
            getLogger().debug("Skipping clean of output directory '{}' as it does not exist.", outputDir.absolutePath)
        }
    }

    @Internal
    @Override
    String getGroup() {
        return FlatBuffersPlugin.GROUP
    }

    @Internal
    @Override
    String getDescription() {
        return 'Deletes the flatbuffers build directory.'
    }
    
}
