import java.net.URL
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke

fun Project.configureDokka(artifactId: String, version: String) {
  apply(plugin = Plugins.BuildPlugins.dokka)

  configure<org.jetbrains.dokka.gradle.DokkaTask> {
    outputDirectory.set(file("../docs/$artifactId"))
    suppressInheritedMembers.set(true)
    dokkaSourceSets.invoke {
      register("main") {
        moduleName.set(artifactId)
        moduleVersion.set(version)
        noAndroidSdkLink.set(false)
        externalDocumentationLink {
          url.set(URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/"))
          packageListUrl.set(
            URL("https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-structures-r4/element-list")
          )
        }
      }
    }
  }
}