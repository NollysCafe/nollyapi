plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.thenolle.plugin"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") { name = "spigotmc-repo" }
    maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap") { name = "ktor-eap" }
    maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")

    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.ktor:ktor-server-core:3.1.2")
    implementation("io.ktor:ktor-server-netty:3.1.2")
    implementation("io.ktor:ktor-server-cors:3.1.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("io.ktor:ktor-server-websockets:3.1.2")
    implementation("io.ktor:ktor-server-status-pages:3.1.2")
    implementation("io.ktor:ktor-server-call-id:3.1.2")
    implementation("io.ktor:ktor-server-call-logging:3.1.2")
}

kotlin {
    jvmToolchain(17)
}

tasks.dokkaHtml {
    outputDirectory.set(file("$buildDir/dokka/html"))
}

task("deployDocs") {
    dependsOn("dokkaHtml")

    doLast {
        val dokkaDir = file("$buildDir/dokka/html")
        val gitHubPagesBranch = "gh-pages"
        val repoUrl = "git@github.com:nollyscafe/nollyapi.git"

        exec {
            commandLine("git", "checkout", gitHubPagesBranch)
            workingDir = file("$projectDir")
        }
        exec {
            commandLine("git", "add", dokkaDir)
            workingDir = file("$projectDir")
        }
        exec {
            commandLine("git", "commit", "-m", "Update documentation")
            workingDir = file("$projectDir")
        }
        exec {
            commandLine("git", "push", repoUrl, gitHubPagesBranch)
            workingDir = file("$projectDir")
        }
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    isZip64 = true
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Multi-release" to "true"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            group = "com.thenolle.plugin"
            version = "0.0.1"
            artifactId = "nollyapi"

            pom {
                name.set("NollyAPI")
                description.set("An API for your plugin needs.")
                url.set("https://cafe.thenolle.com")
                licenses {
                    license {
                        name.set("NFE-OSL v1.0")
                        url.set("https://cafe.thenolle.com/nfe-osl")
                    }
                }
                developers {
                    developer {
                        id.set("nolly")
                        name.set("Nolly")
                        email.set("nolly.berrebi@gmail.com")
                        organization.set("Nolly's Cafe")
                        organizationUrl.set("https://cafe.thenolle.com")
                    }
                }
                scm {
                    url.set("https://github.com/nollyscafe/nollyapi")
                    connection.set("git@github.com:nollyscafe/nollyapi.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "nollyapi"
            url = uri("https://nexus.thenolle.com/repository/nollyapi/")
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}