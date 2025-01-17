package ru.evolinc.gradle.buildtimetracker

import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ReportingBasePlugin
import org.gradle.api.services.BuildServiceSpec
import org.gradle.build.event.BuildEventsListenerRegistry

class BuildTimeTrackerPlugin
@Inject constructor(
    private val registry: BuildEventsListenerRegistry,
) : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply(ReportingBasePlugin::class.java)
        val extension = project.extensions.create(
            "buildTimeTracker", BuildTimeTrackerPluginExtension::class.java, project
        )
        project.gradle.taskGraph.whenReady {
            val clazz = BuildTimeTrackerBuildService::class.java
            val timingRecorder = project.gradle.sharedServices.registerIfAbsent(
                clazz.simpleName, clazz, createConfigureAction(extension, project.gradle)
            )
            registry.onTaskCompletion(timingRecorder)
        }

    }

    private fun createConfigureAction(
        extension: BuildTimeTrackerPluginExtension,
        gradle: Gradle
    ): Action<BuildServiceSpec<BuildTimeTrackerBuildService.Params>> {
        return Action<BuildServiceSpec<BuildTimeTrackerBuildService.Params>> { params ->
            params.parameters.minTaskDuration.set(extension.minTaskDuration)
            params.parameters.startTaskName.set(gradle.startParameter.taskNames.joinToString())
        }
    }
}
