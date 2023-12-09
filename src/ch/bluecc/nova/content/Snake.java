package ch.bluecc.nova.content;

import java.util.Arrays;
import java.util.Random;

public class Snake extends Content {
	private float[][][] volume;
	private Random      rnd = new Random();
	private Snake_[]    snakes;
	
	public Snake(int dimI, int dimJ, int dimK, int numFrames) {
		super("Snake", dimI, dimJ, dimK, numFrames); //$NON-NLS-1$
		volume = new float[dimI+2][dimJ+2][dimK+2];
		for(int i = 0; i < volume.length; i++)
			for(int j = 0; j < volume[i].length; j++)
				Arrays.fill(volume[i][j], 1f);
		
		for(int i = 0; i < dimI; i++)
			for(int j = 0; j < dimJ; j++)
				for(int k = 0; k < dimK; k++)
					volume[i+1][j+1][k+1] = 0f;							
		
		snakes = new Snake_[Math.max(dimJ, dimI) / 2];
		for(int i = 0; i < snakes.length; i++)
			snakes[i] = new Snake_();
	}

	private boolean isFree(int i, int j, int k) {
		return volume[i+1][j+1][k+1] < 0.2f;
	}
	
	class Snake_ {
		static final int DIE_FRAMES = 50;
		
		int[][] trace = new int[30][3];
		
		int i;
		int j;
		int k;

		int di;
		int dj;
		int dk;

		int p;
		
		float[] rgb = new float[3];

		double  lastTime;
		double  phase;
		int     dying;
		
		Snake_() {
			init();
		}
		
		void updateDirection() {
			di = 0;
			dj = 0;
			dk = 0;
			switch(rnd.nextInt(3)) {
			case 0: di = rnd.nextBoolean() ? -1 : 1; break;
			case 1: dj = rnd.nextBoolean() ? -1 : 1; break;
			case 2: dk = rnd.nextBoolean() ? -1 : 1; break;
			}			
		}
		
		void move() {
			for(int t = 0; t < 20; t++) {
				int ti = i + di;
				int tj = j + dj;
				int tk = k + dk;
				
				if(isFree(ti, tj, tk)) {
					i = ti;
					j = tj;
					k = tk;
					claim(i, j, k);
					return;
				}
				
				updateDirection();
			}
			
			dying = DIE_FRAMES;
		}

		private void claim(int i, int j, int k) {
			volume[trace[p][0]][trace[p][1]][trace[p][2]] = 0;
			volume[i+1][j+1][k+1] = 1;
			trace[p++] = new int[] {i+1, j+1, k+1};
			if(p >= trace.length) p = 0;
		}

		private void init() {
			setRGBfromHSV(rnd.nextFloat(), 1f, 1f, rgb, 0);
			i = rnd.nextInt(dimI);
			j = rnd.nextInt(dimJ);
			k = rnd.nextInt(dimK);
			for(int[] p : trace) {
				volume[p[0]][p[1]][p[2]] = 0;
				p[0] = 0;
				p[1] = 0;
				p[2] = 0;
			}
			phase = rnd.nextDouble();
		}

		void run(float[] rgbFrame, double timeInSec) {			
			timeInSec += phase;
			
			if(timeInSec > lastTime + 0.5) {
				lastTime = timeInSec;
				
				if(dying == 0)
					move();
				
				for(int[] p : trace)
					volume[p[0]][p[1]][p[2]] *= 0.90f;
			}
			for(int[] p : trace) {
				final float w = volume[p[0]][p[1]][p[2]];
				if(p[0] > 0 && p[1] > 0 && p[2] > 0)
					addVoxelClamp(rgbFrame, p[0]-1, p[1]-1, p[2]-1, w*rgb[0], w*rgb[1], w*rgb[2]);
			}
			
			if(dying > 0) {
				final float r    = (float)Math.sin((Math.PI * dying) / DIE_FRAMES) * 3.5f;
				final int   minX = Math.max(0,    (int) Math.floor(i-r));
				final int   maxX = Math.min(dimI, (int) Math.ceil (i+r));
				final int   minY = Math.max(0,    (int) Math.floor(j-r));
				final int   maxY = Math.min(dimJ, (int) Math.ceil (j+r));
				final float r2   = r * r;
				
				for(int x = minX; x < maxX; x++) {
					final float dx = x - i;
					for(int y = minY; y < maxY; y++) {
						final float dy = y - j;
						for(int z = 0; z < dimK; z++) {
							final float dz = z - k;
							final float d = dx * dx + dy * dy + dz * dz;
							final float b = (r2- d) / r2;
							if(b > 0f)
								addVoxelClamp(rgbFrame, x, y, z, b * rgb[0], b * rgb[1], b * b * rgb[2]);						
						}	
					}			
				}	
				dying--;
				if(dying == 0)
					init();
			}
		}
		
	}
	
	@Override
	public boolean fillFrame(float[] rgbFrame, double timeInSec) {
		Arrays.fill(rgbFrame, 0f);
		
		for(Snake_ snake : snakes) 
			snake.run(rgbFrame, timeInSec);
				
		return --frames > 0;
	}
}
