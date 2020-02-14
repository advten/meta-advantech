SUMMARY = "Advantech autobrightness"
DESCRIPTION = "autobrightness provided by advantech for board support package."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=64c3d2c96e6ad9941bde3924ddfd1416"

SRC_URI = "file://light_range.conf \
		file://light_lux200.conf \
		file://light_levels.conf \
		file://light_controlbl.conf \
		file://light_autobl.conf \
		file://auto_brightness_self_test.sh \
		file://auto_brightness_flash_test.sh \
		file://auto_brightness_level_mapping.sh \
		file://auto_brightness.service \
		file://init_auto_brightness \
		file://README"

FILES_${PN} = "${sysconfdir}/*"

do_install () {
#
# install device conf
#
	install -d ${D}${sysconfdir}/
	install -m 0644    ${WORKDIR}/light_range.conf			${D}${sysconfdir}/
	install -m 0644    ${WORKDIR}/light_lux200.conf			${D}${sysconfdir}/
	install -m 0644    ${WORKDIR}/light_levels.conf			${D}${sysconfdir}/
	install -m 0644    ${WORKDIR}/light_controlbl.conf		${D}${sysconfdir}/
	install -m 0644    ${WORKDIR}/light_autobl.conf			${D}${sysconfdir}/

	install -m 755 ${WORKDIR}/auto_brightness_self_test.sh ${D}/${sysconfdir}/
	install -m 755 ${WORKDIR}/auto_brightness_flash_test.sh ${D}/${sysconfdir}/
	install -m 755 ${WORKDIR}/auto_brightness_level_mapping.sh ${D}/${sysconfdir}/
	
	install -d ${D}/${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/auto_brightness.service ${D}${systemd_system_unitdir}
	
	install -d ${D}/${systemd_system_unitdir}/multi-user.target.wants
	install -m 0644 ${WORKDIR}/auto_brightness.service ${D}${systemd_system_unitdir}/multi-user.target.wants/auto_brightness.service
	
	install -d ${D}/${bindir}
	install -m 0777 ${WORKDIR}/init_auto_brightness  ${D}/${bindir}
}


FILES_${PN}="${sysconfdir}/auto_brightness_level_mapping.sh  \
			${sysconfdir}/auto_brightness_flash_test.sh  \
			${sysconfdir}/auto_brightness_self_test.sh  \
			${sysconfdir}/light_range.conf  \
			${sysconfdir}/light_lux200.conf  \
			${sysconfdir}/light_levels.conf  \
			${sysconfdir}/light_controlbl.conf  \
			${sysconfdir}/light_autobl.conf  \
			${bindir}/init_auto_brightness \
			${systemd_system_unitdir}/auto_brightness.service \
			${systemd_system_unitdir}/multi-user.target.wants/auto_brightness.service"