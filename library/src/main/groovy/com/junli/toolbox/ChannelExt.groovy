
package com.junli.toolbox

import org.gradle.api.NamedDomainObjectContainer

class ChannelExt{

    String channelPrefix
    NamedDomainObjectContainer<ChannelConfig> channelConfigs;

    public ChannelExt(NamedDomainObjectContainer<ChannelConfig> channelConfigs){
        this.channelConfigs = channelConfigs;
    }

    def channelConfig(Closure closure){
        channelConfigs.configure(closure);
    }

    @Override
    String toString() {
        return "config list: $channelConfigs"
    }
}