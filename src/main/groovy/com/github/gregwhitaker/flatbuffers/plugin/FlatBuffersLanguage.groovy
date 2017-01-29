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

/**
 * Enumeration of all languages supported by the FlatBuffers compiler.
 */
enum FlatBuffersLanguage {

    C_PLUS_PLUS("cpp"),
    JAVA("java"),
    C_SHARP("csharp"),
    GO("go"),
    PYTHON("python"),
    JAVASCRIPT("javascript"),
    PHP("php"),
    GRPC("grpc")

    private static final Map<String, FlatBuffersLanguage> lookup = new HashMap<>()

    static {
        for (FlatBuffersLanguage lang : EnumSet.allOf(FlatBuffersLanguage)) {
            lookup.put(lang.value, lang)
        }
    }

    private String value

    FlatBuffersLanguage(String value) {
        this.value = value
    }

    static FlatBuffersLanguage get(String value) {
        lookup.get(value)
    }

}
