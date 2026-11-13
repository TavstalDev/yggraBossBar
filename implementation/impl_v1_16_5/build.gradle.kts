val adventureApiVersion = project.property("adventureApiVersion") as String

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:${adventureApiVersion}")
    compileOnly(project(":api"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}