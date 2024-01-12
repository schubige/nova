# NOVA Raspberry Pi setup

This document provides step-by-step instructions to set up a Raspberry Pi in headless mode with the NOVA software and integrate it into a home WLAN environment.

Basic Linux console experience is assumed. To edit files use either vi or nano. For details to configure a Raspberry Pi, refer to
headless setup from scratch for integration into home network: <https://www.raspberrypi.org/documentation/configuration/>.



## Step-by-step instructions


### Flashing Raspberry Pi OS to SD Card

* Get Raspberry Pi Imager from <https://www.raspberrypi.com/software/>
* Choose your device (e.g., Raspberry Pi 4)
* Select OS: Raspberry Pi OS Lite (64-bit)
* Set up initial config before writing: user/password, WLAN, enable SSH access
* Flash image to SD Card

#### Alternative option without imager

If you are not using the Imager, you need to setup initial config manually:
 
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

* If not set via Imager, the default password is _raspberry_
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
rpi-eeprom-update -a
reboot
```



* Install required software:

```
apt-get install git
apt-get install libpcap0.8
```

DEV NOTE: from here onwards, the documentation is outdated. Details re latest JDK etc will follow...


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

* Get and compile NOVA code

```
cd /home/pi
git clone https://bitbucket.org/sschubiger/nova.git
cd nova/scripts
./compile.sh
cd ..
cp -rp src/native bin/native
```


* Edit raspi_1x1.properties. Importantly, set the correct Ethernet interface for communication with NOVA (usually it will be eth0 on the Raspberry Pi):

```
nova=eth0
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

* If this is not the case, check your NOVA address jumper settings again.
