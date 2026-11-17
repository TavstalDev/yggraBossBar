plugins {}

allprojects {
    group = "io.github.tavstaldev"
    version = "0.1.1"
}

val jetbrainsAnnotationsVersion = project.property("jetbrainsAnnotationsVersion") as String
val lombokVersion = project.property("lombokVersion") as String

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://jitpack.io")
        maven("https://repo.alessiodp.com/releases")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencies {
        "compileOnly"("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
        "compileOnly"("org.projectlombok:lombok:$lombokVersion")
        "annotationProcessor"("org.projectlombok:lombok:$lombokVersion")
    }
}