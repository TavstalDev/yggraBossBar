val adventureApiVersion = project.property("adventureApiVersion") as String

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.3-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")

    compileOnly(project(":api"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}