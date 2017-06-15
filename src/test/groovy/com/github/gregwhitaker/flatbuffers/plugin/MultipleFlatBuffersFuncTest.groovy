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

package com.github.gregwhitaker.flatbuffers.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS


class MultipleFlatBuffersFuncTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    File inputDir1
    File inputDir2
    String inputDirPath1
    String inputDirPath2

    File outputDir1
    File outputDir2
    String outputDirPath1
    String outputDirPath2

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        inputDir1 = testProjectDir.newFolder('src', 'main', 'flatbuffers1')
        inputDir2 = testProjectDir.newFolder('src', 'main', 'flatbuffers2')
        inputDirPath1 = inputDir1.absolutePath
        inputDirPath2 = inputDir2.absolutePath

        outputDir1 = testProjectDir.newFolder('src', 'main', 'generated', 'flatbuffers1')
        outputDir2 = testProjectDir.newFolder('src', 'main', 'generated', 'flatbuffers2')
        outputDirPath1 = outputDir1.absolutePath
        outputDirPath2 = outputDir2.absolutePath
    }

    def "supports multiple flatbuffers tasks"() {
        given:
        buildFile << """
            import com.github.gregwhitaker.flatbuffers.plugin.tasks.FlatBuffers

            plugins {
                id 'idea'
                id 'java'
                id 'com.github.gregwhitaker.flatbuffers'
            }
            
            flatbuffers {
                language = 'java'
            }
            
            task flatBuffers1(type: FlatBuffers) {
                inputDir = file('${inputDirPath1}')
                outputDir = file('${outputDirPath1}')
            }
            
            task flatBuffers2(type: FlatBuffers) {
                inputDir = file('${inputDirPath2}')
                outputDir = file('${outputDirPath2}')
            }
        """

        when:
        def result1 = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments('flatBuffers1')
                .withPluginClasspath()
                .build()

        def result2 = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments('flatBuffers2')
                .withPluginClasspath()
                .build()

        then:
        result1.task(":flatBuffers1").outcome == SUCCESS
        result2.task(":flatBuffers2").outcome == SUCCESS
    }
}
