plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.gradle.plugin-publish")
    `maven-publish`
}

val pluginId: String by project
val pluginImplementationClass: String by project
val pluginDeclarationName: String by project

gradlePlugin {
    plugins {
        create(pluginDeclarationName) {
            id = pluginId
            implementationClass = pluginImplementationClass
        }
    }
}

val projectGroup: String by project
val projectVersion: String by project

group = projectGroup
version = projectVersion

repositories {
    mavenCentral()
}

val junitVersion: String by project
val assertjVersion: String by project
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "11"
    }
}

plugins.withType<JavaPlugin>().configureEach {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }
}

publishing {
    publications {
        repositories {
            mavenLocal()
        }
    }
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}
