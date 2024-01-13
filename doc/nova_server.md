# NOVA Server Documentation

The NOVA Server is a Java application that controls the NOVA hardware by directly sending ethernet frames to the hardware via jnetpcap. In addition to controlling the NOVA hardware (composed of a number of 5x5x10 voxel NOVA modules), the NOVA Server provides a Web interface running on the default interface on port 80 to control the content playback. 

The server is started through the `main()` method of the `NOVAControl` class, and takes a single argument which points to the configuration file, for which details are outlined below.

## Software configuration

The configuration can be stored in a text file, e.g., `nova.properties`, and contains the following elements:

- `nova`: The ethernet interface used for communicating with NOVA. Default if omitted is `eth0`.
- `addr_<X>_<Y>`: The address (as configured by jumpers on the NOVA board, see below) of the module at module location (X,Y). Default if omitted is `addr_0_0 = 1`.
- `flip`: If `flip = true` then content will be vertically mirrored. Default if omitted is `false`.
- `brightness`: The initial brightness value in the range [0..1]. Default if omitted is `0.5`.
- `content`: A comma separated list of the names of content classes to load at startup. See “Writing a Content Extension” below for writing a
custom content extension. If `content` is set to `AUTO`, then all content classes will be loaded. Default if omitted is `AUTO`.
- `duration`: The number of seconds a content class should run until the server switches to the next content class. If set to `-1`, the selected content will run indefinitely or until changed by the user using the Web interface. Default if omitted is `-1`.
- `movies`: Path to voxel movies for the movie player. Default if omitted is `.`.

A sample configuration could look like this:

```
nova=eth1
addr_0_0 = 1
addr_0_1 = 5
addr_0_2 = 9
addr_0_3 = 13
addr_1_0 = 2
addr_1_1 = 6
addr_1_2 = 10
addr_1_3 = 14
addr_2_0 = 3
addr_2_1 = 7
addr_2_2 = 11
addr_2_3 = 15
addr_3_0 = 4
addr_3_1 = 8
addr_3_2 = 12
addr_3_3 = 16
flip=true
brightness=0.6 
content=Colorcube,Random,Movie,Sweep
duration=300
```

## Hardware address configuration

The modules derive their MAC address from jumpers on the board. The jumpers encode the least significant byte of the MAC address. Refer to `nova_jumpers.jpg` in this documentation for an example.

Generally, the server does not need to setup TCP/IP for the interface that communicates with the modules. However, the modules also set up their own IP address with the least significant 8 bits in the 192.168.1.0/24 subnet. This can be used for troubleshooting, e.g., for pinging the corresponding IP address to see if a module responds.


## Writing content extensions

The server loads content extensions as configured from a predefined package. Adding classes to this package makes them available to the server. A content extension must located in the `content` package and must inherit from the `Content` class. It needs to implement at least a constructor and the fillFrame() method. Below is the implementation of the `Content` class. For example:

```
package ch.bluecc.nova.content;

public class Sweep extends Content {
	/**
	 * Creates a content instance.
	 * 
	 * @param name The name of the content.
	 * @param dimI The X-dimension.
	 * @param dimJ The Y-dimension.
	 * @param dimK The Z-dimension.
	 * @param numFrames The number of frames to run.
	 */
	public Sweep(int dimI, int dimJ, int dimK, int numFrames) {
		super("Sweep", dimI, dimJ, dimK, numFrames);
	}

	/**
	 * Fill the frame.
	 */
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		final double dimK_1 = dimK - 1;
		// loop over all voxels 
		for(int k = 0; k < dimK; k++) {
			double dk = k / dimK_1;
			// compute value based on time from a sine curve
			float v = (float)Math.abs(Math.sin(dk * Math.PI + timeInSec));
			for(int i = 0; i < dimI; i++)
				for(int j = 0; j < dimJ; j++)
					setVoxel(rgbFrame, i, j, k, v, v, v);
		}
		// return true as long as we are allowed to playback frames
		return --frames > 0;
	}
}
```

Be aware that `fillFrame()` must complete in 40ms, otherwise a frame underrun will occur. If the content is too complex to render in real-time, it can be written to a flat file of RGB voxels and played back with the ch.bluecc.nova.content.Movie class.

For additional examples and further details, refer to the source code.

