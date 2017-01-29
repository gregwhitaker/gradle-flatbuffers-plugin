package com.github.gregwhitaker.flatbuffers.plugin.tasks

import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersLanguage
import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersPlugin
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

@ParallelizableTask
class FlatBuffers extends DefaultTask {

    @Optional
    @Input
    File inputDir

    @Input
    File outputDir

    @Optional
    @Input
    String language

    @TaskAction
    void run() {
        createOutputDir()

        def flatcPath = getFlatcPath()
        def inputDir = getInputDir()

        getSchemas().each {
            println "Compiling: '${it}'"

            def cmd = "${flatcPath} --${language} -o ${outputDir} ${it}"

            getLogger().debug("Running command: '${cmd}'")
            cmd.execute([], project.projectDir)
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
                    new IllegalArgumentException("Unsupported value '" + lang + "' specified for property 'language'."))
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

    File getInputDir() {
        if (inputDir) {
            return inputDir
        } else {
            getLogger().debug("No 'inputDir' specified, using default inputDir '${project.projectDir}/src/main/flatbuffers}'.")
            return new File("${project.projectDir}/src/main/flatbuffers")
        }
    }

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

}
