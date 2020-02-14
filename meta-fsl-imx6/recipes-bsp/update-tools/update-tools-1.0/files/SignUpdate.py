#!/usr/bin/env python3
# -*- coding: utf-8 -*-

#
# SignUpdate.py 0.2
#
# This program creates from a file a new file with a signature.
#
# Overview
# - A given input-file is read
# - A output-file is created and a header written into it
# - The input-file is written into the output-file and a signature calculated
# - When the input-file has been read completely, in the header the signature and the length of the input file is stored
#

from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA
from Crypto.PublicKey import RSA
import sys
import argparse
import os

#
# Sign the file
#
def signFile(inputfile, outputfile, keyfile):

    # Import the private key
    #
    key = RSA.importKey(keyfile.read())
    if key.size() > 2048:
        print("Error: Secret key length is larger than 2048. Length is: " + str(key.size()))
        sys.exit()
    h = SHA.new()

    # Create and write header to output-file
    #
    header = bytearray(512)
    header[0] = 0x01
    filessize = len(header)
    outputfile.write(header)

    # Read input-file, calculate signature, write data from input-file to output-file
    #
    print ("Signing... ")
    datablock = inputfile.read(1024)
    while (datablock):
        outputfile.write(datablock)
        h.update(datablock)
        filessize += len(datablock)
        datablock = inputfile.read(1024)
    inputfile.close()

    # Update the header with data length and signature
    #
    # Data length converted to big-endian and stored
    bytes = filessize.to_bytes(6, byteorder = 'big')
    outputfile.seek(3)
    outputfile.write(bytes)
    # Calculate signature and write it to output-file
    signer = PKCS1_v1_5.new(key)
    signature = signer.sign(h)
    outputfile.seek(16)
    outputfile.write(signature)
    outputfile.close()
    print ("Done.")

#
# Main
#
def main(argv):
    parser = argparse.ArgumentParser(description='SignUpdate 0.2')
    parser.add_argument('file_to_sign', type=argparse.FileType('rb'), help='input file to sign')
    parser.add_argument('-k', '--keyfile', type=argparse.FileType('rb'), metavar="private.pem", default="updateServerPrivateKey.pem", help='filename of secret key. key length should be 2048 bits.')
    parser.add_argument('-o', '--out', metavar="outputfile.bin", type=str, help='output filename')
    args = parser.parse_args()
    args.out = args.out if args.out else args.file_to_sign.name + ".bin"
    outputfile = open(args.out, 'wb')
    signFile(args.file_to_sign, outputfile, args.keyfile)

if __name__ == "__main__":
    main(sys.argv)
