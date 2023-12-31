# NOVA Raspberry Pi setup

This document provides step-by-step instructions to set up a Raspberry Pi in headless mode with the NOVA software and integrate it into a home WLAN environment.

Basic Linux console experience is assumed. To edit files use either vi or nano. For details to configure a Raspberry Pi, refer to
headless setup from scratch for integration into home network: <https://www.raspberrypi.org/documentation/configuration/>.

## Step-by-step instructions

### Get Raspian Buster Lite from: <https://www.raspberrypi.org/downloads/raspbian/>

* Flash image to SD card
* Mount the SD card and in /boot
* Create empty file /boot/ssh
* Create file /boot/wpa_supplicant.conf and add:

```
ctrl_interface=DIR=/var/run/wpa_supplicant GROUP=netdev
update_config=1
country=CH

network={
 ssid="Hotel Anker"
 psk="mypasswordhere"
}
```

### Plugin your Raspberry Pi and wait until boot is complete

* From your machine, ssh to raspberrypi.local:

```
ssh pi@raspberrypi.local
```

* The default password is _raspberry_
* In case hostname cannot be resolved, find the Raspberry Pi's IP address on your router and connect with ssh using the IP address.
* Change default password and enter root

```
passwd
sudo su
```

### Run the Raspberry Pi configuration utility

```
raspi-config
```

* Perform the following setup:
  * Update configuration utility
  * Networking: set hostname to any name you prefer (we'll use "novahost" in this documentation)
  * Localization options: set timezone and keyboard layout

### Update and install software

* Update the Raspberry Pi:

```
apt update
apt full-upgrade
apt install rpi-eeprom
rpi-eeprom-update
```

* Install required software:

```
apt-get install openjdk-8-jdk
apt-get install git
apt-get install libpcap0.8
```

### Configure startup script

* Edit /etc/rc.local, add:

```
cd /home/pi/nova/scripts
./novaraspi.sh > /dev/null 2>&1 &
```

### NOVA software setup

* Exit root

```
exit
```

* Get and compile nova code

```
cd /home/pi
git clone https://bitbucket.org/sschubiger/nova.git
cd nova/scripts
./compile.sh
cd ..
cp -rp src/native bin/native
```

* Edit raspi_1x1.properties, comment out:

```
## IP address for binding the HTTP server
# http=192.168.130.130
## IP address for binding the TCP server (only used by windows phone client)
# tcp=192.168.130.130
```

* Plug in nova via ethernet and reboot

```
sudo shutdown -r now
```

* After 10 seconds, the random lights should go on
* Connect to web interface via your web browser: http://novahost.local

## Troubleshooting

Important: likely, your home network runs on the 192.168.1.x network, you need to change this on your router, e.g. 192.168.2.x, since the 192.168.1.x network is used by the NOVA hardware.

* Make sure your nova is set to address 4 (see NOVA Server documentation).
* From your machine, ssh to novahost.local

```
ssh pi@novahost.local
```

* Edit /etc/dhcpcd.conf, add:

```
interface eth0
static ip_address=192.168.1.130/24
```

* Reboot the Raspberry Pi & ssh to novahost.local again:

```
ssh pi@novahost.local
```

* You should be able to ping the NOVA hardware via

```
ping 192.168.1.4
```

* If this is not the case, check your Nova address jumper settings again.
