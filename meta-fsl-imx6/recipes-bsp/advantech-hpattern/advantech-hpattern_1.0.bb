DESCRIPTION = "advantech H pattern test application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
TARGET_CC_ARCH += "${LDFLAGS}"

SRC_URI = "file://advantech_hpattern.c"

S = "${WORKDIR}"
do_compile() {
	${CC} advantech_hpattern.c -o advantech-hpattern
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 advantech-hpattern ${D}${bindir}
}
