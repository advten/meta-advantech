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


function init_test() {
	BL_ENABLE=$(cat "/proc/adv_input_manager/light_en")
	BL_CONTORL=$(cat "/proc/adv_input_manager/control_bl")
	BL_TABLE=$(cat "/proc/adv_input_manager/levels")
	BL_RANGE=$(cat "/proc/adv_input_manager/threshold_range")
	echo BL_TABLE:$BL_TABLE
	sleep 0.2
	echo "0" > "/proc/adv_input_manager/light_en"
	echo "[20,100][5000,150][30000,200]" > "/proc/adv_input_manager/levels"
	NEW_BL_TABLE=($(cat "/proc/adv_input_manager/levels"))
	echo NEW_BL_TABLE:$NEW_BL_TABLE
	sleep 0.2
	echo "10" > "/proc/adv_input_manager/threshold_range"
	echo "1" > "/proc/adv_input_manager/control_bl"
	echo "1" > "/proc/adv_input_manager/light_en"
}

function recover_data() {
	echo "$BL_RANGE" > "/proc/adv_input_manager/threshold_range"
	echo "$BL_TABLE" > "/proc/adv_input_manager/levels"
	echo "$BL_ENABLE" > "/proc/adv_input_manager/light_en"
	echo "$BL_CONTORL" > "/proc/adv_input_manager/control_bl"
}

function mapping() {
LEVEL_TABLE=$(cat "/proc/adv_input_manager/levels" | tr -d "["  | tr "]" "\n" | tr "," "\n")

export MAPPING_RES="0"
#	echo "BL:$BL"
#	echo "TABLE:$LEVEL_TABLE"

for line in $LEVEL_TABLE
do


	if [ "$SW" = "0" ]
	then
		#echo "T_L:${line}"
		export SW="1"
		export MAPPING_LUX="$line"
	else
		#echo "T_B:${line}"
		export SW="0"
		export MAPPING_BL="$line"
		#echo "T_L:$MAPPING_LUX"
		#echo "T_B:$MAPPING_BL"

		if [ "$MAPPING_BL" = "$BL" ]
		then
			#echo "MAPPING_BL:$MAPPING_BL"
			if [ "$LUX" -lt "$MAPPING_LUX" ]
			then
				export SYN_BUG="0"
				export MAPPING_RES="1"
				#echo "MAPPING_LUX:$MAPPING_LUX"
			elif [ "$SYN_BUG" = "0" ]
			then
				export SYN_BUG="1"
				export MAPPING_RES="1"
				echo "1" > "/proc/adv_input_manager/light_en"
			elif [ "$SYN_BUG" = "1" ]
			then
				export SYN_BUG="0"
				export MAPPING_RES="0"
			fi
		fi
	fi
	done

	if [ "$MAPPING_RES" = "0" ] && [ "$BL" != "255" ]
	then
		export LOOP="0"
		echo "value not mapping"
		echo "FAIL"
	fi

	return 0;
}

if [ -f "$AMBIENT_LIGHT_SENSOR_PATH" ]; then
	CAL=($(cat "/proc/adv_input_manager/lux200"))
	echo CAL:$CAL
	init_test
fi
while [ "$LOOP" != "0"  ]
do

	if [ -f "$AMBIENT_LIGHT_SENSOR_PATH" ]; then
		LUX=($(cat "$AMBIENT_LIGHT_SENSOR_PATH"))
		#echo LUX:$LUX
		LUX=$[$[$LUX*200]/$CAL]
		echo LUX:$LUX
		BL=($(cat "/sys/class/backlight/backlight/brightness"))
		echo BL:$BL
		if [ "$RESULT" = "0" ]
		then
			echo "flashlight near"
			if [ "$BL" = "255" ]
			then
				export RESULT="1"
			fi
		elif [ "$RESULT" = "1" ]
		then
			echo "flashlight far"
			if [ "$BL" \< "255" ]
			then
			export RESULT="2"
			fi
		elif [ "$RESULT" = "2" ]
		then
			recover_data
			export LOOP="0"
			echo "auto brightness work"
			echo "PASS"
		fi


	else
		recover_data
		export LOOP="0"
		echo "Light Sensor node is not exist"
		echo "FAIL"
	fi

	if [ "$counter" = "90" ]
	then
		recover_data
		echo "time out"
		echo "FAIL"
		export LOOP="0"
	fi
	counter=$(($counter+1))
	#echo "counter:$counter"
sleep 1
done
