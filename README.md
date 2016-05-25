# FastChannelPack

A fast channel pack plugin for android build

## Download
--------

```groovy
buildscript {
  repositories {
    mavenCentral()
   }
  dependencies {
    classpath 'com.junli.toolbox:channelpack:0.0.1'
  }
}

apply plugin: 'apply plugin: 'junli.toolbox.channelpack''

```

## Generate Channel apk File
--------
Add the extension channel, add new channel Config List by provide channel name list or channel File


```groovy
channel{
    channelPrefix = "{channelPrefix}"
    channelConfig{
        normal{
            channelList= ['qq','wandoujia']
            channelFile = file('channels.txt')
        }

        normal{
            channelList= ['qq','wandoujia','baidu','googleplay']
        }
    }
}

```

the plugin will create task for each channelConfig and build variants

> ./gradlew sample:packNormalDebug


by run the packXXX task ,you will see the channel apk file is generated under rootProjectDir/build/apks

build/apks/qqDebug.apk
build/apks/wandoujiaDebug.apk


## read the channel attribute in the apk file
--------
you can read the META-INF/{channalPrefix}_{channel} file in the apk file and retrieve the
channel attribute using your own method, here is a general method


```java
public static String getChannelFromApk(Context context) {
    ApplicationInfo appinfo = context.getApplicationInfo();
    String sourceDir = appinfo.sourceDir;

    String key = "META-INF/" + "channelPrefix";
    String ret = "";
    ZipFile zipfile = null;
    try {
        zipfile = new ZipFile(sourceDir);
        Enumeration<?> entries = zipfile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            String entryName = entry.getName();
            if (entryName.startsWith(key)) {
                ret = entryName;
                break;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (zipfile != null) {
            try {
                zipfile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    String[] split = ret.split("_");
    String channel = "";
    if (split != null && split.length >= 2) {
        channel = ret.substring(split[0].length() + 1);
    }
    return channel;
}
```

