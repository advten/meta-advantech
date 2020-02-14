DESCRIPTION = "Zxing CPP"
LICENSE = "Apache-2.0"
HOMEPAGE = "https://github.com/glassechidna/zxing-cpp"
PN = "zxing-cpp"
SRCREV = "${AUTOREV}"
SRC_URI="git://github.com/glassechidna/zxing-cpp.git"
S = "${WORKDIR}/git"
LIC_FILES_CHKSUM = "file://COPYING;md5=86d3f3a95c324c9479bd8986968f4327"
inherit pkgconfig cmake
OECMAKE_GENERATOR = "Unix Makefiles"

do_compile() {
	oe_runmake
}
	
do_install() {
	install -Dm755 zxing ${D}${bindir}/zxing-cpp
}

FILES_${PN} = "/usr/bin/zxing-cpp"

