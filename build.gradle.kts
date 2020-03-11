import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm") version "1.3.70"
    id("me.champeau.gradle.jmh") version "0.5.0"
}

repositories {
    jcenter()
}

jmh {
    jmhVersion = "1.21"
    failOnError = true
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.openjdk.jmh:jmh-core:1.21")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets["jmh"].java.srcDirs("src")

val jmhJar by tasks.getting(Jar::class) {
    archiveBaseName.set("benchmarks")
    archiveClassifier.set("")
    archiveVersion.set("")
    destinationDirectory.set(rootDir)
}