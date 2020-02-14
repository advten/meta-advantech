DESCRIPTION = "Update Tools for Windows"
SECTION = "utilities"
PR = "r4"
LICENSE = "CLOSED"

DEPENDS = "mingw-w32-native"
RDEPENDS_${PN} = "mingw-w32-native"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S="${WORKDIR}"

SRC_URI = " \
    file://files \
"

do_install() {
    install -d ${DEPLOY_DIR_IMAGE}
    install -m 0644 ${WORKDIR}/files/SendUpdate.cpp ${DEPLOY_DIR_IMAGE}
    install -m 0755 ${WORKDIR}/files/generate_SendUpdateExe.sh ${DEPLOY_DIR_IMAGE}
}
