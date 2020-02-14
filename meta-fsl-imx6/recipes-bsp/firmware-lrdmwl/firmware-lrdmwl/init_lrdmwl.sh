#!/bin/sh -e

 

echo "WIFI driver"
insmod /lib/firmware/lrdmwl/compat.ko
echo "insmod compat"
insmod /lib/firmware/lrdmwl/cfg80211.ko
echo "insmod cfg80211"
insmod /lib/firmware/lrdmwl/mac80211.ko
echo "insmod mac80211"
insmod /lib/firmware/lrdmwl/lrdmwl.ko
echo "insmod lrdmwl"
insmod /lib/firmware/lrdmwl/lrdmwl_sdio.ko
echo "insmod lrdmwl_sdio"
# ifconfig wlan0 up

echo "BT driver"
insmod /lib/firmware/lrdmwl/bluetooth.ko
echo "insmod bluetooth"
insmod /lib/firmware/lrdmwl/hci_uart.ko
echo "insmod hci_uart"
# hciattach /dev/ttymxc1 any -s 3000000 3000000 flow dtron