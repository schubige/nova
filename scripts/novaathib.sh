#!/bin/sh
while true
do
./compile.sh
sudo nice -n -19 java -Xmx1024m -Djava.library.path=../lib -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar ch.digisyn.nova.NOVAControl ../hib_10x2.properties > /dev/null 2>&1
sleep 1
done
