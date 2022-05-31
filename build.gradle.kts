plugins {
    kotlin("jvm") version "1.6.10"
    `java-library`
    `maven-publish`
    id("signing")
    id("org.jetbrains.dokka") version "1.6.10"
}

val artifact = "prunes"
group = "monster.nor.$artifact"
version = "1.0-SNAPSHOT"

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    testImplementation("org.codehaus.groovy:groovy:3.0.8")
    testImplementation("ch.qos.logback:logback-core:1.2.6")
    testImplementation("ch.qos.logback:logback-classic:1.2.6")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.17")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.17")

    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("com.google.truth.extensions:truth-java8-extension:1.1.3")
}

