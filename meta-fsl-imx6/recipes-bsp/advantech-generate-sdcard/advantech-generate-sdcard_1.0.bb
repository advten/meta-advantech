DESCRIPTION = "advantech generate sdcard image process"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "parted-native mtools-native dosfstools-native"
SRC_URI = "file://advantech-generate-sdcard.sh"

S = "${WORKDIR}"

do_install() {
	install -d 0644 ${DEPLOY_DIR_IMAGE}/SDCARD
	install -m 0644 ${WORKDIR}/advantech-generate-sdcard.sh ${DEPLOY_DIR_IMAGE}/SDCARD/advantech-generate-sdcard.sh

	cd ${DEPLOY_DIR_IMAGE}/SDCARD
	echo 1-${MACHINE} 2-${WORKDIR} 3-${DEPLOY_DIR_IMAGE} 4-${KERNEL_DEVICETREE} 5-${KERNEL_IMAGETYPE}
	/bin/bash ${DEPLOY_DIR_IMAGE}/SDCARD/advantech-generate-sdcard.sh ${MACHINE} ${WORKDIR} ${DEPLOY_DIR_IMAGE} ${KERNEL_DEVICETREE} ${KERNEL_IMAGETYPE}
}

