mkdir -p ../bin
javac -cp ../bin:../lib/jnetpcap.jar:../lib/mail.jar:../bin -d ../bin ../src/ch/bluecc/nova/*.java ../src/ch/bluecc/nova/content/*.java ../src/org/corebounce/collections/*.java ../src/org/corebounce/io/*.java ../src/org/corebounce/net/*.java ../src/org/corebounce/net/winnetou/*.java ../src/org/corebounce/util/*.java

