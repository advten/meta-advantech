#!/bin/sh

logFile=/home/root/bt_mac.tt
#bt_path=/sys/devices/soc0/soc/2000000.aips-bus/2000000.spba-bus/2008000.ecspi/spi_master/spi0/spi0.0/bt_mac
bt_mac=00:11:22:33:44:55:66

btinit()
{
bt_mac=`cat /sys/devices/soc0/soc/2000000.aips-bus/2000000.spba-bus/2008000.ecspi/spi_master/spi0/spi0.0/bt_mac`
echo ${bt_mac} > ${logFile}
brcm-patchram-plus  --enable_hci --no2bytes --tosleep 200000 -baudrate 115200 --patchram /lib/firmware/bcm/BCM4345C0.hcd --bd_addr ${bt_mac} /dev/ttymxc1
hciattach /dev/ttymxc1 any 115200 flow
}

btinit
