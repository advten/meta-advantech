#!/bin/sh

if [ -f /etc/dhcpserv_enable ]; then
	# wait ethernet interface
	sleep 15

	echo "starting the dhcp server..." > /dev/kmsg

	echo "configuring network interfaces... "
	/sbin/sysctl -e -p /etc/sysctl.conf >/dev/null 2>&1
	/sbin/ifup -a
	echo "done."

	echo "start dhcp... "
	/usr/sbin/dhcpd  -cf /etc/dhcp/dhcpd.conf enp1s0 -q
	echo "done."

	# wait dhcpd service
	sleep 10
fi

