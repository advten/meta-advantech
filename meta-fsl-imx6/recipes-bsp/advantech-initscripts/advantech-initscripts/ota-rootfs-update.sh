#!/bin/bash

#ota_partition=/dev/mmcblk1p3
ota_new_ext4=adv-rootfs-ota-imx6q-imsse01.ext4
ota_new_rootfs="ota_new_rootfs"
ota_old_rootfs="ota_old_rootfs"
check_mount_partition=`mount`
ota_partition=`cat /proc/cmdline | awk '{print $2}' | cut -d "=" -f2 | sed 's/.$/3/'`

update_ota_rootfs() {
	while true
	do
		systemctl stop systemd-udevd.service
		
		sleep 2

		dd if=/etc/adv-ota/adv-rootfs-ota-imx6q-imsse01.ext4 of=${ota_partition} bs=10M && \
		/sbin/e2fsck -p /dev/mmcblk2p3 && \
		sync
		
		if [ "$?" != "0" ]; then
			echo "[OTA Rootfs] update rootfs is failed, retry"
			continue
		fi
		
		systemctl restart systemd-udev-trigger.service
		
		sleep 5
		
		systemctl restart systemd-udevd.service
		
		break
	done
	
	return 0
}

main(){
		if [ ! -f /etc/adv-ota/${ota_new_ext4} ]; then
			echo "/etc/adv-ota/${ota_new_ext4} is not exist, exit"
			exit
		fi
		
		if [ ! -d /home/root/$ota_old_rootfs ]; then
			mkdir /home/root/$ota_old_rootfs
		fi

		if [ ! -d /home/root/$ota_new_rootfs ]; then
			mkdir /home/root/$ota_new_rootfs
		fi

		if [[ "$check_mount_partition" == *"${ota_partition}"* ]]; then
			echo "[OTA Rootfs] $ota_partition is already mounted"
		else			
			while true
			do
				echo "[OTA Rootfs] $ota_partition is mounting"
				
				mount -t ext4 ${ota_partition} /home/root/$ota_old_rootfs/
				
				if [ "$?" != "0" ]; then
					echo "[OTA Roofs] $ota_partition mount failed, update new rootfs"
					update_ota_rootfs
				else
					break
				fi
			done
		fi

		if [[ "$check_mount_partition" == *"${ota_new_ext4}"* ]]; then
			echo "[OTA Rootfs] $ota_new_ext4 is already mounted"
		else
			echo "[OTA Rootfs] $ota_new_ext4 is mounting"
			mount -t ext4 /etc/adv-ota/${ota_new_ext4} /home/root/$ota_new_rootfs/
			if [ $? -ne 0 ]; then
				echo "[OTA Rootfs] mount /etc/adv-ota/${ota_new_ext4} Fail and exit!"

				umount /home/root/$ota_old_rootfs
				rm -rf /home/root/$ota_new_rootfs
				rm -rf /home/root/$ota_old_rootfs
				exit
			fi
		fi

		ota_new_ver=`cat /home/root/ota_new_rootfs/etc/version`
		ota_old_ver=`cat /home/root/ota_old_rootfs/etc/version`

		echo "[OTA Rootfs] /etc/adv-ota/$ota_new_ext4 version: $ota_new_ver"
		echo "[OTA Rootfs] ${ota_partition} version: $ota_old_ver"

		umount /home/root/$ota_old_rootfs && sync
		umount /home/root/$ota_new_rootfs && sync


		if [[ "${ota_new_ver}" == "${ota_old_ver}" ]]; then
			echo "[OTA Rootfs] Already up-to-date"
		else
			echo "[OTA Rootfs] Update....."

			update_ota_rootfs

			if [ $? -ne 0 ]; then
				echo "[OTA Rootfs] Update Fail"
			else
				echo "[OTA Rootfs] Update success"
			fi
		fi

		rm -rf /home/root/$ota_new_rootfs
		rm -rf /home/root/$ota_old_rootfs
}

main
