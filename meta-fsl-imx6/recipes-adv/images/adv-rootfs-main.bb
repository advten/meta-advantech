DESCRIPTION = "Advantech Image - main rootfs"
LICENSE = "MIT"

require recipes-adv/images/adv-image-qt5-validation-imx.bb

CORE_IMAGE_EXTRA_INSTALL += " \
    firmware-lrdmwl \
    advantech-initscripts \
    brcm-patchram-plus \
    advantech-autobrightness \
    advtest-burnin \
    advtest-factory \
   "

# install packages for test purpose
CORE_IMAGE_EXTRA_INSTALL += " \
    advantech-hpattern \
    advantech-egalax-fw \
    advantech-vpm-fw \
	tslib \
	tslib-calibrate \
	tslib-tests\ 
    dhcp-server \
    dhcp-client \
    init-ifupdown \
    fbida \
    openssh \
    stress \
    iperf2 \
    "
# install package for requirement
CORE_IMAGE_EXTRA_INSTALL += " \
	opencv \
	zbar \
	zxing-cpp \
	tesseract \
"


IMAGE_FSTYPES = "ext4"

IMAGE_OVERHEAD_FACTOR = "1.6"

