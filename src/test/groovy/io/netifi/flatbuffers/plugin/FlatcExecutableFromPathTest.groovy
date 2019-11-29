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
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification

import java.nio.file.Paths

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@IgnoreIf({ env.TRAVIS || env.CI })
@Requires({ os.linux })
class FlatcExecutableFromPathTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()


    File buildFile
    File outputDir
    File bufferSpec
    String flatcPath
    String outputDirPath

    def setup () {
        buildFile = testProjectDir.newFile('build.gradle')
        outputDir = testProjectDir.newFolder('src', 'main', 'generated', 'java')
        testProjectDir.newFolder("src", "main", 'flatbuffers')
        bufferSpec = testProjectDir.newFile("src/main/flatbuffers/vehicle.fbs")
        outputDirPath = outputDir.absolutePath
        flatcPath = "which -p flatc".execute().text
    }

    def "can find flatc compiler on system path" () {
        given:
        buildFile << """
            import io.netifi.flatbuffers.plugin.tasks.FlatBuffers

            plugins {
                id 'java'
                id 'io.netifi.flatbuffers'
            }
            
            flatbuffers {
                language = 'java'
                flatcPath = 'flatc'
            }
            
            task flatBuffers1(type: FlatBuffers) {
                outputDir = file('$outputDirPath')
            }
            """.stripIndent()

        bufferSpec << """
            namespace com.sample;
            
            table Vehicle {
                /// The type
                vehicleType: string (required);
                /// The name of the vehicle
                vehicleName: string (required);
                /// The weight of the vehicle (lbs)
                vehicleWeight: float;
            }
            
            root_type Vehicle;
            """.stripIndent()

        when:
        def result = GradleRunner.create()
                                 .withDebug(true)
                                 .forwardOutput()
                                 .withProjectDir(testProjectDir.root)
                                 .withArguments("flatBuffers1")
                                 .withPluginClasspath()
                                 .build()

        then:
        result.task(":flatBuffers1").outcome == SUCCESS
        Paths.get(outputDirPath, "com", "sample", "Vehicle.java").toFile().exists()
    }
}
