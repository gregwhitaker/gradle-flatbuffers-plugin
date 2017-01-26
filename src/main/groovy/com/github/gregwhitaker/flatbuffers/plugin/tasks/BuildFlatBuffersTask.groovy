package com.github.gregwhitaker.flatbuffers.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
@CacheableTask
class BuildFlatBuffersTask extends DefaultTask {

    @TaskAction
    void run() {
        println "BuildFlatBuffersTask"
    }
    
}
