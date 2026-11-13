plugins {
    id("com.gradleup.shadow") version "9.3.1"
}

version = "1.0.0"

val adventureApiVersion = project.property("adventureApiVersion") as String
val adventurePlatformVersion = project.property("adventurePlatformVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-platform-bukkit:${adventurePlatformVersion}")
    implementation("net.byteflux:libby-bukkit:1.3.2")
    implementation(project(path = ":bootstrap", configuration = "shadow")) {
        isTransitive = false
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

configurations.runtimeClasspath {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
}

// Disable the default JAR task
tasks.jar {
    enabled = false
}

tasks.compileJava {
    dependsOn(":bootstrap:shadowJar")
}

// Configure the Shadow JAR task
tasks.shadowJar {
    dependsOn(":bootstrap:shadowJar")
    archiveBaseName.set("yggraBossBar-test")
    archiveClassifier.set("") // Set the classifier for the JAR
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot" // Add custom manifest attributes
    }

    exclude("org/slf4j/**")

    val s = "io.github.tavstaldev.yggraBossBar.test.shadow"
    relocate("net.byteflux", "$s.libby")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}