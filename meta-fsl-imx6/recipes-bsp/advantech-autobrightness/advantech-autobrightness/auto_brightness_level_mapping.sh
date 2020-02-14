#!/bin/sh

AMBIENT_LIGHT_SENSOR_PATH="/proc/adv_input_manager/lux"
CAL="200"
LOOP="1"
LUX="1"
MAPPING_LUX="0"
MAPPING_BL="0"
MAPPING_RES="0"
RESULT="0"
SW="0"
counter=1
SYN_BUG="0"
sleep 2

function init_test() {
	BK_ENABLE=$(cat "/proc/adv_input_manager/light_en")
	BK_CONTORL=$(cat "/proc/adv_input_manager/control_bl")
	BK_TABLE=$(cat "/proc/adv_input_manager/levels")
	BK_RANGE=$(cat "/proc/adv_input_manager/threshold_range")
	BK_CAL=$(cat "/proc/adv_input_manager/lux200")
	### echo BK_TABLE:$BK_TABLE
	sleep 0.5
	echo "0" > "/proc/adv_input_manager/light_en"
	echo "0" > "/proc/adv_input_manager/control_bl"
	echo "200" > "/proc/adv_input_manager/lux200"
	echo "[100,40][200,60][500,80][1200,140][2500,180][6000,220][10000,250]" > "/proc/adv_input_manager/levels"
	NEW_BK_TABLE=($(cat "/proc/adv_input_manager/levels"))
	echo NEW_BK_TABLE:$NEW_BK_TABLE
	sleep 1
	echo "3" > "/proc/adv_input_manager/threshold_range"
	echo "1" > "/proc/adv_input_manager/control_bl"
	echo "1" > "/proc/adv_input_manager/light_en"
}

function recover_data() {
	echo "$BK_RANGE" > "/proc/adv_input_manager/threshold_range"
	echo "$BK_TABLE" > "/proc/adv_input_manager/levels"
	echo "$BK_ENABLE" > "/proc/adv_input_manager/light_en"
	echo "$BK_CONTORL" > "/proc/adv_input_manager/control_bl"
	echo "$BK_CAL" > "/proc/adv_input_manager/lux200"
}
 
function mapping2() {
	if [[ "$LUX" -lt "100" ]] && [[ "$BL" -eq "40" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "99" ]] && [[ "$LUX" -lt "200" ]] && [[ "$BL" -eq "60" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "199" ]] && [[ "$LUX" -lt "500" ]] && [[ "$BL" -eq "80" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "499" ]] && [[ "$LUX" -lt "1200" ]] && [[ "$BL" -eq "140" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "1199" ]] && [[ "$LUX" -lt "2500" ]] && [[ "$BL" -eq "180" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "2499" ]] && [[ "$LUX" -lt "6000" ]] && [[ "$BL" -eq "220" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "5999" ]] && [[ "$LUX" -lt "10000" ]] && [[ "$BL" -eq "250" ]];then
		export RESULT="1"
	fi
	if [[ "$LUX" -gt "9999" ]] && [[ "$BL" -eq "255" ]];then
		export RESULT="1"
	fi
}

if [ -f "$AMBIENT_LIGHT_SENSOR_PATH" ]; then
	CAL=($(cat "/proc/adv_input_manager/lux200"))
	echo CAL:$CAL
	init_test
fi
sleep 1
while [ "$LOOP" != "0" ]
do
sleep 1
	if [ -f "$AMBIENT_LIGHT_SENSOR_PATH" ]; then
		LUX=($(cat "$AMBIENT_LIGHT_SENSOR_PATH"))
		BL=($(cat "/sys/class/backlight/backlight/brightness"))
		if [ "$RESULT" = "0" ]
		then
			mapping2
		fi
		if [ "$RESULT" = "1" ]
		then
			recover_data
			export LOOP="0"
			echo LUX:$LUX
			echo BL:$BL
			echo "auto brightness mapping table"
			echo "PASS"
		fi
	else
		recover_data
		export LOOP="0"
		echo "Light Sensor node is not exist"
		echo "FAIL"
	fi

	if [ "$counter" -gt "5" ]
	then
		recover_data
		echo "time out"
		echo "FAIL"
		export LOOP="0"
	fi
	counter=$(($counter+1))
	### echo "counter:$counter"

done
