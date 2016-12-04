#!/bin/sh
while true
do
sudo java -Xmx1024m -Djava.library.path=../lib -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar ch.digisyn.nova.NOVAControl ../1x1.properties
sleep 1
done
