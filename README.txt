    ____  ________  _______     ________    ___________   ________
   / __ \/ ____/  |/  / __ \   / ____/ /   /  _/ ____/ | / /_  __/
  / /_/ / /   / /|_/ / / / /  / /   / /    / // __/ /  |/ / / /   
 / ____/ /___/ /  / / /_/ /  / /___/ /____/ // /___/ /|  / / /    
/_/    \____/_/  /_/_____/   \____/_____/___/_____/_/ |_/ /_/  

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
$Êexport POLOPOLY_SERVER=192.168.101.135

-- Set the POLOPOLY_USER environment variable to change the polopoly user 
-- (sysadmin is used by default):
$Êexport POLOPOLY_USER=brian

-- Set the POLOPOLY_PASSWORD environment variable to change the polopoly user's password 
-- (sysadmin is used by default):
$Êexport POLOPOLY_PASSWORD=brianspassword


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


