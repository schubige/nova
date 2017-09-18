#!/bin/sh
while true
do
./compile.sh
sudo java -Xmx1024m -Djava.library.path=../lib -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar ch.digisyn.nova.NOVAControl ../hib_10x2.properties > /dev/null 2>&1
sudo renice -19 $(pidof java)
sudo renice 19 $(pidof TeamViewer_Desktop)
sleep 1
done
