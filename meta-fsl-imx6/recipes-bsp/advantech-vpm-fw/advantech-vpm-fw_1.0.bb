SUMMARY = "Advantech"
DESCRIPTION = "VPM firmware upgrade"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://FWUPGRADE_SDK_LINUX_ARM.1.0.8.0.tar.gz \
           file://adv-ims-se01-vpm-v0.091.ebf"


S = "${WORKDIR}"
INSANE_SKIP_${PN} += "already-stripped"

do_install () {
	install -d ${D}${exec_prefix}/sdk
	install -d ${D}${exec_prefix}/sdk/bin
	install -d ${D}${exec_prefix}/sdk/headers
	install -d ${D}${exec_prefix}/sdk/lib
	
	install -m 0755 ${WORKDIR}/sdk/bin/ebf-up                 ${D}${exec_prefix}/sdk/bin/
	install -m 0755 ${WORKDIR}/sdk/headers/firmware_update.h  ${D}${exec_prefix}/sdk/headers/
	install -m 0755 ${WORKDIR}/sdk/lib/libfwupgrade.so        ${D}${exec_prefix}/sdk/lib/
	
	install -m 0755 ${WORKDIR}/adv-ims-se01-vpm-v0.091.ebf    ${D}${exec_prefix}/sdk/bin/
}

FILES_${PN} = "/usr/sdk/*"

