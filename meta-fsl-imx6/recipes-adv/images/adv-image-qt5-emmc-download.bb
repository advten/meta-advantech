DESCRIPTION = "Freescale Image - Adds Qt5"
LICENSE = "MIT"



inherit core-image
inherit distro_features_check

ROOTFS_POSTPROCESS_COMMAND += "install_emmc_download_images;"

CORE_IMAGE_EXTRA_INSTALL += " \
	advantech-emmc-download \
	e2fsprogs \
	dosfstools \
	util-linux \
	parted \
    "

install_emmc_download_images() {
	
	install -d ${IMAGE_ROOTFS}/emmc
	
	install -m 0644 ${DEPLOY_DIR_IMAGE}/u-boot.imx ${IMAGE_ROOTFS}/emmc/u-boot.imx
	install -m 0644 ${DEPLOY_DIR_IMAGE}/zImage ${IMAGE_ROOTFS}/emmc/zImage
	install -m 0644 ${DEPLOY_DIR_IMAGE}/zImage-imx6q-imsse01.dtb ${IMAGE_ROOTFS}/emmc/imx6q-imsse01.dtb
	install -m 0644 ${DEPLOY_DIR_IMAGE}/adv-rootfs-main-imx6q-imsse01.ext4	${IMAGE_ROOTFS}/emmc/adv-rootfs-main-imx6q-imsse01.ext4	
	install -m 0644 ${DEPLOY_DIR_IMAGE}/adv-rootfs-ota-imx6q-imsse01.ext4	${IMAGE_ROOTFS}/emmc/adv-rootfs-ota-imx6q-imsse01.ext4	
}

DISTRO_FEATURES_remove = " x11 wayland bluetooth "
