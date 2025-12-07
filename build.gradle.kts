import java.time.LocalDate

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(libs.kotlinx.datetime)

    implementation(ktorLibs.client.okhttp)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
}

tasks.register<JavaExec>("updateHoliday") {
    group = "holiday"
    description = "Update holiday"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.github.taetae98coding.holiday.JvmAppKt")

    val startYear = System.getenv("START_YEAR") ?: LocalDate.now().year.toString()
    val endInclusiveYear = System.getenv("END_INCLUSIVE_YEAR") ?: (LocalDate.now().year + 2).toString()
    val serviceKey = requireNotNull(System.getenv("SERVICE_KEY"))

    environment("START_YEAR", startYear)
    environment("END_INCLUSIVE_YEAR", endInclusiveYear)
    environment("SERVICE_KEY", serviceKey)
}
