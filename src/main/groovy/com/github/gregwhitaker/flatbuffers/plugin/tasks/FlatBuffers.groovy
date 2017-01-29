package com.github.gregwhitaker.flatbuffers.plugin.tasks

import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersPlugin
import org.gradle.api.DefaultTask
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
        println outputDir
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
            return language.toLowerCase()
        } else if (project.flatbuffers.language) {
            return project.flatbuffers.language.toLowerCase()
        } else {
            throw new TaskConfigurationException(path,
                    "A problem was found with the configuration of task '" + name + "'.",
                    new IllegalArgumentException("No value has been specified for property 'language'."))
        }
    }

}
