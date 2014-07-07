#!/bin/sh
while true
do
sudo java -Xmx512m -Xincgc -Dhttp=192.168.100.1 -Djava.library.path=../lib -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar ch.digisyn.nova.NOVAControl ../1x1.properties
sleep 1
done
