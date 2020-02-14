DESCRIPTION = "Mingw-w64 cross toolchain (Host: Linux_64, Target: Windows_64)"
SECTION = "toolchains"
PR = "r1"
LICENSE = "CLOSED"

inherit native

S="${WORKDIR}"

SRC_URI = " \
    http://downloads.sourceforge.net/mingw-w64/Toolchains%20targetting%20Win64/Personal%20Builds/rubenvb/gcc-4.8-release/x86_64-w64-mingw32-gcc-4.8.0-linux64_rubenvb.tar.xz \
"

SRC_URI[md5sum] = "af194517da252704cb6d07ee601dfe3b"
SRC_URI[sha256sum] = "5b7a22bf0c0a90309f77851b855e479317b9dd0cf1d1c6eddd88a35f3f02c9f5"

do_install() {
    mkdir -p ${DEPLOY_DIR_TOOLS}/mingw64
    cp -r ${WORKDIR}/mingw64/* ${DEPLOY_DIR_TOOLS}/mingw64
}
