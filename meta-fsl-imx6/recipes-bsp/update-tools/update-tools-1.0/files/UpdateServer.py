#!/usr/bin/env python3
# -*- coding: utf-8 -*-

#
# UpdateServer.py 0.5
#
# This program is a server and receives and installs a firmware on a target device
#
# Overview
# - Program waits in a loop to receive data from a client
# - The data is a file that contains a header and a TAR file
# - The header contais a file version, data size and signature of the file
# - At first, the header is received
# - Next, the TAR file is received and extraced to a given directory
# - Finally, the signature of the received data is verified
#

from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA

import socket
import sys
import tarfile
from io import RawIOBase
import sys
import os
import argparse
import logging
import logging.handlers
import subprocess
import tempfile
import shutil

isMounted = False
dryRun = False

# Exception class
#
class WrongFileVersionException(Exception):
    pass

# Exception class
#
class InvalidDatasizeException(Exception):
    pass

#
# Class SocketStreamReader
#
# This class implements the functionality to read data from a socket and to calculate a signature of the data
# The recived data contains a header, and a TAR file
#
#
class SocketStreamReader(RawIOBase):

    signature = 0
    sc = 0
    verifier = 0
    sha = 0
    logger = 0
    expectedstreamsize = 0
    filesize = 0
    expectedfilesize = 0
    actualfilesize = 0

    def __init__(self, logger):
        self.logger = logger

    # Read data from socket
    # Tarfile object uses in method "extractall" the overwritten "read" method of the SocketStreamReader class
    def read(self, size):
        x = b''
        remaining = size
        # Receive from socket the data size that is requested
        # Ensure that the data size is received, even if the socket delivers blocks of smaller sizes
        while remaining > 0 and self.filesize > 0:
            chunk = self.sc.recv(remaining)
            if chunk:
                x += chunk
                remaining -= len(chunk)
                self.filesize -= len(chunk)
            else:
                break
        # Update signature, calculate actual received data
        if len(x) > 0:
            self.sha.update(x)
            self.actualfilesize += len(x)
        return x

    # Open a socket connection
    #
    def openSocket(self, ip, port):
        logger.info("Waiting for connection")
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
        s.bind((ip, int(port)))
        s.listen(10)
        self.sc, address = s.accept()
        logger.info(("Accepted connection from " + str(address)))
        return self.sc

    # Close socket connection
    #
    def closeSocket(self):
        try:
            self.sc.close()
        except Exception as e:
            pass

    # Receive and process the header
    #
    def readHeader(self):
        remaining = 512
        header = b''

        # Receive the 512 bytes of header data
        while remaining > 0:
            chunk = self.sc.recv(remaining)
            header += chunk
            remaining -= len(chunk)

        # Check header version
        fileversion = header[0]
        logger.info("Header version: " + str(fileversion))
        if fileversion != 1:
            raise WrongFileVersionException()

        # Get data size, convert from big-endian to system-endian
        integerbytes = header[3:9]
        self.expectedstreamsize = int.from_bytes(integerbytes,byteorder = 'big')

        # Set the data size to be received
        logger.info("Expected total streamsize: " + str(self.expectedstreamsize))
        self.filesize = self.expectedstreamsize - 512
        self.expectedfilesize = self.filesize

        # Set the signature, 2048 bits
        self.signature = header[16:272]

    # Load the public key and init verifyer
    #
    def initSignatureVerifyer(self, keyfile):
        keyfile.seek(0)
        key = RSA.importKey(keyfile.read())
        self.verifier = PKCS1_v1_5.new(key)
        self.sha = SHA.new()

    # Check the signature after having received all the data
    #
    def checkSignature(self):
        if self.verifier.verify(self.sha, self.signature):
            return True
        else:
            return False

    # Check the received data size of the TAR file after having received all the data
    #
    def checkReceivedDatasize(self):
        logger.info("Expected filesize: " + str(self.expectedfilesize))
        logger.info("Actual filesize: " + str(self.actualfilesize))
        if self.expectedfilesize != self.actualfilesize:
            raise InvalidDatasizeException()

    # After extracting the TAR and installation of firmware, send result to client
    #
    def sendResult(self, id, text):
        try:
            result = "01:"
            result += id[0:4]
            result += ":"
            result += text
            logger.info ('Sending result: '+result)
            self.sc.sendall(result.encode('utf-8'))
        except Exception as e:
            logger.error("Failed to send data to client.")

    def logInternalData(self):
            logger.info("len(signature) = " +  str(len(self.signature)))
            logger.info("filesize = " +  str(self.filesize))
            logger.info("expectedfilesize = " +  str(self.expectedfilesize))
            logger.info("actualfilesize = " +  str(self.actualfilesize))

# Receive the TAR, and extract it
#
def readAndExtractTAR(ssr, directory):
    logger.info('Downloading and extracting')
    # Tarfile object uses in method "extractall" the overwritten "read" method of the SocketStreamReader class
    with tarfile.open(mode='r|*', fileobj=ssr, debug=0) as tf:
        tf.extractall(path=directory)

# Format and Mount Main Partition to /mnt
#
def formatAndMountMainPartition():
    global isMounted
    if isMounted:
        raise OSError("Partition is already mounted!")
    else:
        logger.info("Formatting partition")
        if not dryRun:
            subprocess.check_call("mkfs -t ext4 /dev/mmcblk2p2", shell=True, timeout=60)
        logger.info("Mounting partition")
        if not dryRun:
            subprocess.check_call("mount /dev/mmcblk2p2 /mnt", shell=True)
        isMounted = True

# Sync and Umount Main Partition
#
def unmountFilesystem():
    global isMounted
    if isMounted:
        logger.info("Unmounting partition")
        if not dryRun:
            subprocess.check_call("sync", shell=True)
            subprocess.check_call("umount /mnt", shell=True)
            subprocess.check_call("sync", shell=True)
        isMounted = False

# Enable Main System via U-Boot env
#
def enableMainSystem():
    subprocess.check_call("fw_setenv mainsysvalid 1", shell=True)
    logger.info("enabling main system")

# Disable Main System via U-Boot env
#
def disableMainSystem():
    subprocess.check_call("fw_setenv mainsysvalid 0", shell=True)
    logger.warn("disabling main system")

# Enable OTA Boot via U-Boot env
#
def enableOTABoot():
    subprocess.check_call("fw_setenv OTA_boot yes", shell=True)
    logger.info("enabling OTA_boot")

# Disable OTA Boot via U-Boot env
#
def disableOTABoot():
    subprocess.check_call("fw_setenv OTA_boot no", shell=True)
    logger.warn("disabling OTA_boot")

# Create and init the logging
#
def createLogger():
    logger = logging.getLogger('updateserver')
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    if not dryRun:
        hdlr = logging.FileHandler('/var/log/updateserver.log','w')
        hdlr.setFormatter(formatter)
        logger.addHandler(hdlr)
    # also log to stderr
    stderrHandler = logging.StreamHandler()
    stderrHandler.setFormatter(formatter)
    logger.addHandler(stderrHandler)
    logger.setLevel(logging.INFO)
    return logger

#
# Main
#
if __name__ == "__main__":
    programname = 'UpdateServer 0.5'
    parser = argparse.ArgumentParser(description=programname)
    parser.add_argument('-k', '--key', type=argparse.FileType('rb'), default="/etc/updateServerPublicKey.pem", help='Filename of public key to use')
    parser.add_argument('-p', '--port', type=str, metavar="PORT", default="5001", help='Port number to use. Default: 9999')
    parser.add_argument('-a', '--address', type=str, metavar="ADDRESS", default="", help='IP address to use. Default: Listen on all interfaces')
    parser.add_argument('-d', '--dry-run', type=bool, metavar="true/false", default=False, help='Enable dry run (no modifications in system). Default: false')
    args = parser.parse_args()
    address = args.address
    port = args.port
    key = args.key
    dryRun = args.dry_run
    logger = createLogger()
    logger.info(programname)

    listening = "Listening on IP " + address + ", port " + str(port)
    logger.info(listening)

    tempDirPath = ""
    while True:
        try:
            ssr = SocketStreamReader(logger)
            ssr.openSocket(address, port)
            ssr.readHeader()
            ssr.initSignatureVerifyer(key)

            disableMainSystem()
            enableOTABoot()
            formatAndMountMainPartition()
            if dryRun:
                tempDirPath = tempfile.mkdtemp()
                readAndExtractTAR(ssr, tempDirPath)
            else:
                readAndExtractTAR(ssr, "/mnt")
            ssr.checkReceivedDatasize()
            if ssr.checkSignature():
                enableMainSystem()
                disableOTABoot()
                msg = "Firmware installed."
                logger.info(msg)
                ssr.sendResult("0000","OK")
                logger.info("Rebooting")
                unmountFilesystem()
                ssr.closeSocket()
                if dryRun:
                    logger.info("DRY RUN. NOT REBOOTING!")
                else:
                    subprocess.check_call("reboot")
            else:
                error = "Error. The firmware signature is invalid."
                logger.error(error)
                ssr.sendResult("1300",error)

        except tarfile.TarError as e:
            error = "Error while extracting the tarfile."
            logger.error(error)
            ssr.sendResult("1000",error)
        except (socket.gaierror, socket.error) as e:
            error = "Connection lost."
            logger.error(error)
        except OSError as e:
            error = "OSError occured."
            logger.error(error)
            ssr.sendResult("1100",error)
        except WrongFileVersionException as e:
            error = "Error. Bad version number in firmware."
            logger.error(error)
            ssr.sendResult("1200",error)
        except InvalidDatasizeException as e:
            error = "Error. The data size received from client does not mach the expected data size."
            logger.error(error)
            ssr.sendResult("1300",error)
        except KeyboardInterrupt as e:
            error = "Program stopped by keyboard interrupt."
            logger.error(error)
            ssr.sendResult("1400",error)
            sys.exit()
        except Exception as e:
            error = "General exception caught."
            logger.exception(error)
            ssr.sendResult("2000",error)
        finally:
            if dryRun:
                shutil.rmtree(tempDirPath)
            unmountFilesystem()
            ssr.closeSocket()
