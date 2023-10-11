/*
 * Copyright 2022-2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.workflow.testing

import java.io.InputStream

open class Loadable {
  fun resolveName(name: String): String? {
    var name: String? = name
    if (name == null) {
      return name
    }
    if (!name.startsWith("/")) {
      var c: Class<*> = javaClass
      while (c.isArray) {
        c = c.componentType
      }
      val baseName = c.name
      val index = baseName.lastIndexOf('.')
      if (index != -1) {
        name = (baseName.substring(0, index).replace('.', '/') + "/" + name)
      }
    } else {
      name = name.substring(1)
    }
    return name
  }

  fun listFiles(assetName: String): List<String> {
    val name = resolveName(assetName)!!

    val list = javaClass.classLoader?.getResource(name) ?: ClassLoader.getSystemResource(name)

    val retList = mutableListOf<String>()
    if (list != null) {
      // works on desktop
      retList.addAll(load(list.openStream()).split("\n"))
    } else {
      // works on mobile

      // Little hack because android does not allow looping through Resources.
      // TODO: Turn this into a task that generates .contents.txt automatically: ls > contents.txt
      val list =
        javaClass.classLoader?.getResource(name + "/contents.txt")
          ?: ClassLoader.getSystemResource(name + "/contents.txt")
      retList.addAll(load(list.openStream()).split("\n"))
    }

    return retList
  }

  fun open(assetName: String): InputStream {
    return try {
      javaClass.getResourceAsStream(assetName)!!
    } catch (e: Exception) {
      println("Can't find $assetName")
      throw e
    }
  }

  fun load(asset: InputStream): String {
    return asset.bufferedReader().use { bufferReader -> bufferReader.readText() }
  }

  fun load(assetName: String): String {
    return load(open(assetName))
  }
}
