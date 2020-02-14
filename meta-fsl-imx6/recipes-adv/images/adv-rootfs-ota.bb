DESCRIPTION = "Advantech Image - OTA rootfs"
LICENSE = "MIT"

inherit core-image
inherit distro_features_check

CORE_IMAGE_EXTRA_INSTALL += " \
        util-linux \
        python3 \
        python3-misc \
        python3-modules \
        python3-pycrypto \
        init-ifupdown \
        dhcp-server \
        dhcp-client \
        e2fsprogs \
        dosfstools \
        util-linux \
        parted \
        coreutils \
        advantech-initscripts \
        gnupg \
        advantech-partition-expansion \
        update-tools \
        "
		
DISTRO_FEATURES_remove = " x11 wayland bluetooth"

IMAGE_FSTYPES = "ext4"

inherit extrausers
EXTRA_USERS_PARAMS = "usermod -p '${ROOT_PASSWORD_HASH}' root; "

