#!/bin/sh -e
cd /lib/firmware/lrdmwl

./mfg60n-3.5.5.94.sh install
sleep 1
ln -sf /lib/ld-2.23.so /lib/ld-linux.so.3 
ln -sf /lib/ld-2.23.so /lib/ld-linux.so
ln -sf 88W8997_mfg_sdio_uart_v16.205.153.252.bin 88W8997_sdio_mfg.bin
ln -sf /usr/lib/libedit.lrd.so.0.0.53  /usr/lib/libedit.so.0
ln -sf /lib/libncursesw.so.5 /usr/lib/libncurses.so.5
sync