import org.jetbrains.intellij.platform.gradle.IntelliJPlatform
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.couchbase"
version = "1.1.7"
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets["main"].java.srcDirs("src/main/gen")

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://mobile.maven.couchbase.com/maven2/dev/") }
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    implementation(files("lib/couchbase-lite-java-ee-3.1.3-7-release.jar"))
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("org.projectlombok:lombok:1.18.30")
    implementation("com.couchbase.client:java-client:3.6.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("software.amazon.awssdk:dynamodb:2.25.60")
    implementation("software.amazon.awssdk:auth:2.25.60")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.opencsv:opencsv:5.5.2") // OpenCSV
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.10.0")
    implementation("com.obiscr:openai-auth:1.0.1")


    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("com.didalgo:gpt3-tokenizer:0.1.7")
    implementation("com.fifesoft:rsyntaxtextarea:3.3.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.11.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.theokanning.openai-gpt3-java:service:0.14.0")
    implementation("com.vladsch.flexmark:flexmark:0.64.8")
    implementation("com.vladsch.flexmark:flexmark-ext-tables:0.64.8")

    implementation("com.vladsch.flexmark:flexmark-html2md-converter:0.64.8")

    testImplementation("org.testcontainers:couchbase:1.19.7")
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdeaCommunity("2025.1")
        bundledPlugin("com.intellij.java")
        bundledPlugin("com.intellij.modules.json")
        bundledPlugin("org.jetbrains.plugins.terminal")
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
            1.1.7   - fixes for SQL++ BETWEEN clause and comments grammar 
            1.1.6.3 - adds support for 2025.2
            1.1.6.2 — adds support for 2025.1
        """.trimIndent()
        ideaVersion {
            sinceBuild = "251.23774.435"
            untilBuild = "260.*"
        }
        vendor {
            name = "Couchbase"
            url = "https://www.couchbase.com"
            email = "devadvocates@couchbase.com"
        }
    }
    pluginVerification {
        ides{
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2025.1")
        }
    }
}
//intellij {
//    version.set("2025.1")
//    type.set("IC") // Target IDE Platform
//
//    plugins.set(listOf("com.intellij.java", "org.jetbrains.plugins.terminal"))
//    plugins.add("json")
//    plugins.add("intellij.json.backend")
//}

