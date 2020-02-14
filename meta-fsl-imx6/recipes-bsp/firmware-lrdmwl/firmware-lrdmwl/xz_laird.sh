#!/bin/sh -e
rm -f laird-backport-3.5.5.57.tar
rm -f laird-backport-3.5.5.57.tar.xz
tar cvf laird-backport-3.5.5.57.tar laird-backport-3.5.5.57
xz -z laird-backport-3.5.5.57.tar 