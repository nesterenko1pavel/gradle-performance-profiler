package ru.evolinc.gradle.buildtimetracker

import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicReference
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFinishEvent

abstract class BuildTimeTrackerBuildService : BuildService<BuildTimeTrackerBuildService.Params>,
    OperationCompletionListener,
    AutoCloseable {

    interface Params : BuildServiceParameters {
        val minTaskDuration: Property<Long>
        val startTaskName: Property<String>
    }

    private val client by lazy(LazyThreadSafetyMode.NONE) { BuildTimeTrackerHttpClient() }

    private val taskDurations: MutableCollection<Pair<String, Long>> = ConcurrentLinkedQueue()
    private val buildStart = AtomicReference(Instant.now().plus(1, ChronoUnit.DAYS))

    override fun onFinish(event: FinishEvent) {
        if (event is TaskFinishEvent) {
            val eventStartTime = event.result.startTime
            val eventEndTime = event.result.endTime

            val eventStart = Instant.ofEpochMilli(eventStartTime)
            buildStart.accumulateAndGet(eventStart, ::minOf)

            val mills = eventEndTime - eventStartTime
            if (mills >= parameters.minTaskDuration.get()) {
                taskDurations.add(event.descriptor.taskPath to mills)
            }

            println("> $mills ${event.descriptor}")
        }
    }

    override fun close() {
        if (taskDurations.isEmpty()) return
        val buildTimeMills = Duration.between(buildStart.get(), Instant.now()).toMillis()
        client.sendBuildInfo(buildTimeMills, taskDurations, parameters.startTaskName.get())
    }
}
