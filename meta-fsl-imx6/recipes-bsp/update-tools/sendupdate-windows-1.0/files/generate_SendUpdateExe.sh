#!/bin/bash

# exit on error of any command
set -e

# check command line arguments
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <signed binary main image>"
    #exit -1
fi

if [ ! -f $1 ]; then
    echo "Error: input file not found!"
    exit -1
fi

# input filename with absolute path
#IN=``

# the directory of the script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "creating temporary directory..."
WORK_DIR=`mktemp -d -p ${SCRIPT_DIR}`

# deletes the temp directory
function cleanup {
  rm -rf "$WORK_DIR"
}

# register the cleanup function to be called on the EXIT signal
trap cleanup EXIT

cd ${WORK_DIR}

# generate header
echo "generating header..."
ln -s /home/hades/Advantech/IMS-SE01/SourceCode/IMS-SE01/build-x11/tmp/deploy/images/imx6q-imsse01/adv-rootfs-main-imx6q-imsse01.ext4.bin ./payload
../../../tools/mingw32/bin/i686-w64-mingw32-ld -r -b binary -o payload.o payload


echo "compiling..."
cd ${SCRIPT_DIR}
../../tools/mingw32/bin/i686-w64-mingw32-g++ -I${WORK_DIR} SendUpdate.cpp ${WORK_DIR}/payload.o -o SendUpdate.exe -lws2_32 -static

echo "success"
exit 0
