#!/bin/sh
check_log=$(cat "/etc/egalax/egalax_fw_log")
cd /etc/egalax
echo "check egalax fw ..."
./eSensorTester_ARMhf > temp_log
result_log=$(cat "/etc/egalax/temp_log")
if [ "$check_log" = "$result_log" ]; then
        echo "PASS"
else
        echo "FAIL"
fi
