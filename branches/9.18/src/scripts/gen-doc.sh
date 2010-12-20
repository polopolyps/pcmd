#!/bin/sh

###
# Generate tool documentation in Confluence markup.
#
# This tool runs the help command for all installed tools in pcmd and reformats the output to Confluence wiki markup for publication. 
# It should be placed together with the pcmd script in your project installation. Pipe output to a file and paste contents into 
# Confluence when finished.
#
# Note that it is not very stable and will stop generating good output if the default output of pcmd changes.
#

PCMD=./pcmd

function gen_tool_doc() 
{
    TOOL=$1
    
    TMPFILE=`mktemp -t gen-doc`
    
    $PCMD help $TOOL 2> $TMPFILE
    
    OUTPUT=`cat $TMPFILE`
    
    HEAD=`cat $TMPFILE | head -n 1`
    DESC=`echo $HEAD | cut -d ':' -f 2`
    
    DEFAULT1="\-\-server"
    DEFAULT2="The server name or the connection URL to use to connect to Polopoly. Defaults to localhost."
    DEFAULT3="\-\-loginuser"
    DEFAULT4="The\ Polopoly\ user\ to\ log\ in.\ Defaults\ to\ \"sysadmin\"."
    DEFAULT5="\-\-loginpassword"
    DEFAULT6="The\ password\ of\ the\ Polopoly\ user\ to\ log\ in.\ If\ not\ specified,\ no\ user\ will\ be\ logged\ in\ (which\ is\ fine\ for\ most\ operations)."
    DEFAULT7="\-\-policycache"
    DEFAULT8="The\ size\ of\ the\ policy\ cache\ of\ the\ client."
    DEFAULT9="\-\-contentcache"
    DEFAULT10="The\ size\ of\ the\ content\ cache\ of\ the\ client."
    DEFAULT11="\-\-stoponexception"
    DEFAULT12="Whether to interrupt the operation when an exception occurs or just ignore it and continue."
    
    echo "h3. {anchor:$TOOL}$TOOL"
    echo $DESC
    echo "{noformat:nopanel=true}"
    cat $TMPFILE | grep -v "$HEAD" | grep -v "$DEFAULT1" | grep -v "$DEFAULT2" | grep -v "$DEFAULT3"  | grep -v "$DEFAULT4" | grep -v "$DEFAULT5" | grep -v "$DEFAULT6" | grep -v "$DEFAULT7" | grep -v "$DEFAULT8" | grep -v "$DEFAULT9" | grep -v "$DEFAULT10" | grep -v "$DEFAULT11" | grep -v "$DEFAULT12" | fold -w 80
    echo "{noformat}\n"
}

CMDS=`mktemp -t gen-doc`

$PCMD 2> $CMDS

echo "{toc}"
echo "{warning:title=Do not edit}This page is generated by the gen-doc.sh script in the pcmd source{warning}"

for i in $( cat $CMDS | cut -f 1 -d ' ' | grep -v Usage | grep -v Use | grep -v Available | sort ); do
    gen_tool_doc $i
done