DESCRIPTION = "Jungheinrich keys for update-tools"
SECTION = "utilities"
PR = "r2"
LICENSE = "CLOSED"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S="${WORKDIR}"
SRC_URI = "file://files"

do_install() {
    mkdir -p ${D}${sysconfdir}

    # install public key into system
    install -m 0600 ${WORKDIR}/files/public.pem ${D}${sysconfdir}/updateServerPublicKey.pem

    # install private key into deploy folder
    install -m 0600 ${WORKDIR}/files/private.pem ${DEPLOY_DIR_IMAGE}/updateServerPrivateKey.pem
}
