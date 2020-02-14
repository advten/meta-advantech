#!/bin/sh -e

echo "setting device ..."

/lib/firmware/lrdmwl/set_mfg.sh > /dev/null

/lib/firmware/lrdmwl/clean_mfg_fw.sh > /dev/null

lmu -reg FCC > /dev/null &

sleep 1

echo "FCC finish,please reboot device"