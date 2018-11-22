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
package io.netifi.flatbuffers.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class CleanFlatBuffersFuncTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    File outputDir
    String outputDirPath

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        outputDir = testProjectDir.newFolder('src', 'main', 'generated', 'flatbuffers')
        outputDirPath = outputDir.absolutePath
    }

    def "cleanFlatBuffers deletes the flatbuffers generated code directory"() {
        given:
        buildFile << """
            import io.netifi.flatbuffers.plugin.tasks.FlatBuffers

            plugins {
                id 'idea'
                id 'java'
                id 'io.netifi.flatbuffers'
            }
            
            flatbuffers {
                language = 'java'
            }
            
            task flatBuffers(type: FlatBuffers) {
                outputDir = file('${outputDirPath}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withDebug(true)
                .withProjectDir(testProjectDir.root)
                .withArguments('cleanFlatBuffers')
                .withPluginClasspath()
                .build()

        then:
        result.task(":cleanFlatBuffers").outcome == SUCCESS
        !outputDir.exists()
        outputDir.parentFile.exists()
    }
}
