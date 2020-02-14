FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRCBRANCH = "imx_v2018.03_4.14.98_2.0.0_ga"
UBOOT_SRC ?= "git://localhost/home/hades/Advantech/Gitserver/uboot-imx.git;protocol=ssh;user=hades"
SRC_URI = "${UBOOT_SRC};branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"

do_compile () {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'ld-is-gold', d)}" ]; then
		sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' ${S}/config.mk
	fi

	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
	then
		echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
		echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
	fi

    if [ -n "${UBOOT_CONFIG}" ]
    then
        unset i j k
        for config in ${UBOOT_MACHINE}; do
            i=$(expr $i + 1);
            for type in ${UBOOT_CONFIG}; do
                j=$(expr $j + 1);
                if [ $j -eq $i ]
                then
                    oe_runmake -C ${S} O=${B}/${config} ${config}
                    oe_runmake -C ${S} O=${B}/${config} ${UBOOT_MAKE_TARGET}
                    for binary in ${UBOOT_BINARIES}; do
                        k=$(expr $k + 1);
                        if [ $k -eq $i ]; then
                            cp ${B}/${config}/${binary} ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
                        fi
                    done
                    unset k
                fi
            done
            unset  j
        done
        unset  i
    else
        oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
        oe_runmake -C ${S} O=${B} ${UBOOT_MAKE_TARGET}
		oe_runmake -C ${S} O=${B}/${config} ${UBOOT_BINARY}
    fi

}
