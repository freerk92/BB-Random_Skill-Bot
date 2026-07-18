plugins {
    kotlin("jvm") version "2.2.21"
    application
    id("com.gradleup.shadow") version "8.3.5"
}

group = "frl.freerk.bbrandomskillbot"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:6.4.2") {
        exclude(module = "opus-java")
    }
    implementation("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("frl.freerk.bbrandomskillbot.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set("bb-random-skill-bot")
    archiveClassifier.set("")
    archiveVersion.set("")
}
