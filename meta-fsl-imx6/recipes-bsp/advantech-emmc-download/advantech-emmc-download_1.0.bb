SUMMARY = "Advantech emmc download scripts"
DESCRIPTION = "Initscripts provided by advantech for board support package."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=3d14358d5cb9cd537bab2028270080bc"

DEPENDS += "initscripts"
RDEPENDS_${PN} += "bash"

SRC_URI = "file://emmc-download.sh \
		   file://emmc-download.service \
           file://README"
		   
		   
		   
inherit update-alternatives systemd
DEPENDS_append = " update-rc.d-native"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd','systemd-systemctl-native','',d)}"



do_install () {

	install -d ${D}/emmc
	install -d ${D}${sysconfdir}/init.d

	install -m 0775    ${WORKDIR}/emmc-download.sh	${D}${sysconfdir}/init.d
	install -m 0775    ${WORKDIR}/emmc-download.sh	${D}/emmc/emmc-download.sh
	
# Create runlevel links

# Create systemd services 
#
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
	
		#install -d ${D}${sysconfdir}
		
		
		install -d ${D}${systemd_system_unitdir}
		install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
		
		
		install -m 644 ${WORKDIR}/emmc-download.service ${D}/${systemd_system_unitdir}


		cd ${D}${systemd_system_unitdir}/multi-user.target.wants/
		ln -sf ../emmc-download.service ${D}${systemd_system_unitdir}/multi-user.target.wants/emmc-download.service
		
	fi
}

FILES_${PN} += "${systemd_unitdir}/system/* "
FILES_${PN} += "/emmc/* "
