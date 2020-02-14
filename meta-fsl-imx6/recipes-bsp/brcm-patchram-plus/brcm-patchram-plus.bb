DESCRIPTION = "Broadcom Bluetooth patch utility (based on Intel Edison tree)"
SECTION = "connectivity"

SRC_URI = "file://brcm-patchram-plus.c \
		   file://README \
		  "

LICENSE="MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/README;md5=005b42888f9b23baed15016743b0012f"

do_compile() {
        ${CC} ${CFLAGS} ${LDFLAGS} -o ${WORKDIR}/${P}/brcm-patchram-plus ${WORKDIR}/brcm-patchram-plus.c
}

do_install() {
	install -v -d  ${D}/usr/sbin/
	install -d ${D}${sbindir}
	install -m 0755 ${WORKDIR}/${P}/brcm-patchram-plus ${D}${sbindir}/brcm-patchram-plus
}

FILES_${PN} = "/usr/sbin/ \
			   /usr/sbin/brcm-patchram-plus \
			  "
