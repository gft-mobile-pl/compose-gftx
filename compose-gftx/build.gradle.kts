import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.gft.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

mavenPublishing {
    configure(
        AndroidSingleVariantLibrary(
            sourcesJar = true,
            publishJavadocJar = true,
        )
    )

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
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
