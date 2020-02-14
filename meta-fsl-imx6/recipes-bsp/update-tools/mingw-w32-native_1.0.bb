DESCRIPTION = "Mingw-w32 cross toolchain (Host: Linux_64, Target: Windows_32)"
SECTION = "toolchains"
PR = "r1"
LICENSE = "CLOSED"

inherit native

S="${WORKDIR}"

SRC_URI = " \
    http://downloads.sourceforge.net/mingw-w64/Toolchains%20targetting%20Win32/Personal%20Builds/rubenvb/gcc-4.8-release/i686-w64-mingw32-gcc-4.8.0-linux64_rubenvb.tar.xz \
"

SRC_URI[md5sum] = "584c378b7c8cd64ddc326e6c5368a7e6"
SRC_URI[sha256sum] = "021e94114abbc8f964bb2731a995616712bb5866fb9263ae4c5a4bb3b3ec269a"

do_install() {
    mkdir -p ${DEPLOY_DIR_TOOLS}/mingw32
    cp -r ${WORKDIR}/mingw32/* ${DEPLOY_DIR_TOOLS}/mingw32
}
