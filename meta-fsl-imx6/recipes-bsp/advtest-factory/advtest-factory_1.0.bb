DESCRIPTION = "Advantech factory test tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

S = "${WORKDIR}/git"
RDEPENDS_${PN} += "bash"
FILES_${PN} = "/home/root"

SRCBRANCH = "master"
SRCREV = "${AUTOREV}"

INSANE_SKIP_${PN} += "already-stripped"

SRC_URI = "git://172.16.9.237/home/gituser/code/nxp/Yocto_L4.1.15_2.1.0/advtest-factory.git;protocol=ssh;user=gituser;branch=${SRCBRANCH} \
           file://factory.desktop"

do_install() {
	install -d  ${D}/home/root/advtest/factory
	cp -r ${S}/* ${D}/home/root/advtest/factory/
	chmod -R a+x ${D}/home/root/advtest

	install -d ${D}/usr/share/applications
	install -m 0644 ${WORKDIR}/factory.desktop ${D}/usr/share/applications
}

FILES_${PN} += "/usr/share/applications/*"