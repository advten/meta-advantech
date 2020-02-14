SUMMARY = "Advantech"
DESCRIPTION = "lrdmwl provided."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=a433f8cf2ead0f0068b9307dcf60e183"
#DEPENDS = "linux-imx"
SRC_URI = "file://88W8997_sdio_uart_v2.5.8.1.bin \
		file://88W8997_sdio_uart_v2.5.8.3.bin \
		file://88W8997_sdio_uart_v2.5.10.3.bin \
		file://88W8997_sdio_uart_v7.5.6.29.bin \
		file://88W8997_ST_sdio_uart_v8.5.18.50.bin \
		file://lrdmwl_sdio.ko \
		file://lrdmwl.ko \
		file://cfg80211.ko \
		file://mac80211.ko \
		file://compat.ko \
		file://bluetooth.ko \
		file://hci_uart.ko \
		file://lrdmwl.service \
		file://lrdmwl \
		file://wifiregion \
		file://xwifi \
		file://x0wifi \
		file://x1wifi \
		file://x2wifi \
		file://xwifi.sh \
		file://x0wifi.sh \
		file://x1wifi.sh \
		file://x2wifi.sh \
		file://tistest \
		file://tis_wpa.conf \
		file://5gtest \
		file://5g_wpa.conf \
		file://init_lrdmwl.sh \
		file://install_lrdmwl3594.sh \
		file://install_lrdmwl5g.sh \
		file://install_lrdmwl5023.sh \
		file://install_lrdmwl3557.sh \
		file://install_lrdmwl6092.sh \
		file://install_lrdmwl60138.sh \
		file://mfg60n-3.5.5.65.sh \
		file://mfg60n-3.5.5.94.sh \
		file://88W8997_mfg_sdio_uart_v16.205.153.252.bin \
		file://set_mfg.sh \
		file://set_mfg57.sh \
		file://tar_laird3594.sh \
		file://tar_laird5023.sh \
		file://tar_laird5g.sh \
		file://tar_laird3557.sh \
		file://tar_laird6092.sh \
		file://tar_laird60138.sh \
		file://clean_mfg_fw.sh \
		file://fcc_mod.sh \
		file://rmmod_lrdmwl.sh \
		file://laird-backport-3.5.5.94.tar \
		file://laird-backport-3.5.5.57.tar \
		file://laird-backport-5g.tar \
		file://laird-backport-5.0.2.3.tar \
		file://laird-backport-6.0.0.92.tar \
		file://laird-backport-6.0.0.138.tar \
		file://README"
		
S = "${WORKDIR}"
TARGET_CC_ARCH += "${LDFLAGS}"
INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-dev = "ldflags"
INSANE_SKIP_${PN} += "already-stripped rpaths"

do_install () {
echo "TMPDIR:${TMPDIR}"
ln -sf ${TMPDIR}/sysroots-components/x86_64/gcc-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-gcc  ${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-gcc

cp -f ${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-ld ${TMPDIR}/sysroots-components/x86_64/gcc-cross-arm/usr/bin/arm-poky-linux-gnueabi/
cp -f ${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-as ${TMPDIR}/sysroots-components/x86_64/gcc-cross-arm/usr/bin/arm-poky-linux-gnueabi/
cp -f ${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-objdump ${TMPDIR}/sysroots-components/x86_64/gcc-cross-arm/usr/bin/arm-poky-linux-gnueabi/

cd laird-backport-3.5.5.57
echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko
" > build_wifi_runtime.sh

#chmod 777 build_wifi_runtime.sh
#./build_wifi_runtime.sh
cd ..
./tar_laird3557.sh

cd laird-backport-3.5.5.94
echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko
" > build_wifi_runtime.sh

#chmod 777 build_wifi_runtime.sh
#./build_wifi_runtime.sh
cd ..
./tar_laird3594.sh


cd laird-backport-5g
echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko
" > build_wifi_runtime.sh

#chmod 777 build_wifi_runtime.sh
#./build_wifi_runtime.sh
cd ..
./tar_laird5g.sh


cd laird-backport-5.0.2.3
echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko
" > build_wifi_runtime.sh

#chmod 777 build_wifi_runtime.sh
#./build_wifi_runtime.sh
cd ..
./tar_laird5023.sh

cd laird-backport-6.0.0.92
echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko
" > build_wifi_runtime.sh

#chmod 777 build_wifi_runtime.sh
#./build_wifi_runtime.sh
cd ..
./tar_laird6092.sh

cd laird-backport-6.0.0.138

echo "export KLIB_BUILD=${TMPDIR}/work/imx6q_imsse01-poky-linux-gnueabi/linux-imx/4.14.98-r0/build
export ARCH=arm
export CROSS_COMPILE=${TMPDIR}/sysroots-components/x86_64/binutils-cross-arm/usr/bin/arm-poky-linux-gnueabi/arm-poky-linux-gnueabi-
sed -i '/gcc/d' kconf/Makefile
sed -i '/^conf:/a\	gcc -o conf conf.c zconf.tab.c' kconf/Makefile
make clean
make defconfig-sterling60
make  -j9
sleep 1
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl_sdio.ko ../lrdmwl_sdio.ko
cp  ./drivers/net/wireless/laird/lrdmwl/lrdmwl.ko ../lrdmwl.ko
cp  ./net/wireless/cfg80211.ko ../cfg80211.ko
cp  ./net/mac80211/mac80211.ko ../mac80211.ko
cp  ./compat/compat.ko ../compat.ko
cp  ./drivers/bluetooth/hci_uart.ko  ../hci_uart.ko
cp  ./net/bluetooth/bluetooth.ko  ../bluetooth.ko 
" > build_wifi_runtime.sh

chmod 777 build_wifi_runtime.sh
./build_wifi_runtime.sh
cd ..
./tar_laird60138.sh

cp 88W8997_ST_sdio_uart_v8.5.18.50.bin 88W8997_sdio.bin
cp firmware-lrdmwl60138.tar1 firmware-lrdmwl.tar1

	install -d ${D}/${base_libdir}/firmware/lrdmwl
	install -m 0777		${WORKDIR}/88W8997_sdio_uart_v2.5.8.1.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/88W8997_sdio_uart_v2.5.8.3.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/88W8997_sdio_uart_v2.5.10.3.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/88W8997_sdio_uart_v7.5.6.29.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/88W8997_ST_sdio_uart_v8.5.18.50.bin		${D}/${base_libdir}/firmware/lrdmwl/

	install -m 0777		${WORKDIR}/88W8997_sdio.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/lrdmwl_sdio.ko		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/lrdmwl.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/cfg80211.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/mac80211.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/compat.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/bluetooth.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/hci_uart.ko			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/mfg60n-3.5.5.65.sh		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/mfg60n-3.5.5.94.sh		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl3594.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl5g.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl5023.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl6092.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl60138.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/firmware-lrdmwl3557.tar1		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/88W8997_mfg_sdio_uart_v16.205.153.252.bin		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/init_lrdmwl.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl3594.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl5g.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl5023.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl3557.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl6092.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/install_lrdmwl60138.sh			${D}/${base_libdir}/firmware/lrdmwl/

	install -m 0777		${WORKDIR}/set_mfg.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/set_mfg57.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/clean_mfg_fw.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/rmmod_lrdmwl.sh			${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/xwifi.sh		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/x0wifi.sh		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/x1wifi.sh		${D}/${base_libdir}/firmware/lrdmwl/
	install -m 0777		${WORKDIR}/x2wifi.sh		${D}/${base_libdir}/firmware/lrdmwl/
	
	install -d ${D}/${bindir}
	install -m 0777		${WORKDIR}/lrdmwl		${D}/${bindir}
	install -m 0777		${WORKDIR}/tistest		${D}/${bindir}
	install -m 0777		${WORKDIR}/5gtest		${D}/${bindir}
	install -m 0777		${WORKDIR}/xwifi		${D}/${bindir}
	install -m 0777		${WORKDIR}/x0wifi		${D}/${bindir}
	install -m 0777		${WORKDIR}/x1wifi		${D}/${bindir}
	install -m 0777		${WORKDIR}/x2wifi		${D}/${bindir}
	install -m 0777		${WORKDIR}/wifiregion		${D}/${bindir}
	install -m 0777		${WORKDIR}/fcc_mod.sh			${D}/${bindir}/fcc_mod
	
	install -d ${D}/${sysconfdir}
	install -m 0777		${WORKDIR}/tis_wpa.conf		${D}${sysconfdir}/
	install -m 0777		${WORKDIR}/5g_wpa.conf		${D}${sysconfdir}/
	
	install -d ${D}/${systemd_system_unitdir}
	install -m 0644		${WORKDIR}/lrdmwl.service		${D}${systemd_system_unitdir}
	
	install -d ${D}/${systemd_system_unitdir}/multi-user.target.wants
	#install -m 0644 ${WORKDIR}/lrdmwl.service ${D}${systemd_system_unitdir}/multi-user.target.wants/lrdmwl.service
	ln  -sf  ../lrdmwl.service ${D}${systemd_system_unitdir}/multi-user.target.wants/
}

FILES_${PN}="${base_libdir}/firmware/lrdmwl/88W8997_sdio.bin \
			${base_libdir}/firmware/lrdmwl/88W8997_sdio_uart_v2.5.8.1.bin \
			${base_libdir}/firmware/lrdmwl/88W8997_sdio_uart_v2.5.8.3.bin \
			${base_libdir}/firmware/lrdmwl/88W8997_sdio_uart_v2.5.10.3.bin \
			${base_libdir}/firmware/lrdmwl/88W8997_sdio_uart_v7.5.6.29.bin \
			${base_libdir}/firmware/lrdmwl/88W8997_ST_sdio_uart_v8.5.18.50.bin \
			${base_libdir}/firmware/lrdmwl/lrdmwl_sdio.ko \
			${base_libdir}/firmware/lrdmwl/lrdmwl.ko \
			${base_libdir}/firmware/lrdmwl/init_lrdmwl.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl3594.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl5g.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl5023.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl3557.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl6092.sh \
			${base_libdir}/firmware/lrdmwl/install_lrdmwl60138.sh \
			${base_libdir}/firmware/lrdmwl/clean_mfg_fw.sh \
			${base_libdir}/firmware/lrdmwl/set_mfg.sh \
			${base_libdir}/firmware/lrdmwl/set_mfg57.sh \
			${base_libdir}/firmware/lrdmwl/rmmod_lrdmwl.sh \
			${base_libdir}/firmware/lrdmwl/xwifi.sh \
			${base_libdir}/firmware/lrdmwl/x0wifi.sh \
			${base_libdir}/firmware/lrdmwl/x1wifi.sh \
			${base_libdir}/firmware/lrdmwl/x2wifi.sh \
			${base_libdir}/firmware/lrdmwl/cfg80211.ko \
			${base_libdir}/firmware/lrdmwl/mac80211.ko \
			${base_libdir}/firmware/lrdmwl/compat.ko \
			${base_libdir}/firmware/lrdmwl/bluetooth.ko \
			${base_libdir}/firmware/lrdmwl/hci_uart.ko \
			${base_libdir}/firmware/lrdmwl/mfg60n-3.5.5.65.sh \
			${base_libdir}/firmware/lrdmwl/mfg60n-3.5.5.94.sh \
			${base_libdir}/firmware/lrdmwl/88W8997_mfg_sdio_uart_v16.205.153.252.bin \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl3594.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl5g.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl5023.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl6092.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl60138.tar1 \
			${base_libdir}/firmware/lrdmwl/firmware-lrdmwl3557.tar1 \
			${bindir}/lrdmwl \
			${bindir}/wifiregion \
			${bindir}/tistest \
			${bindir}/5gtest \
			${bindir}/xwifi \
			${bindir}/x0wifi \
			${bindir}/x1wifi \
			${bindir}/x2wifi \
			${bindir}/fcc_mod \
			${sysconfdir}/tis_wpa.conf \
			${sysconfdir}/5g_wpa.conf \
			${systemd_system_unitdir}/lrdmwl.service \
			${systemd_system_unitdir}/multi-user.target.wants/lrdmwl.service"
