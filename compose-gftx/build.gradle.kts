import com.vanniktech.maven.publish.SonatypeHost
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = Java.jvmTarget
            }
        }
    }
    androidTarget{
        compilations.all {
            kotlinOptions {
                jvmTarget = Java.jvmTarget
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(libs.androidx.lifecycle.runtime.compose)

            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
            }
        }
    }
}

android {
    namespace = "com.gft.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        compileOptions {
            sourceCompatibility = Java.sourceCompatibility
            targetCompatibility = Java.targetCompatibility
        }
    }
}

compose {
    kotlinCompilerPlugin.set(Compose.kotlinCompilerPlugin)
}

mavenPublishing {
    coordinates(project.property("libraryGroupId") as String, "compose", project.property("libraryVersion") as String)

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
                children().first { it is Node && (it.name() as QName).localPart == name } as Node

            val dependencyVersions = configurations["releaseRuntimeClasspath"].resolvedConfiguration.resolvedArtifacts.associate { it.moduleVersion.id.name to it.moduleVersion.id.version }
            asNode().child("dependencies").children().filterIsInstance<Node>().forEach { dependencyNode ->
                val isVersionMissing = (dependencyNode["version"] as NodeList).isEmpty()
                if (isVersionMissing) {
                    val artifactId = ((dependencyNode["artifactId"] as NodeList).first() as Node).text()
                    val version = dependencyVersions["$artifactId-android"]
                    dependencyNode.appendNode("version", version)
                }
            }
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
