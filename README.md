#PCMD
Polopoly Command Tool (Pcmd) is a collection of utilities for accessing basic Polopoly functionality using the command line. Functionality includes searching for objects, inspecting their properties and modifying or deleting content. Coupled with the standard Linux command-line tools (e.g. grep) this gives powerful possibilities of quickly inspecting or modifying the server state.

##Standalone distributions

| Polopoly version| Distribution        
| ------------- |-------------|                                                   
| 10.10.0       | [pcmd-client-10.10.0-beta.3.2.1.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.10.0/beta.3.2.1/pcmd-client-10.10.0-beta.3.2.1-standalone.zip)            |
| 10.10.1       | [pcmd-client-10.10.1-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.10.1/beta.3.2.1/pcmd-client-10.10.1-beta.3.2.1-standalone.zip)              | 
| 10.10.1-fp1   | [pcmd-client-10.10.1-fp1-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.10.1-fp1/beta.3.2.1/pcmd-client-10.10.1-fp1-beta.3.2.1-standalone.zip)              |
| 10.10.1-fp2   | [pcmd-client-10.10.1-fp2-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.10.1-fp2/beta.3.2.1/pcmd-client-10.10.1-fp2-beta.3.2.1-standalone.zip)   |
| 10.10.1-fp3   | [pcmd-client-10.10.1-fp3-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.10.1-fp3/beta.3.2.1/pcmd-client-10.10.1-fp3-beta.3.2.1-standalone.zip)  |
| 10.12.0       | [pcmd-client-10.12.0-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.12.0/beta.3.2.1/pcmd-client-10.12.0-beta.3.2.1-standalone.zip)               |
| 10.12.0-fp1   | [pcmd-client-10.12.0-fp1-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.12.0-fp1/beta.3.2.1/pcmd-client-10.12.0-fp1-beta.3.2.1-standalone.zip)   |
| 10.14.0       | [pcmd-client-10.14.0-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.14.0/beta.3.2.1/pcmd-client-10.14.0-beta.3.2.1-standalone.zip)               |
| 10.14.0-fp1   | [pcmd-client-10.14.0-fp1-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.14.0-fp1/beta.3.2.1/pcmd-client-10.14.0-fp1-beta.3.2.1-standalone.zip)  |
| 10.16.0   | [pcmd-client-10.16-beta.3.2.1-standalone.zip](http://maven.polopoly.com/nexus/content/repositories/professional-services/com/polopoly/ps/tools/pcmd-client-10.16/beta.3.2.1/pcmd-client-10.16-beta.3.2.1-standalone.zip)  |

##Getting started


1. download the pcmd distribuition ZIP file matching your Polopoly version from nexus.

2. Unpack the zip file in a directory on your local computer.

3. Should you be using a certain fixpack version of Polopoly that does not fully match the Polopoly versions above you will need to replace the polopoly.jar file in the lib-folder of the unpacked zip file with the one used in your system. If you are running Polopoly 10.6.1-fp6 for instance, you need to download the 10.6.1 version of the pcmd distribution and replace the polopoly-10.6.1.jar file with your system's polopoly-10.6.1-fp6 after unpacking the zip. 

4. Add the pcmd binary located in the "bin" directory to your PATH to be able to run pcmd from anywhere in the filesystem. See HOW TO INSTALL PCMD ON THE PATH below.

Now you are ready to go!

##Executing pcmd

Run the pcmd script located in the bin folder of this distribution. 
Note that some of the tools require that polopoly is running.

### List all available tools that can be run
```bash
$ ./pcmd
```
### Run a specific tool
```bash
$ ./pcmd <toolname>  <arguments>  ## Example, ./pcmd inspect 2.0
```

##How to
###Help
If you want to list what arguments or options a tool uses you
can run the tool's help command.

```bash
$ ./pcmd help <toolname>  
```


## Maven archetype pcmd-client-archetype-simple

You can create a maven project that can create a custom distribution for your project. It contains an example of how to create a Tool. 

```bash
mvn archetype:generate                                 \
  -DarchetypeGroupId=com.polopoly.ps.tools             \
  -DarchetypeArtifactId=pcmd-client-archetype-simple   \
  -DarchetypeVersion=beta.3.2.1                         \
  -DgroupId=my.groupid                                 \
  -DartifactId=pcmd-client
```

##Configuration

If you want to connect to a different server or use a different login name or 
use a different password you can set the following environment variables:

-- Set the POLOPOLY_SERVER environment variable to connect to another server:
```bash
$export POLOPOLY_SERVER=192.168.101.135
```
-- Set the POLOPOLY_USER environment variable to change the polopoly user 
-- (sysadmin is used by default):
```bash
$export POLOPOLY_USER=brian
```
-- Set the POLOPOLY_PASSWORD environment variable to change the polopoly user's password 
-- (sysadmin is used by default):
```bash
$export POLOPOLY_PASSWORD=brianspassword
```
_Those options are only available on the pcmd and pcmdDebug_

##Add pcmd on the PATH
On Mac/linux:
If you want to run the tool from other places add the pcmd script 
in your .profile or .bash_rc or similar and export it.
```bash
export PATH=/Users/brian/tools/pcmd/bin/pcmd:$PATH
```
## Debuging pcmd

The script pcmdDebug

If you need to configure  the logging when running pcmd you can do it on $BASEDIR/conf/log4j.properties


```bash
handlers=java.util.logging.ConsoleHandler

log4j.rootLogger=INFO, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yy/MM/dd HH:mm:ss} %p %c{2}:


```
