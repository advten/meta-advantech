SUMMARY = "Advantech partition expansion service"
DESCRIPTION = "Create the data partition which fills up the entire mmc storage"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=fb13aedbd6fd521d2ba54d78b35fb65b"

DEPENDS += "initscripts advantech-initscripts"

RDEPENDS_${PN} += "bash"

SRC_URI = "file://advantech-partition-expansion.sh \
           file://advantech-partition-expansion.service \
           file://README"
		   
inherit update-alternatives systemd
DEPENDS_append = " update-rc.d-native"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"

FILES_${PN} += "${systemd_unitdir}/system/* ${sysconfdir}/*"

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

	install -m 0644    ${WORKDIR}/advantech-partition-expansion.sh	${D}${sysconfdir}/init.d

#
# Create runlevel links
#
	update-rc.d -r ${D} advantech-partition-expansion.sh start 98 2 3 4 5 .
	
#
# Create systemd services 
#
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${sysconfdir}
		install -d ${D}${sysconfdir}/adv-ota
		
		install -d ${D}${systemd_system_unitdir}
		install -d ${D}${systemd_system_unitdir}/custom.target.wants
		
		install -m 0644 ${WORKDIR}/advantech-partition-expansion.sh ${D}${sysconfdir}/adv-ota
		install -m 0644 ${WORKDIR}/advantech-partition-expansion.service ${D}${systemd_system_unitdir}

		cd ${D}${systemd_system_unitdir}/custom.target.wants/
		ln -sf ../advantech-partition-expansion.service ${D}${systemd_system_unitdir}/custom.target.wants/advantech-partition-expansion.service
	fi
}

