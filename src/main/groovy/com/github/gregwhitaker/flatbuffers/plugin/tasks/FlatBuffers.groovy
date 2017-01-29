package com.github.gregwhitaker.flatbuffers.plugin.tasks

import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersLanguage
import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction
import org.gradle.execution.commandline.TaskConfigurationException

@ParallelizableTask
class FlatBuffers extends DefaultTask {

    @Input
    File outputDir

    @Optional
    @Input
    String language

    @TaskAction
    void run() {
        createOutputDir()
    }

    /**
     * Creates the output directory for generated flatbuffers if it does not already exist.
     */
    private void createOutputDir() {
        if (!outputDir.exists()) {
            getLogger().debug("Creating output directory '{}'.", outputDir.absolutePath)
            outputDir.mkdirs()
        } else {
            getLogger().debug("Skipping creation of output directory '{}' as it already exists.", outputDir.absolutePath)
        }
    }

    private void validateLanguage(String lang) {
        if (FlatBuffersLanguage.get(lang) == null) {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("Unsupported value '" + lang + "' specified for property 'language'."))
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
        return 'Assembles flatbuffers for this project.'
    }

    String getLanguage() {
        if (language) {
            validateLanguage(language)
            getLogger().debug("Generating code for language '{}' specified in the task configuration.", language)
            return language.toLowerCase()
        } else if (project.flatbuffers.language) {
            validateLanguage(project.flatbuffers.language)
            getLogger().debug("Generating code using the default language '{}' specified in the 'flatbuffers' configuration.")
            return project.flatbuffers.language.toLowerCase()
        } else {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("No value has been specified for property 'language'."))
        }

    }

}
