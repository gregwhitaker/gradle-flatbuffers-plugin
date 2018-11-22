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
import io.netifi.flatbuffers.plugin.FlatBuffersLanguage
import io.netifi.flatbuffers.plugin.FlatBuffersPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.execution.commandline.TaskConfigurationException

import java.util.concurrent.TimeUnit

class FlatBuffers extends DefaultTask {

    private File inputDir
    private File outputDir
    private String language
    private String extraArgs = new String()

    @TaskAction
    void run() {
        createOutputDir()

        def flatcPath = getFlatcPath()

        getSchemas().each {
            println "Compiling: '${it}'"

            def cmd = "${flatcPath} --${language} -o ${outputDir} ${extraArgs} ${it}"

            getLogger().debug("Running command: '${cmd}'")

            try {
                Process process = cmd.execute([], project.projectDir)
                def exited = process.waitFor(30, TimeUnit.SECONDS)

                if (exited) {
                    if (process.exitValue() != 0) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))
                        StringBuilder builder = new StringBuilder()

                        String line = null
                        while ( (line = reader.readLine()) != null) {
                            builder.append(line)
                            builder.append(System.getProperty("line.separator"))
                        }

                        throw new TaskExecutionException(this, new Exception(builder.toString()))
                    }
                } else {
                    throw new TaskExecutionException(this, new Exception("Timed out while compiling '${it}'."))
                }
            } catch (IOException e) {
                throw new TaskExecutionException(this, e)
            }
        }
    }

    /**
     * Creates the output directory for generated FlatBuffers if it does not already exist.
     */
    private void createOutputDir() {
        if (!outputDir.exists()) {
            getLogger().debug("Creating output directory '{}'.", outputDir.absolutePath)
            outputDir.mkdirs()
        } else {
            getLogger().debug("Skipping creation of output directory '{}' as it already exists.", outputDir.absolutePath)
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
    @Internal
    private String getFlatcPath() {
        String path = project.flatbuffers.flatcPath

        if (path) {
            if (!path.endsWith("/flatc")) {
                path += "/flatc"
            }
        } else {
            // Assuming that flatc is on the system path
            path = "flatc"
        }

        return path
    }

    /**
     * Gets all schema files to use during FlatBuffers compilation.
     *
     * @return a list of all schema files to compile
     */
    @Internal
    private File[] getSchemas() {
        def schemas = []
        getInputDir().eachFileRecurse(FileType.FILES) { file ->
            if (file.name.endsWith(".fbs")) {
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

    @Optional
    @InputDirectory
    File getInputDir() {
        if (inputDir) {
            return inputDir
        } else {
            getLogger().debug("No 'inputDir' specified, using default inputDir '${project.projectDir}/src/main/flatbuffers}'.")
            return new File("${project.projectDir}/src/main/flatbuffers")
        }
    }

    void setInputDir(File inputDir) {
        this.inputDir = inputDir
    }

    @OutputDirectory
    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(File outputDir) {
        this.outputDir = outputDir
    }

    @Optional
    @Input
    String getLanguage() {
        if (language) {
            validateLanguage(language)
            getLogger().debug("Compiling code for language '{}' specified in the task configuration.", language)
            return language.toLowerCase()
        } else if (project.flatbuffers.language) {
            validateLanguage(project.flatbuffers.language)
            getLogger().debug("Compiling code using the default language '{}' specified in the 'flatbuffers' configuration.")
            return project.flatbuffers.language.toLowerCase()
        } else {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("No value has been specified for property 'language'."))
        }

    }

    void setLanguage(String language) {
        this.language = language
    }

    @Optional
    @Input
    String getExtraArgs() {
        return this.extraArgs;
    }

    void setExtraArgs(String extraArgs) {
        this.extraArgs = extraArgs
    }
}
