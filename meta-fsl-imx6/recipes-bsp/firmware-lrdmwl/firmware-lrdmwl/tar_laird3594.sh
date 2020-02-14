#!/bin/sh -e
rm -f firmware-lrdmwl3594.tar1

md5sum lrdmwl_sdio.ko | awk '{print $1}' > lrdmwl_sdio.md5
md5sum lrdmwl.ko | awk '{print $1}' > lrdmwl.md5
md5sum cfg80211.ko | awk '{print $1}' > cfg80211.md5
md5sum mac80211.ko | awk '{print $1}' > mac80211.md5
md5sum compat.ko | awk '{print $1}' > compat.md5
md5sum bluetooth.ko | awk '{print $1}' > bluetooth.md5
md5sum hci_uart.ko | awk '{print $1}' > hci_uart.md5


tar cvf firmware-lrdmwl3594.tar1  lrdmwl_sdio.ko lrdmwl.ko cfg80211.ko mac80211.ko compat.ko bluetooth.ko hci_uart.ko lrdmwl_sdio.md5 lrdmwl.md5 cfg80211.md5 mac80211.md5 compat.md5 bluetooth.md5 hci_uart.md5
