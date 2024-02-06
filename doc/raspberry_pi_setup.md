# NOVA Raspberry Pi setup

This document provides step-by-step instructions to set up a Raspberry Pi in headless mode with the NOVA software and integrate it into a home WLAN environment.

Basic Linux console experience is assumed. To edit files use either vi or nano. For details to configure a Raspberry Pi, refer to
headless setup from scratch for integration into home network: <https://www.raspberrypi.org/documentation/configuration/>.



## Step-by-step instructions


### Flash Raspberry Pi OS to SD Card

* Get Raspberry Pi Imager from <https://www.raspberrypi.com/software/>
* Choose your device (e.g., Raspberry Pi 4)
* Select OS: Raspberry Pi OS Lite (64-bit) (use latest, currently Debian Bookworm)
* Set up initial configuration before writing: host name, user/password, WLAN, timezone, enable SSH access. **Important:** make sure to get this configuration right, otherwise you will not be able to connect to your Raspberry Pi after you boot it for the first time. For the rest of this document, `nova` is assumed as host name -- feel free to use another name of your choice.
* Flash image to SD Card
* Put SD Card into Raspberry Pi


### Plugin your Raspberry Pi and wait until boot is complete

* From your machine, ssh to nova.local:

```
ssh pi@nova.local
```

* In case hostname cannot be resolved, find the Raspberry Pi's IP address on your router and connect with ssh using the IP address.

* Run the Raspberry Pi configuration utility. Optional: this step allows you to change additional configuration parameters as required by your home setup. You can also use this in case you later move your Raspberry Pi to a different WLAN or if the WLAN password changes.

```
sudo raspi-config
```


### Update and install software

* Update the Raspberry Pi:

```
sudo apt update
sudo apt full-upgrade
sudo reboot
```

* Install required software:

```
sudo apt-get install git
sudo apt-get install libpcap0.8
sudo apt-get install maven
```


### Install OpenJDK 21 or later

Note: once OpenJDK 21 or later becomes available via apt-get, you can install the package via apt-get, and skip to the next section.

* Get latest OpenJDK package via wget: go to https://jdk.java.net, select JDK 21 or later ("Ready for Use"), and copy link to Linux/AArch64 `.tar.gz` package.
* In terminal on your Raspberry Pi, issue commands (make sure to update latest link and package name)

```
wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-aarch64_bin.tar.gz
tar -xzf tar xzf openjdk-21.0.2_linux-aarch64_bin.tar.gz
```

* This will result in your JDK being unpacked in your home directory at `/home/pi/jdk-21.0.2` You will need this path later for the automatic startup. Again, the exact path will be different for later JDK versions.


### NOVA software setup and configuration

* Get and compile NOVA code

```
cd /home/pi
git clone https://github.com/schubige/nova.git
cd nova
export JAVA_HOME=/home/pi/jdk-21.0.2/
mvn install
```

* Edit `configs/nova.properties`. Importantly, set the correct Ethernet interface and the correct module setup for communication with NOVA. Typically this will be as follows (depending on jumper setting on your module, see [here](nova_control.md)).

```
nova=eth0
addr_0_0=1
```


### Configure startup script and reboot

* Edit /etc/rc.local (e.g. `sudo nano /etc/rc.local`), add (before `exit 0`):

```
export JAVA_HOME=/home/pi/jdk-21.0.2/
cd /home/pi/nova
./scripts/novaraspi.sh > /dev/null 2>&1 &
```

* Plug in NOVA via ethernet and reboot

```
sudo shutdown -r now
```

* After 10 seconds, the random lights should go on
* Connect to web interface via your web browser: http://nova.local
