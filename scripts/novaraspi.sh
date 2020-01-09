#!/bin/sh
sleep 10
while true
do
sudo java -Xmx512m -Djava.library.path=/tmp -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar ch.bluecc.nova.NOVAControl ../raspi_1x1.properties
sleep 1
done
