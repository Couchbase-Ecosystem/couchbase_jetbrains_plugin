plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.couchbase"
version = "1.1.1"
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
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")


}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.6")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("232.10227.8")
        untilBuild.set("246.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    val patchArgs =
        listOf("--add-exports java.desktop/sun.awt=ALL-UNNAMED --add-opens java.desktop/java.awt=ALL-UNNAMED")

    test {
        jvmArgs(patchArgs)
    }
}


