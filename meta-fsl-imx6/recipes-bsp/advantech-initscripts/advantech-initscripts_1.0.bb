SUMMARY = "Advantech init scripts"
DESCRIPTION = "Initscripts provided by advantech for board support package."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=3d14358d5cb9cd537bab2028270080bc"

DEPENDS += "initscripts"
RDEPENDS_advantech-initscripts += "bash"
SRC_URI = "file://vpm-keyevent.sh \
           file://bt-init.sh \
           file://bootcount.sh \
           file://dhcpserv-init.sh \
           file://vpm-keyevent.service \
           file://bootcount.service \
           file://dhcpserv-init.service \
           file://custom.target \
		   file://ota-rootfs-update.sh \
		   file://ota-rootfs-update.service \
           file://README"

inherit update-alternatives systemd
DEPENDS_append = " update-rc.d-native"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"

FILES_${PN} = "${systemd_unitdir}/system/* ${sysconfdir}/* ${base_libdir}/*"

do_install () {
#
# Create directories and install device independent scripts
#
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/rcS.d
	install -d ${D}${sysconfdir}/rc0.d
	install -d ${D}${sysconfdir}/rc1.d
	install -d ${D}${sysconfdir}/rc2.d
	install -d ${D}${sysconfdir}/rc3.d
	install -d ${D}${sysconfdir}/rc4.d
	install -d ${D}${sysconfdir}/rc5.d
	install -d ${D}${sysconfdir}/rc6.d

	install -m 0644    ${WORKDIR}/vpm-keyevent.sh	${D}${sysconfdir}/init.d
	install -m 0644    ${WORKDIR}/bt-init.sh   ${D}${sysconfdir}/init.d
	install -m 0644    ${WORKDIR}/dhcpserv-init.sh ${D}${sysconfdir}/init.d
	
	install -d ${D}/${base_libdir}
	install -m 0777    ${WORKDIR}/bootcount.sh     ${D}${base_libdir}
	install -m 0777    ${WORKDIR}/bt-init.sh       ${D}${base_libdir}
	install -m 0777    ${WORKDIR}/dhcpserv-init.sh ${D}${base_libdir}
	install -m 0777    ${WORKDIR}/ota-rootfs-update.sh     ${D}${base_libdir}

#
# Create runlevel links
#
	update-rc.d -r ${D} vpm-keyevent.sh start 99 2 3 4 5 .
	update-rc.d -r ${D} bt-init.sh start 99 2 3 4 5 .
	update-rc.d -r ${D} dhcpserv-init.sh start 99 2 3 4 5 .

#
# Create systemd services 
#
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${systemd_system_unitdir}
		install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
		install -d ${D}${systemd_system_unitdir}/custom.target.wants

		install -m 644 ${WORKDIR}/custom.target ${D}/${systemd_system_unitdir}
		install -m 644 ${WORKDIR}/vpm-keyevent.service ${D}/${systemd_system_unitdir}
		install -m 644 ${WORKDIR}/bootcount.service ${D}/${systemd_system_unitdir}
		install -m 644 ${WORKDIR}/dhcpserv-init.service ${D}/${systemd_system_unitdir}
		install -m 644 ${WORKDIR}/ota-rootfs-update.service ${D}/${systemd_system_unitdir}

		cd ${D}${systemd_system_unitdir}/multi-user.target.wants/
		ln -sf ../custom.target ${D}${systemd_system_unitdir}/multi-user.target.wants/custom.target

		cd ${D}${systemd_system_unitdir}/custom.target.wants/
		ln -sf ../vpm-keyevent.service ${D}${systemd_system_unitdir}/custom.target.wants/vpm-keyevent.service
		ln -sf ../bootcount.service ${D}${systemd_system_unitdir}/custom.target.wants/bootcount.service
		ln -sf ../dhcpserv-init.service ${D}${systemd_system_unitdir}/custom.target.wants/dhcpserv-init.service
		#ln -sf ../ota-rootfs-update.service ${D}${systemd_system_unitdir}/custom.target.wants/ota-rootfs-update.service
	fi
}

