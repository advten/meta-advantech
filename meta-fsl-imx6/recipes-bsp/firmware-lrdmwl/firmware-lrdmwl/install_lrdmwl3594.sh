#!/bin/sh -e

rm -f /lib/firmware/lrdmwl/compat.ko
rm -f /lib/firmware/lrdmwl/cfg80211.ko
rm -f /lib/firmware/lrdmwl/mac80211.ko
rm -f /lib/firmware/lrdmwl/lrdmwl.ko
rm -f /lib/firmware/lrdmwl/lrdmwl_sdio.ko
rm -f /lib/firmware/lrdmwl/bluetooth.ko
rm -f /lib/firmware/lrdmwl/hci_uart.ko
cd /lib/firmware/lrdmwl
tar xvf  firmware-lrdmwl3594.tar1

rm -f /lib/firmware/lrdmwl/88W8997_sdio.bin
cp /lib/firmware/lrdmwl/88W8997_sdio_uart_v2.5.8.1.bin /lib/firmware/lrdmwl/88W8997_sdio.bin
sync