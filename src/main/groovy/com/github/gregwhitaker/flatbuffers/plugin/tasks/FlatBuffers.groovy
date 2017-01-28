package com.github.gregwhitaker.flatbuffers.plugin.tasks

import com.github.gregwhitaker.flatbuffers.plugin.FlatBuffersPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

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
            return project.flatbuffers.lanugage.toLowerCase()
        } else {
            return null
        }
    }
}
