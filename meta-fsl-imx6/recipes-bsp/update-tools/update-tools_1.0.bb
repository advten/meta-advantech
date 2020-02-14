DESCRIPTION = "Update-Tools"
SECTION = "utilities"
PR = "r11"
LICENSE = "CLOSED"

RDEPENDS_${PN} = " \
        util-linux \
        python3 \
        python3-misc \
        python3-modules \
        python3-pycrypto \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S="${WORKDIR}"

INITSCRIPT_NAME = "updateserver"
INITSCRIPT_PARAMS = "defaults 10"
inherit update-rc.d

inherit update-alternatives systemd
DEPENDS_append = " update-rc.d-native"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"


SRC_URI = " \
        file://files \
"

do_install() {
    mkdir -p ${D}${sysconfdir}/init.d/
    mkdir -p ${D}/usr/bin

    install -m 0755 ${WORKDIR}/files/SignUpdate.py ${DEPLOY_DIR_IMAGE}
    install -m 0755 ${WORKDIR}/files/SendUpdate.py ${DEPLOY_DIR_IMAGE}
    install -m 0600 ${WORKDIR}/files/public.pem ${D}${sysconfdir}/updateServerPublicKey.pem


    install -m 0755 ${WORKDIR}/files/UpdateServer.py ${D}/usr/bin/UpdateServer.py


    install -m 0755 ${WORKDIR}/files/updateserver ${D}${sysconfdir}/init.d/

		#============================================#
		#=== Create systemd services ================#
		#============================================#
		
		install -d ${D}${systemd_system_unitdir}
		install -d ${D}${systemd_system_unitdir}/custom.target.wants

		install -m 0644 ${WORKDIR}/files/updateserver.service ${D}${systemd_system_unitdir}
		install -m 0644 ${WORKDIR}/files/initenp.service ${D}${systemd_system_unitdir}
		
		cd ${D}${systemd_system_unitdir}/custom.target.wants/
		ln -sf ../updateserver.service ${D}${systemd_system_unitdir}/custom.target.wants/updateserver.service
		ln -sf ../initenp.service ${D}${systemd_system_unitdir}/custom.target.wants/initenp.service


}
FILES_${PN} += "${systemd_unitdir}/system/* ${sysconfdir}/*" 
