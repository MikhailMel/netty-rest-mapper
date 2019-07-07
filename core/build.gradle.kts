import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.40"
}

group = "ru.scratty"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.netty", "netty-all", "4.1.37.Final")

//    implementation("org.apache.logging.log4j", "log4j-api", "2.12.0")
//    implementation("org.apache.logging.log4j", "log4j-core", "2.12.0")
//    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.12.0")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
