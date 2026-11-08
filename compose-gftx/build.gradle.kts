import com.vanniktech.maven.publish.SonatypeHost
import groovy.util.Node
import groovy.util.NodeList

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    android {
        namespace = "com.gft.compose"
        compileSdk = 37
        minSdk = 26
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.jetbrains.compose.ui)
            implementation(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
            implementation(libs.androidx.navigationevent.compose)
        }
    }
}

mavenPublishing {
    coordinates(
        project.property("libraryGroupId") as String,
        "compose",
        project.property("libraryVersion") as String
    )

    pom {
        name.set(project.property("libraryName") as String)
        description.set(project.property("libraryDescription") as String)
        inceptionYear.set(project.property("libraryInceptionYear") as String)
        url.set("https://${project.property("libraryRepositoryUrl") as String}")
        licenses {
            license {
                name.set(project.property("libraryLicenseName") as String)
                url.set(project.property("libraryLicenseUrl") as String)
                distribution.set(project.property("libraryLicenseDistribution") as String)
            }
        }
        developers {
            developer {
                name.set(project.property("libraryDeveloperName") as String)
            }
        }
        scm {
            url.set("https://${project.property("libraryRepositoryUrl") as String}")
            connection.set("scm:git:git://${project.property("libraryRepositoryUrl") as String}")
            developerConnection.set("scm:git:ssh://git@${project.property("libraryRepositoryUrl") as String}.git")
        }

        withXml {
            fun Node.child(name: String) =
                children().first {
                    it is Node && it.name().toString().substringAfterLast('}') == name
                } as Node

            val dependencyVersions =
                configurations["androidRuntimeClasspath"].resolvedConfiguration.resolvedArtifacts.associate { it.moduleVersion.id.name to it.moduleVersion.id.version }
            asNode().child("dependencies").children().filterIsInstance<Node>()
                .forEach { dependencyNode ->
                    val isVersionMissing = (dependencyNode["version"] as NodeList).isEmpty()
                    if (isVersionMissing) {
                        val artifactId =
                            ((dependencyNode["artifactId"] as NodeList).first() as Node).text()
                        val version = dependencyVersions["$artifactId-android"]
                        dependencyNode.appendNode("version", version)
                    }
                }
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
