plugins {
    id("java-library")
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "org.nordiumm"
version = "1.0.2"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        url = uri("https://nordiumm.github.io/EventAPI/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("net.nordiumm:nixon-event-api:1.0.4")

    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    runServer {
        minecraftVersion("26.2")
        jvmArgs("-Xms2G", "-Xmx2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf(
            "version" to project.version,
            "description" to project.description
        )

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}