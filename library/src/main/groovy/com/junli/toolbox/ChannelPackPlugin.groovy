package com.junli.toolbox

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

public class ChannelPackPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        NamedDomainObjectContainer<ChannelConfig> configList = project.container(ChannelConfig);

        ChannelExt ext = new ChannelExt(configList)

        project.extensions.add('channel', ext)

        project.android.applicationVariants.all { var ->

            ChannelExt channelExt = project.extensions.getByName('channel')

            NamedDomainObjectContainer<ChannelConfig> fetchedConfigList =
                    channelExt.getChannelConfigs();

            fetchedConfigList.forEach { ChannelConfig config ->
                def taskName = "pack"+ config.getName().capitalize() + var.name.capitalize()
                Task packTask = project.task(taskName, type: PackTask){
                    channelConfig = config
                    variant = var
                    channelPrefix = channelExt.channelPrefix
                    group = 'pack'
                }

                packTask.dependsOn var.assemble
//                packAllTask.dependsOn packTask
            }


        }
    }

}

