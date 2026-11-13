plugins {
    id("com.gradleup.shadow") version "9.6.1"
}

val adventureApiVersion = project.property("adventureApiVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.3-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")

    implementation(project(":api"))
    runtimeOnly(project(":impl_v1_8_8"))
    runtimeOnly(project(":impl_v1_9_4"))
    runtimeOnly(project(":impl_v1_13"))
    runtimeOnly(project(":impl_v1_16_5"))
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

// Configure the Shadow JAR task
tasks.shadowJar {
    archiveBaseName.set("yggraBossBar")
    archiveClassifier.set("") // Set the classifier for the JAR
    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot" // Add custom manifest attributes
    }

    exclude("org/slf4j/**")
    exclude("org/yaml/**")
}

// Ensure the Shadow JAR task runs during the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}