plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "com.couchbase"
version = "1.0.2"


sourceSets["main"].java.srcDirs("src/main/gen")

repositories {
    mavenCentral()


}

dependencies {
    implementation("com.couchbase.client:java-client:3.4.9")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.1.2")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("243.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}