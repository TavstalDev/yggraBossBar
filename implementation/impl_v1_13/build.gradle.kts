val adventureApiVersion = project.property("adventureApiVersion") as String
val adventurePlatformVersion = project.property("adventurePlatformVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-legacy:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-plain:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-text-serializer-gson:${adventureApiVersion}")
    compileOnly("net.kyori:adventure-platform-bukkit:${adventurePlatformVersion}")
    compileOnly(project(":api"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}