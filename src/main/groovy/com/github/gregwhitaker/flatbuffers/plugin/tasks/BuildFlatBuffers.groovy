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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

import java.nio.file.Files
import java.nio.file.Paths

class BuildFlatBuffers extends DefaultTask {
    private File inputDir = project.flatbuffers.inputDir
    private File outputDir = project.flatbuffers.outputDir

    @TaskAction
    void run() {
        validateFlatBuffersCompilerPath()
        validateInputDir()

        createOutputDir()

        String flatcPath = project.flatbuffers.flatcPath
    }

    @Optional
    @InputDirectory
    File getInputDir() {
        return inputDir
    }

    @Optional
    @OutputDirectory
    File getOutputDir() {
        return outputDir
    }

    /**
     * Validates that the configured flatbuffers compiler path exists.
     */
    void validateFlatBuffersCompilerPath() {
        String flatcPath = project.flatbuffers.flatcPath

        if (flatcPath) {
            if (!Files.exists(Paths.get(flatcPath))) {
                throw new TaskConfigurationException(getPath(), "The configured 'flatc' path does not exist", new FileNotFoundException())
            }
        }
    }

    /**
     * Validates that the configured flatbuffers input directory exists.
     */
    void validateInputDir() {
        if (!getInputDir().exists()) {
            throw new TaskConfigurationException(getPath(), "The configured 'inputDir' path does not exist", new FileNotFoundException())
        }
    }

    /**
     * Creates the output directory for generated flatbuffers if it does not exist.
     */
    void createOutputDir() {
        if (!getOutputDir().exists()) {
            getOutputDir().mkdir()
        }
    }

}
