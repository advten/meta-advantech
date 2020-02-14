#!/bin/sh

#================================================
# environment setup
#================================================
# system configuration
FREESPACE_ALIGNEMENT=32
MSG_TAG="Partition Expansion"
DEBUG=1

DATA_PARTITION_NUM="4"
DATA_PARTITION_INIT="0"
DATA_PARTITION_DIR="/data"
DATA_PARTITION_DESCRIPTION="/data/description"
DATA_PARTITION_PASSWORD="advantech data partition"

# /dev/mmcblk[0-9]
MMCDEV_NAME=$(cat /proc/cmdline | sed s/.*root=/root=/g | cut -d " " -f1 | awk -F "=" '{print $NF}' | sed s/p[0-9]//g)

#================================================
# function
#================================================
function printlog () {
	MSG=$1
	
	echo "[${MSG_TAG}] : ${MSG}"
}

function create_data_partition () {
	# unit : MB
	MMCDEV_FREESPACE_START=$(parted ${MMCDEV_NAME} unit MB print free | sed /^$/d | tail -1 | sed s/MB//g | awk '{print $1}')
	MMCDEV_FREESPACE_END=$(parted ${MMCDEV_NAME} unit MB print free | sed /^$/d | tail -1 | sed s/MB//g | awk '{print $2}')
	MMCDEV_FREESPACE_SIZE=$(parted ${MMCDEV_NAME} unit MB print free | sed /^$/d | tail -1 | sed s/MB//g | awk '{print $3}')

	MMCDEV_FREESPACE_START_ALIGNED=$(expr ${MMCDEV_FREESPACE_START} + $(expr ${FREESPACE_ALIGNEMENT} - 1))
	MMCDEV_FREESPACE_START_ALIGNED=$(expr ${MMCDEV_FREESPACE_START_ALIGNED} - $(expr ${MMCDEV_FREESPACE_START_ALIGNED} % ${FREESPACE_ALIGNEMENT}))

	MMCDEV_FREESPACE_SIZE_ALIGNED=$(expr ${MMCDEV_FREESPACE_SIZE} - $(expr ${MMCDEV_FREESPACE_START_ALIGNED} - ${MMCDEV_FREESPACE_START}))
	MMCDEV_FREESPACE_SIZE_ALIGNED=$(expr ${MMCDEV_FREESPACE_SIZE_ALIGNED} - $(expr ${MMCDEV_FREESPACE_SIZE_ALIGNED} % ${FREESPACE_ALIGNEMENT}))

	MMCDEV_FREESPACE_END_ALIGNED=$(expr ${MMCDEV_FREESPACE_START_ALIGNED} + ${MMCDEV_FREESPACE_SIZE_ALIGNED})

	if [ "${DEBUG}" == "1" ]; then
		printlog "MMCDEV_FREESPACE_START         : $MMCDEV_FREESPACE_START"
		printlog "MMCDEV_FREESPACE_END           : $MMCDEV_FREESPACE_END"
		printlog "MMCDEV_FREESPACE_SIZE          : $MMCDEV_FREESPACE_SIZE"
		printlog "MMCDEV_FREESPACE_START_ALIGNED : $MMCDEV_FREESPACE_START_ALIGNED"
		printlog "MMCDEV_FREESPACE_END_ALIGNED   : $MMCDEV_FREESPACE_END_ALIGNED"
		printlog "MMCDEV_FREESPACE_SIZE_ALIGNED  : $MMCDEV_FREESPACE_SIZE_ALIGNED"
	fi

	parted -s ${MMCDEV_NAME} unit MB mkpart primary ext4 ${MMCDEV_FREESPACE_START_ALIGNED} ${MMCDEV_FREESPACE_END_ALIGNED}

	if [ "$?" != "0" ]; then
		printlog "create partition by parted command failed"
		return 1
	else
		DATA_PARTITION_INIT="1"
	fi

	sync

	sleep 5

	mkfs -t ext4 "${MMCDEV_NAME}p${DATA_PARTITION_NUM}"

	return 0
}

function manage_data_partition () {
	local password

	# check mountpoint
	if [ ! -d ${DATA_PARTITION_DIR} ]; then
		mkdir -p ${DATA_PARTITION_DIR}
	fi

	# mount partition
	umount "${MMCDEV_NAME}p${DATA_PARTITION_NUM}" &> /dev/null
	umount ${DATA_PARTITION_DIR} &> /dev/null

	mount -t ext4 "${MMCDEV_NAME}p${DATA_PARTITION_NUM}" ${DATA_PARTITION_DIR}

	if [ "$?" != "0" ]; then
	
		sleep 5
		
		mount -t ext4 "${MMCDEV_NAME}p${DATA_PARTITION_NUM}" ${DATA_PARTITION_DIR}
		
		if [ "$?" != "0" ]; then
		
			# give it the second shot
			mkfs -t ext4 "${MMCDEV_NAME}p${DATA_PARTITION_NUM}" && \
			mount -t ext4 "${MMCDEV_NAME}p${DATA_PARTITION_NUM}" ${DATA_PARTITION_DIR} && \
			sync
			
			if [ "$?" != "0" ]; then
				printlog "mount data partition failed"
				return 1
			else
				DATA_PARTITION_INIT="1"
			fi
		fi
	fi

	# verify partition
	if [ "${DATA_PARTITION_INIT}" == "1" ]; then
		echo "${DATA_PARTITION_PASSWORD}" > ${DATA_PARTITION_DESCRIPTION}
	else
		if [ -e "${DATA_PARTITION_DESCRIPTION}" ]; then
			password=$(cat "${DATA_PARTITION_DESCRIPTION}")

			if [ "$password" != "${DATA_PARTITION_PASSWORD}" ]; then
				printlog "unrecognized system partition layout"
				return 1
			fi
		else
			printlog "description file is not exist"
			return 1
		fi
	fi

	sync

	return 0
}

#================================================
# main
#================================================
if [ "${MMCDEV_NAME}" == "/dev/mmcblk2" ]; then
	printlog "eMMC device management"

	# eMMC partition has been defined
	DATA_PARTITION_NUM="5"
	
	manage_data_partition

	if [ "$?" != 0 ]; then
		printlog "manage the data partition failed"
		exit 1
	fi
	
elif [ "${MMCDEV_NAME}" == "/dev/mmcblk1" ]; then
	printlog "SD device management"
	
	MMCDEV_PARTITION_NUM=$(parted -s ${MMCDEV_NAME} print | grep -e "^ [0-9]" | wc -l)

	if [ "${MMCDEV_PARTITION_NUM}" == "$(expr ${DATA_PARTITION_NUM} - 1)" ]; then
		printlog "create the partition to store user data"
		
		create_data_partition

		if [ "$?" != 0 ]; then
			printlog "create the data partition failed"
			exit 1
		fi

		manage_data_partition

		if [ "$?" != 0 ]; then
			printlog "manage the data partition failed"
			exit 1
		fi
	elif [ "${MMCDEV_PARTITION_NUM}" == "${DATA_PARTITION_NUM}" ]; then
		printlog "the ${MMCDEV_NAME}p${DATA_PARTITION_NUM} has been created"

		manage_data_partition

		if [ "$?" != 0 ]; then
			printlog "manage the data partition failed"
			exit 1
		fi

		exit 0
	else
		printlog "unknown partition layout for this project"
		exit 1
	fi
else
	printlog "unknow mmc device : ${MMCDEV_NAME}"
	exit 1
fi
#================================================
