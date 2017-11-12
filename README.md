# gradle-flatbuffers-plugin
[![Build Status](https://travis-ci.org/netifi/gradle-flatbuffers-plugin.svg?branch=master)](https://travis-ci.org/netifi/gradle-flatbuffers-plugin)

Gradle plugin for generating code from Google [FlatBuffers](https://google.github.io/flatbuffers/) schemas.

## Requirements

This plugin requires that the FlatBuffers compiler be installed on the system.

For information on building and installing the compiler please refer to the [FlatBuffers Documentation](https://google.github.io/flatbuffers/flatbuffers_guide_building.html).

## Usage
Please see the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/com.github.gregwhitaker.flatbuffers) for instructions on including this plugin in your project.

### Extension Properties
The plugin defines the following extension properties in the `flatbuffers` closure:

| Property  | Type   | Default Value | Required | Description                                        |
|-----------|--------|---------------|----------|----------------------------------------------------|
| flatcPath | String | flatc         | False    | The path to the flatc compiler.                    |
| language  | String | null          | False    | The language to use when compiling the FlatBuffers.|

*Note:* Please see the [Supported Languages](#supported-languages) section for valid `language` values.

### Custom Task Types
The plugin provides the following custom task types for generating FlatBuffers:

| Type                        | Description                   |
|-----------------------------|-------------------------------|
| [FlatBuffers](#flatbuffers) | Compiles FlatBuffers schemas. |

#### FlatBuffers
This task type compiles FlatBuffers schemas.

```$groovy
    task createFlatBuffers(type: FlatBuffers) {
        inputDir = file("src/main/flatbuffers")
        outputDir = file("src/generated/flatbuffers")
        language = 'java'
    }
```

| Property  | Type   | Default Value          | Required | Description                                         |
|-----------|--------|------------------------|----------|-----------------------------------------------------|
| inputDir  | File   | `src/main/flatbuffers` | False    | The path to the schemas directory.                  |
| outputDir | File   | null                   | True     | The path to the directory for compiled FlatBuffers. | 
| language  | String | value in extension     | False    | The language to use when compiling the schemas.     |


*Note:* Please see the [Supported Languages](#supported-languages) section for valid `language` values.

### Supported Languages
The plugin supports generating code in all languages currently supported by FlatBuffers:

| Language   | Property Value |
|------------|----------------|
| C++        | cpp            |
| Java       | java           |
| C#         | csharp         |
| Go         | go             |
| Python     | python         |
| Javascript | javascript     |
| PHP        | php            |
| GRPC       | grpc           |

### Example
This example generates Java FlatBuffers from the schema files in the default `src/main/flatbuffers` directory and places the generated code in `src/generated/flatbuffers`.

```$groovy
    import com.github.gregwhitaker.flatbuffers.plugin.tasks.FlatBuffers
    
    plugins {
      id "com.github.gregwhitaker.flatbuffers" version "1.0.2"
    }

    flatbuffers {
        flatcPath = '/Users/greg/bin/flatc'
        language = 'java'
    }
    
    task createFlatBuffers(type: FlatBuffers) {
        outputDir = file("src/generated/flatbuffers")
    }
```

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/netifi/gradle-flatbuffers-plugin/issues).

## License
Copyright 2017 Netifi Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
