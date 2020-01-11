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
package io.netifi.flatbuffers.plugin.tasks

import groovy.io.FileType
import groovy.transform.CompileStatic
import io.netifi.flatbuffers.plugin.FlatBuffersLanguage
import io.netifi.flatbuffers.plugin.FlatBuffersPlugin
import io.netifi.flatbuffers.plugin.FlatBuffersPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.gradle.execution.commandline.TaskConfigurationException
import org.gradle.process.ExecSpec
import org.gradle.process.internal.ExecException

@CompileStatic
class FlatBuffers extends DefaultTask {

    @Optional
    @InputDirectory
    File inputDir

    @OutputDirectory
    File outputDir

    @Input
    String language

    @Input
    @Optional
    String extraArgs = ""

    @TaskAction
    void run() {
        createOutputDir()

        def flatcPath = getFlatcPath()

        getSchemas().each { File schema ->
            println "Compiling: '$schema.absolutePath'"

            try {
                project.exec { ExecSpec spec ->
                    spec.executable flatcPath
                    spec.args "--$language"
                    if (outputDir) {
                        spec.args '-o', "$outputDir.absolutePath"
                    }
                    if (extraArgs) {
                        spec.args extraArgs.split()
                    }
                    spec.args "$schema.absolutePath"
                    spec.workingDir(project.projectDir)

                    logger.debug("Running command: '${spec.commandLine.join(' ')}'")
                }.assertNormalExitValue()

            } catch (ExecException e) {
                throw new TaskExecutionException(this, e)
            }
        }
    }

    /**
     * Creates the output directory for generated FlatBuffers if it does not already exist.
     */
    private void createOutputDir() {
        if (!outputDir.exists()) {
            logger.debug("Creating output directory '{}'.", outputDir.absolutePath)
            outputDir.mkdirs()
        } else {
            logger.debug("Skipping creation of output directory '{}' as it already exists.", outputDir.absolutePath)
        }
    }

    /**
     * Validates that the configured language is supported by FlatBuffers.
     *
     * @param lang language to generate
     */
    private void validateLanguage(String lang) {
        if (FlatBuffersLanguage.get(lang) == null) {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("Unsupported value '${lang}' specified for property 'language'."))
        }
    }

    /**
     * Gets the path to the FlatBuffers compiler.
     *
     * @return path to the FlatBuffers compiler
     */
    private String getFlatcPath() {
        return project.extensions.getByType(FlatBuffersPluginExtension).flatcPath ?: 'flatc'
    }

    /**
     * Gets all schema files to use during FlatBuffers compilation.
     *
     * @return a list of all schema files to compile
     */
    private List<File> getSchemas() {
        def schemas = []
        getInputDir().eachFileRecurse(FileType.FILES) { file ->
            if (file.name ==~ /^.*\.fbs$/) {
                schemas << file
            }
        }
        return schemas
    }

    @Internal
    @Override
    String getGroup() {
        return FlatBuffersPlugin.GROUP
    }

    @Internal
    @Override
    String getDescription() {
        return 'Assembles FlatBuffers for this project.'
    }

    File getInputDir() {
        if (inputDir) {
            return inputDir
        } else {
            logger.debug("No 'inputDir' specified, using default inputDir '${project.projectDir}/src/main/flatbuffers}'.")
            return new File("${project.projectDir}/src/main/flatbuffers")
        }
    }

    String getLanguage() {
        FlatBuffersPluginExtension pluginExtension = project.extensions.getByType(FlatBuffersPluginExtension)
        if (language) {
            validateLanguage(language)
            logger.debug("Compiling code for language '{}' specified in the task configuration.", language)
            return language.toLowerCase()
        } else if (pluginExtension.language) {
            validateLanguage(pluginExtension.language)
            logger.debug("Compiling code using the default language '{}' specified in the 'flatbuffers' configuration.")
            return pluginExtension.language.toLowerCase()
        } else {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("No value has been specified for property 'language'."))
        }

    }
}
