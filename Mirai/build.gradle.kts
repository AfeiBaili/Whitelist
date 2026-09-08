plugins {
    kotlin("jvm") version "1.9.0"
    id("net.mamoe.mirai-console") version "2.16.0"
}

val whitelistVersion = properties["plugin.version"] as String

group = "cn.afeibaili"
version = whitelistVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.6")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}