package ru.evolinc.gradle.buildtimetracker

import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

class BuildTimeTrackerHttpClient(
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .build(),
) {
    fun sendBuildInfo(
        buildTimeMills: Long,
        taskDurations: MutableCollection<Pair<String, Long>>,
        startTaskName: String,
    ) {
        println("buildTimeMills: $buildTimeMills")
        println("taskDurations: $taskDurations")
        println("startTaskName: $startTaskName")

        val operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        println("arch: ${operatingSystemMXBean.arch}")
        println("operationSystem: ${operatingSystemMXBean.name}")
        println("version: ${operatingSystemMXBean.version}")
        println("systemLoadAverage: ${operatingSystemMXBean.systemLoadAverage}")
    }
}