package ru.evolinc.gradle.buildtimetracker

import org.gradle.api.Project
import org.gradle.api.provider.Property

open class BuildTimeTrackerPluginExtension(
    project: Project,
) {

    val minTaskDuration: Property<Long> = project.objects.property(Long::class.java)
        .convention(0L)
}
