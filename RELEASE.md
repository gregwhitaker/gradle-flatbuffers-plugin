# Plugin Release Instructions
Follow the steps below to create a new release of the gradle-flatbuffers-plugin.

1. Set Release Version

    Update the release version of the plugin in the [build.gradle](build.gradle) file.
    
2. Commit Release Version Change

    Commit the change to the release version and verify that the [Travis CI build](https://travis-ci.org/netifi/gradle-flatbuffers-plugin) passes.
    
3. Tag Release in Github

    Tag the release in Github with the release version. Travis CI will automatically build the tag and publish the
    new plugin version to the [Gradle Plugin Repository](https://plugins.gradle.org/plugin/io.netifi.flatbuffers).
    
4. Verify Plugin Published

    Verify that the plugin was published to the [Gradle Plugin Repository](https://plugins.gradle.org/plugin/io.netifi.flatbuffers) and that the latest
    version of the plugin is correct.