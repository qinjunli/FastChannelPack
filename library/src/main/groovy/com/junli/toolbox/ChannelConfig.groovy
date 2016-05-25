package com.junli.toolbox


public class ChannelConfig{

    String name;

    public ChannelConfig(String name){
        this.name = name;
    }

    File channelFile;

    ArrayList<String> channelList;

    public ArrayList<String> getConfiguredChannelList()
    {
        ArrayList<String> result = new ArrayList<>();

        //add channelList
        if(channelList != null){
            result.addAll(channelList)
        }

        //read channelFile and append to result
        if(channelFile != null){
            channelFile.eachLine {line ->
                def channelName = line.split('#')[0]
                result << channelName
            }
        }

        return result;
    }

    @Override
    String toString() {
        return "channel config name: $name, list: $channelList }"
    }
}