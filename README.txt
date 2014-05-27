    ____  ________  _______  
   / __ \/ ____/  |/  / __ \ 
  / /_/ / /   / /|_/ / / / / 
 / ____/ /___/ /  / / /_/ /  
/_/    \____/_/  /_/_____/   

HOW TO INSTALL PCMD 
To run pcmd on your local polopoly installation you do NOT need to download the pcmd source! PCMD produces assembly artifacts when built and these are deployed to the maven.polopoly.com nexus repository. 

So to use PCMD on your local installation you should 

1. download the pcmd assembly ZIP file matching your Polopoly version from nexus.

For Polopoly 10.8.1-fp1:
The full link is: http://maven.polopoly.com/nexus/service/local/repositories/professional-services/content/com/polopoly/ps/tools/pcmd/2.4/pcmd-2.4-distribution-10.10.0.zip

For Polopoly 10.8.1-fp1:
The full link is: http://maven.polopoly.com/nexus/service/local/repositories/professional-services/content/com/polopoly/ps/tools/pcmd/2.3/pcmd-2.3-distribution-10.8.1-fp1.zip

For Polopoly 10.6.1:
The full link is: http://maven.polopoly.com/nexus/service/local/repositories/professional-services/content/com/polopoly/ps/tools/pcmd/2.3/pcmd-2.3-distribution-10.6.1-ab18661.zip

For Polopoly 10.6.0:
The full link is: http://maven.polopoly.com/nexus/service/local/repositories/professional-services/content/com/polopoly/ps/tools/pcmd/2.3/pcmd-2.3-distribution-10.6.0-bc91117.zip

2. Unpack the zip file in a directory on your local computer.

3. Should you be using a certain fixpack version of Polopoly that does not fully match the Polopoly versions above you will need to replace the polopoly.jar file in the lib-folder of the unpacked zip file with the one used in your system. If you are running Polopoly 10.6.1-fp6 for instance, you need to download the 10.6.1 version of the pcmd distribution and replace the polopoly-10.6.1.jar file with your system's polopoly-10.6.1-fp6 after unpacking the zip. 

4. Add the pcmd binary located in the "bin" directory to your PATH to be able to run pcmd from anywhere in the filesystem. See HOW TO INSTALL PCMD ON THE PATH below.

Now you are ready to go!

HOW TO RUN PCMD CLIENT
Run the pcmd script located in the bin folder of this distribution. 
Note that some of the tools require that polopoly is running.

-- List all available tools that can be run
$ ./pcmd

-- Run a specific tool
$ ./pcmd <toolname>   ## Example, ./pcmd inspect 2.0


HOW TO GET HELP FROM A TOOL
If you want to list what arguments or options a tool uses you
can run the tool's help command.
$ ./pcmd help <toolname>  ## Example, ./pcmd help inspect


HOW TO CONFIGURE PCMD CLIENT
If you want to connect to a different server or use a different login name or 
use a different password you can set the following environment variables:

-- Set the POLOPOLY_SERVER environment variable to connect to another server:
$export POLOPOLY_SERVER=192.168.101.135

-- Set the POLOPOLY_USER environment variable to change the polopoly user 
-- (sysadmin is used by default):
$export POLOPOLY_USER=brian

-- Set the POLOPOLY_PASSWORD environment variable to change the polopoly user's password 
-- (sysadmin is used by default):
$export POLOPOLY_PASSWORD=brianspassword


HOW TO ADD CUSTOM TOOL
Just put the jar of your tool in the folder custom-lib and it 
should show up in the list of available tools in pcmd when you run it.


HOW TO INSTALL PCMD ON THE PATH
On Mac/linux:
If you want to run the tool from other places add the pcmd script 
in your .profile or .bash_rc or similar and export it.

export PATH=/Users/brian/tools/pcmd-client-10.2.0-fp3-r59278-2/bin/pcmd:$PATH


HOW TO TROUBLESHOOT THE INSTALLATION
The verbose argument prints current classpath and the configuration for pcmd, 
i.e. server, login name and login password.

$ ./pcmd --verbose

If you need to increase the logging when running pcmd you can use the JAVA_OPTS and provide a
custom logging.properties file for java.util.logging.
$ export JAVA_OPTS=-Djava.util.logging.config.file=/Users/brian/tools/pcmd-logging.properties

Example logging.properties file:
-----------------------------------------------------------------------------
handlers=java.util.logging.ConsoleHandler

.level=INFO

java.util.logging.ConsoleHandler.level = INFO
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
------------------------------------------------------------------------------


