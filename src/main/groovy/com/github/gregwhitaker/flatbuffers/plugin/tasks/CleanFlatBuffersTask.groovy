package com.github.gregwhitaker.flatbuffers.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class CleanFlatBuffersTask extends DefaultTask {

    @TaskAction
    void run() {
        println "CleanFlatBuffersTask"
    }

}
