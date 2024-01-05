plugins {
    application
}

application.mainClass = "de.cofinpro.jsondb.Main"

group = "de.cofinpro"
version = "0.8-SNAPSHOT"

java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
}

dependencies {
    val log4jVersion = "3.0.0-beta1"
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jcommander:jcommander:1.83")

    implementation("redis.clients:jedis:5.2.0-alpha2")

    val lombokVersion = "1.18.30"
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")

}

tasks.named<Test>("test") {
    useJUnitPlatform()
}