#!/bin/sh

### BEGIN INIT INFO
# Provides:             vpm-keyevent
# Required-Start:
# Required-Stop:
# Default-Start:        2 3 4 5
# Default-Stop:
### END INIT INFO

VPM_INIT_MODE="/sys/devices/soc0/soc/2100000.aips-bus/21a0000.i2c/i2c-0/0-0078/vpmintmode"

if [ -f "$VPM_INIT_MODE" ]; then
	echo 1 > "$VPM_INIT_MODE" 
else
	echo "sysfs entry for vpm keyevent is not exist"
fi
