export ARCH="arm"
make defconfig-sterling60
make  -j9
sleep 1
mkdir out
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko

