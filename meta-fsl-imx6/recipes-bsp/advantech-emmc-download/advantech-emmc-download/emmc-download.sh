#!/bin/bash
TRY_ERROR=`seq 0 1 2`

EMMC_PATH=/dev/mmcblk2
EMMC_BOOT_PATH=/dev/mmcblk2p1
EMMC_ROOTFS_PATH=/dev/mmcblk2p2
EMMC_OTA_ROOTFS_PATH=/dev/mmcblk2p3
EMMC_DATA_ROOTFS_PATH=/dev/mmcblk2p4

SOURCE_DIR=/emmc


BOOTLOADER_FILE=u-boot.imx
DTB_FILE=imx6q-imsse01.dtb
BOOT_FILE=zImage
ROOTFS_FILE=adv-rootfs-main-imx6q-imsse01.ext4
OTA_ROOTFS_FILE=adv-rootfs-ota-imx6q-imsse01.ext4


IMAGE_BOOTLOADER="u-boot"

# Handle u-boot suffixes
UBOOT_SUFFIX="imx"
UBOOT_SUFFIX_SDCARD="${UBOOT_SUFFIX}"

# boot partition volume id
BOOTDD_VOLUME_ID="${PROJECT}"

# boot partition size [in KiB]
BOOT_SPACE="8192"

# set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT="2048"

ROOTFS_MAIN_SIZE=$(expr 13 \* 1024 \* 1024 \+ 422912 \+ 1007)
ROOTFS_OTA_SIZE=$(expr 1 \* 1024 \* 1024)
ROOTFS_150M_SIZE=$(expr 150 \* 1024)



clean(){

     mount_device=`awk '{print $2}' /proc/mounts | grep $1`
     if [ "$mount_device" ];then
        umount $1 && sync

        sync
        sleep 2
        sync

        rm -rf $1
     fi
}


printlog(){
    echo "$1" | tee /dev/tty0
    echo "$1" > /dev/kmsg
}

bootloader(){
    printlog "[EMMC-Download]: Downloading booloader..."
    retval=1
    BOOTLOADER_PATH=${SOURCE_DIR}/${BOOTLOADER_FILE}
    SPI_ROM=/dev/mtdblock0

    if [ ! -e ${BOOTLOADER_PATH} ]; then
        printlog "[EMMC-Download]: Bootloader file not found!"
        retval=1
    fi

    if [ ! -e ${SPI_ROM} ]; then
        printlog "[EMMC-Download]: SPI_ROM device not found!"
        retval=1
    else
        retval=0
    fi

    if [ ${retval} -eq 0 ]; then
        for i in ${TRY_ERROR}
        do
            dd if=${BOOTLOADER_PATH} of=${SPI_ROM} bs=512 seek=2
            if [ $? -ne 0 ]; then
                printlog "[EMMC-Download]: dd failed\n"
            else
                printlog "[EMMC-Download]: u-boot download OK."

                retval=0
                break
            fi
        done
    else
        return 1
    fi

    if [ ${retval} -ne 0 ]; then
        #clean
        return 1
    else
        return 0
    fi
}

bootimage(){
    printlog  "[EMMC-Download]: Coping boot files..."
    TARGET_DIR=p1
    retval=1
    
    if [ ! -e ${EMMC_BOOT_PATH} ]; then
        printlog "[EMMC-Download]:Bootloader file not found!"
        return 1
    fi
    
    if [ ! -e ${TARGET_DIR} ]; then
    mkdir ${TARGET_DIR}
    fi

    mount -t vfat ${EMMC_BOOT_PATH} ${TARGET_DIR}
    if [ $? -ne 0 ]; then
        printlog "[EMMC-Download]: Fail to mount emmc boot partition!"
        return 1
    fi
    
    for i in ${TRY_ERROR}
    do
        cp ${SOURCE_DIR}/${DTB_FILE} ${TARGET_DIR}/
        if [ $? -ne 0 ] || [ ! -e "${TARGET_DIR}/${DTB_FILE}" ]; then
            printlog "[EMMC-Download]: Copy boot dtb file failed"
        else
            printlog "[EMMC-Download]: Copy dtb OK."
            retval=0
            break
        fi
    done
    
    if [ ${retval} -ne 0 ]; then
        clean ${EMMC_BOOT_PATH}
        clean ${TARGET_DIR}
        return 1
    fi
    
    for i in ${TRY_ERROR}
    do
        cp ${SOURCE_DIR}/${BOOT_FILE} ${TARGET_DIR}/
        if [ $? -ne 0 ] || [ ! -e "${TARGET_DIR}/${BOOT_FILE}" ]; then
            printlog "[EMMC-Download]: Copy boot file failed"
        else
            printlog "[EMMC-Download]: Copy boot file OK."
            retval=0
            break
        fi
    done
    
    clean ${EMMC_BOOT_PATH}
    clean ${TARGET_DIR}
    if [ ${retval} -ne 0 ]; then
        return 1
    else 
        return 0
    fi
}

rootfs(){
    printlog "[EMMC-Download]: Create rootfs..."
    TARGET_PATH=p2
    SOURCE_PATH=s2
    
    retval=1

    clean ${TARGET_PATH} && clean ${SOURCE_PATH}
    if [ ! -e ${TARGET_PATH} ]; then
        mkdir ${TARGET_PATH}
    fi
    if [ ! -e ${SOURCE_PATH} ]; then
        mkdir ${SOURCE_PATH}
    fi
    
    if [ ! -e ${EMMC_ROOTFS_PATH} ] || [ ! -e ${TARGET_PATH} ]; then
        printlog "[EMMC-Download]: File not found:"${EMMC_ROOTFS_PATH} or ${TARGET_PATH}
        return 1
    fi

    printlog "[EMMC-Download]: Mount rootfs partition..."
    mount -t ext4 ${EMMC_ROOTFS_PATH} ${TARGET_PATH}
    if [ $? -ne 0 ]; then
        printlog "[EMMC-Download]: rootfs partition mount failed"
        return 1
    fi
    
    printlog "[EMMC-Download]: Mount rootfs file..."
    mount ${SOURCE_DIR}/${ROOTFS_FILE} ${SOURCE_PATH}
    if [ $? -ne 0 ]; then
        clean ${EMMC_ROOTFS_PATH}
        clean ${TARGET_PATH} && clean ${SOURCE_PATH}
        printlog "[EMMC-Download]: rootfs file mount failed"
        return 1
    fi
    
    printlog "[EMMC-Download]: Copy rootfs..."
    for i in ${TRY_ERROR}
    do
        cp -R ${SOURCE_PATH}/* ${TARGET_PATH}/
        if [ $? -ne 0 ]; then
            printlog "[EMMC-Download]: Copy rootfs file failed"
            printlog "[EMMC-Download]: Please check SD image!"
            #read -rsp $'press any key...'
        else
            printlog "[EMMC-Download]: Copy rootfs ok!"
            retval=0
            break
        fi
    done

    clean ${EMMC_ROOTFS_PATH}
    clean ${SOURCE_PATH} && clean ${TARGET_PATH}
	
	return 0;
}

ota_rootfs(){
    printlog "[EMMC-Download]: Create ota rootfs..."
    TARGET_PATH=p3
    SOURCE_PATH=s3
    
    retval=1

    clean ${TARGET_PATH} && clean ${SOURCE_PATH}

    if [ ! -e ${TARGET_PATH} ]; then
        mkdir ${TARGET_PATH}
    fi
    if [ ! -e ${SOURCE_PATH} ]; then
        mkdir ${SOURCE_PATH}
    fi
    
    if [ ! -e ${EMMC_OTA_ROOTFS_PATH} ] || [ ! -e ${TARGET_PATH} ]; then
        printlog "[EMMC-Download]: File not found:"${EMMC_OTA_ROOTFS_PATH}
        return 1
    fi

    printlog "[EMMC-Download]: Mount ota rootfs partition..."
    mount -t ext4 ${EMMC_OTA_ROOTFS_PATH} ${TARGET_PATH}
    if [ $? -ne 0 ]; then
        printlog "[EMMC-Download]: ota rootfs partition mount failed"
        return 1
    fi
    
    printlog "[EMMC-Download]: Mount rootfs file..."
    mount ${SOURCE_DIR}/${OTA_ROOTFS_FILE} ${SOURCE_PATH}
    if [ $? -ne 0 ]; then
        umount ${TARGET_PATH}
        printlog "[EMMC-Download]: ota rootfs file mount failed"
        return 1
    fi
    
    printlog "[EMMC-Download]: Copy ota rootfs..."
    for i in ${TRY_ERROR}
    do
        cp -R ${SOURCE_PATH}/* ${TARGET_PATH}/
        if [ $? -ne 0 ]; then
            clean ${EMMC_OTA_ROOTFS_PATH}
            clean ${TARGET_PATH} && clean ${SOURCE_PATH}
            printlog "[EMMC-Download]: Copy ota rootfs file failed"
            printlog "[EMMC-Download]: Please check SD image!"
            read -rsp $'press any key...'
        else
            clean ${EMMC_OTA_ROOTFS_PATH}
            clean ${TARGET_PATH} && clean ${SOURCE_PATH}
            printlog "[EMMC-Download]: Copy ota rootfs ok!"
            printlog "[EMMC-Download]: Done and Success!"
            printlog "[EMMC-Download]: Please remove SD Card and reboot device!"
            read -rsp $'press any key...'
            retval=0
            break
        fi
    done
    sync
    sync
	return 0;
}

validateSourceFile(){
    if [ ! -e ${SOURCE_DIR}/${BOOTLOADER_FILE} ]; then
        printlog "EMMC-Download]: Bootloader file: "${SOURCE_DIR}/${BOOTLOADER_FILE}" not found!"
        return 1
    fi
    
    mount_device=`awk '{print $1}' /proc/mounts | grep "/dev/sdb*"`
    if [ "$mount_device" ];then
       umount $mount_device
    fi

    if [ ! -e ${SOURCE_DIR}/${DTB_FILE} ]; then
        printlog "EMMC-Download]: DTB file not found!"
        return 1
    fi
    
    if [ ! -e ${SOURCE_DIR}/${BOOT_FILE} ]; then
        printlog "EMMC-Download]: Boot file not found!"
        return 1
    fi
    
    if [ ! -e ${SOURCE_DIR}/${ROOTFS_FILE} ]; then
        printlog "EMMC-Download: Rootfs file not found!"
        return 1
    fi
	
	if [ ! -e ${SOURCE_DIR}/${OTA_ROOTFS_FILE} ]; then
        printlog "EMMC-Download: OTA Rootfs file not found!"
        return 1
    fi
	
    return 0;
}

main(){
    # Preparing for backup block
    echo "" | tee /dev/tty0
    echo "" | tee /dev/tty0
    printlog "[EMMC-Download]: Preparing for backup block"

    
    validateSourceFile
    if [ $? -ne 0 ]; then
        printlog "[EMMC-Download]: Please check SD image!"
		read -rsp $'press any key...'
        return 1
    fi

    # Bootloader
    bootloader
    retval=$?
    if [ ${retval} -ne 0 ]; then
        printlog "[EMMC-Download]: Please check SD image!"
        read -rsp $'press any key...'
        return 1
    fi
    
    # Do file system

    printlog "[EMMC-Download]: Do EMMC file system..."
	dd if=/dev/zero of=${EMMC_PATH}  bs=1M  count=10
	
    printlog "[EMMC-Download]: Format EMMC..."


	parted -s ${EMMC_PATH} mklabel gpt
	parted -s ${EMMC_PATH} unit KiB mkpart primary ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} \- 1)
	parted -s ${EMMC_PATH} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE} \- 1)
	parted -s ${EMMC_PATH} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE} + ${ROOTFS_OTA_SIZE} \- 1)
	parted -s ${EMMC_PATH} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE} + ${ROOTFS_OTA_SIZE}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE} + ${ROOTFS_OTA_SIZE} + ${ROOTFS_150M_SIZE} \- 1)
	#parted -s ${EMMC_PATH} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE} + ${ROOTFS_MAIN_SIZE} + ${ROOTFS_OTA_SIZE} + ${ROOTFS_150M_SIZE}) 100%
	parted ${EMMC_PATH} print

	sync && sleep 1


    mkfs.vfat ${EMMC_BOOT_PATH}
    mkfs.ext4 -F ${EMMC_ROOTFS_PATH}
	mkfs.ext4 -F ${EMMC_OTA_ROOTFS_PATH}
	mkfs.ext4 -F ${EMMC_DATA_ROOTFS_PATH}
	
	sync && sleep 1
    
    # Copy zImage dtb
    bootimage

    if [ $? -ne 0 ]; then
        printlog "[EMMC-Download]: Please check SD image!"
        read -rsp $'press any key...'
        return 1
    fi
    
    # Copy rootfs
    rootfs
	if [ $? -ne 0 ]; then
            read -rsp $'press any key...'
	else
            # Copy ota rootfs
	    ota_rootfs
    fi

}

main
