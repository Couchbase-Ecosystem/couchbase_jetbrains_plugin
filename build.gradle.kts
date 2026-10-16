import org.jetbrains.intellij.platform.gradle.IntelliJPlatform
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.couchbase"
version = "1.1.11"
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

sourceSets["main"].java.srcDirs("src/main/gen")

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://mobile.maven.couchbase.com/maven2/dev/")
        content { includeGroupByRegex("com\\.couchbase.*") }
    }
    intellijPlatform {
        defaultRepositories()
    }
}

configurations.configureEach {
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    exclude(group = "org.jsoup", module = "jsoup")          // let IDE's jsoup win
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.48")
    implementation(files("lib/couchbase-lite-java-ee-3.1.3-7-release.jar"))
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    compileOnly("org.projectlombok:lombok:1.18.48")
    implementation("com.couchbase.client:java-client:3.13.0")
    implementation("org.slf4j:slf4j-simple:2.0.20")
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.8.0.202609011348-r")
    implementation("com.google.code.gson:gson:2.14.0")

    implementation("software.amazon.awssdk:dynamodb:2.56.0")
    implementation("software.amazon.awssdk:auth:2.56.0")
    implementation("org.mongodb:mongodb-driver-sync:5.13.0")

    implementation("com.opencsv:opencsv:5.12.0") // OpenCSV
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("org.mockito:mockito-core:5.24.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.4")

    testImplementation("org.testcontainers:couchbase:1.21.4")
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdeaCommunity("2025.2")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.modules.json")
        testFramework(TestFrameworkType.Platform)
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellijPlatform {
    pluginConfiguration {
        id = "com.couchbase.couchbase-intellij-plugin"
        name = "Couchbase"
        version = "${project.version}"
        description = """
            Couchbase is an award-winning distributed NoSQL cloud database that delivers unmatched versatility, performance, scalability, and financial value for all of your cloud, mobile, on-premises, hybrid, distributed cloud, and edge computing applications.<br>
                        The plugin provides integrated support for Couchbase within the IntelliJ IDEA, making it easier to interact with your Couchbase databases directly from your development environment.<br>
                        <ul>
                          <li><strong>Connection Management:</strong> Easily connect to your local or remote Couchbase clusters and manage your connections within the IntelliJ IDEA.</li>
                          <li><strong>Data Manipulation:</strong> View, add, update, and delete documents in your Couchbase buckets directly from IntelliJ.</li>
                          <li><strong>SQL++ Support:</strong> Write and execute SQL++ queries from within IntelliJ. The plugin includes syntax highlighting, error checking, and auto-completion features for SQL++ (previously known as N1QL), making it easier to write and debug your queries.</li>
                        </ul>
                        For more information visit the
                        <a href="https://github.com/couchbaselabs/couchbase_jetbrains_plugin">project repo</a>.
        """.trimIndent()
        changeNotes = """
            1.1.11  - removes the Capella iQ chat tab and its Capella login; dependency upgrades (Couchbase Java SDK 3.13), removal of deprecated APIs, compatibility with IntelliJ 2025.2 (252) up to 2027.3 (273.*); dropped support for 2025.1
            1.1.10  - compatibility with intellij 262.*
            1.1.9   - compatibility with intellij 261.*
            1.1.8   - fixes issue connecting to Capella clusters via private link endpoint
            1.1.7   - fixes for SQL++ BETWEEN clause and comments grammar 
            1.1.6.3 - adds support for 2025.2
            1.1.6.2 — adds support for 2025.1
        """.trimIndent()
        ideaVersion {
            sinceBuild = "252"
            untilBuild = "273.*"
        }
        vendor {
            name = "Couchbase"
            url = "https://www.couchbase.com"
            email = "devadvocates@couchbase.com"
        }
    }
    pluginVerification {
        ides{
            create(IntelliJPlatformType.IntellijIdeaCommunity, "2025.2")
            create(IntelliJPlatformType.IntellijIdea, "2026.2")
        }
    }
}

tasks.wrapper {
    gradleVersion = "9.8.1"
}
//intellij {
//    version.set("2025.1")
//    type.set("IC") // Target IDE Platform
//
//    plugins.set(listOf("com.intellij.java", "org.jetbrains.plugins.terminal"))
//    plugins.add("json")
//    plugins.add("intellij.json.backend")
//}
