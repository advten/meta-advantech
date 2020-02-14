#!/bin/sh -e

echo "BT driver"
rmmod hci_uart
echo "rmmod hci_uart"
rmmod bluetooth
echo "rmmod bluetooth"

echo "WIFI driver"
rmmod lrdmwl_sdio
echo "rmmod lrdmwl_sdio"
rmmod lrdmwl
echo "rmmod lrdmwl"
rmmod mac80211
echo "rmmod mac80211"
rmmod cfg80211
echo "rmmod cfg80211"
rmmod compat
echo "rmmod compat"

