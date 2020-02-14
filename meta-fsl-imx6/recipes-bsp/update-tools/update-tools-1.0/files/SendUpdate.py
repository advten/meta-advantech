#!/usr/bin/env python3
# -*- coding: utf-8 -*-

#
# SendUpdate.py 0.5
#
# This program sends a file to the UpdateServer
#
# Overview
# - A given input-file is read
# - The file is send via a socket to the UpdateServer
# - After sending the entire file, a result is expected from the UpdateServer. The result is displayed.
#
#

import socket
import sys
import os
import argparse

#
# Class SocketStreamWriter
#
# This class implements the functionality to read the input file and to send data via a socket
#
class SocketStreamWriter:

  sc = 0
  filesize = 0
  sentData = 0
  last_percentage = -1

  # Open a socket connection
  #
  def openSocket(self, address, port):
      print("Connecting to " + address + " port " + str(port) + "...")
      self.sc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      self.sc.connect((address,int(port)))
      print("Connected")
      return self.sc

  # Close socket connection
  #
  def closeSocket(self):
      try:
         self.sc.close()
      except Exception as e:
         error = "Exception caught: " + e.message
         print(error)

  # Send data via a socket connection
  #
  def send(self, data):
      self.sentData += len(data)
      current_percentage = round((self.sentData / filesize) * 100)
      if (self.last_percentage != current_percentage):
          sys.stdout.write("Progress: {0:d}%\r".format(current_percentage))
          sys.stdout.flush()
          self.last_percentage = current_percentage
      self.sc.sendall(data)

  # Receive a result from the server
  #
  def receiveResult(self):
      result =""
      try:
          chunk = self.sc.recv(256).decode('utf-8')
          while chunk:
              result += chunk
              chunk = self.sc.recv(256).decode('utf-8')
      except Exception as e:
          pass
      finally:
          print("Result: "+ result)

# Read the input-file, send data
#
def sendfile(ssw, f):
    print("Sending file...")

    l = f.read(8192)
    while(l):
      ssw.send(l)
      l = f.read(8192)

#
# Main
#
if __name__ == "__main__":

    parser = argparse.ArgumentParser(description='SendUpdate 0.5')
    parser.add_argument('file_to_send', type=argparse.FileType('rb'), help='file to send')
    parser.add_argument('-a', '--address', type=str, metavar="HOSTNAME/IP", default="192.168.11.100", help='hostname/IP address to connect to')
    parser.add_argument('-p', '--port', type=int, default="5001", help='port to connect to')
    args = parser.parse_args()
    filesize = os.path.getsize(args.file_to_send.name)
    try:
        ssw = SocketStreamWriter()
        ssw.filesize = filesize
        ssw.openSocket(args.address, args.port)
        sendfile(ssw, args.file_to_send)
        ssw.receiveResult()
    except KeyboardInterrupt as e:
        pass
    except ConnectionRefusedError as e:
        error = "Could not connect to the server."
        print(error)
    except ConnectionResetError as e:
        ssw.receiveResult()
    except Exception as e:
        error = "General exception caught."
        print(error)
        print(e)
        ssw.receiveResult()
    finally:
        ssw.closeSocket()
