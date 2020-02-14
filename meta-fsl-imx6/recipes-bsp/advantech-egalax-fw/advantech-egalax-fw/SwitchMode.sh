#!/bin/sh

MODE=0

if [ ! -f /home/touchmode ]; then
        echo 0 > /home/touchmode
fi

if [ -z $1 ]; then
	MODE=`cat /home/touchmode`
else
	MODE=$1
fi

/usr/bin/eGloveSwitch -s $MODE
if [ $? -eq 0 ]; then
	echo "Switch Mode Success" > /dev/kmsg
        echo $MODE > /home/touchmode
else
        echo "Switch Mode Fail ret=$?" > /dev/kmsg
fi
