/**
 * maps4cim - a real world map generator for CiM 2
 * Copyright 2013 Sebastian Straub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nx42.maps4cim.map.texture;

import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.texture.data.Texture;

/**
 * just some ground texture for test purposes
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
@Deprecated
public class TestTexture extends TextureMap {


    @Override
    public int[][] generateTexture() {
        return allColorCombinations();
    }

    public int[][] allColorCombinations() {
        int[][] map = new int[edgeLength][edgeLength];

        // base
        for (int y = 0; y < edgeLength; y++) {
            for (int x = 0; x < edgeLength; x++) {
                map[y][x] = Texture.GRASS.draw();
            }
        }

        int nextY = 0;

        for (int i = 0; i < Texture.values().length; i++) {
            Texture tex = Texture.values()[i];

            for (int y = i * 15; y < (i+1) * 15; y++) {
                for (int x = 0; x < edgeLength; x++) {
                    int val = x / 7;
                    boolean xGap = (x % 7) == 0 || (x % 7) == 1 || (x % 7) == 2;
                    boolean yGap = y < (i * 15) + 5;
                    if(!xGap && !yGap && val <= 255) {
                        map[y][x] = tex.draw(val / 255f);
                    }
                    nextY = y;
                }
            }
        }

        nextY += 10;


        for (int y = nextY; y < nextY+512; y++) {
            for (int x = 10; x < 522; x++) {
                int xVal = (x-10) / 2;
                int yVal = (y-nextY) / 2;

                map[y][x] = Texture.draw(0, xVal, yVal);
                if(xVal + yVal == 256) {
                    map[y][x] = Texture.draw(255, 0, 0);
                }
            }
        }

        for (int y = nextY; y < nextY+512; y++) {
            for (int x = 532; x < 532 + 512; x++) {
                int xVal = (x-532) / 2;
                int yVal = (y-nextY) / 2;

                map[y][x] = Texture.draw(xVal, 0, yVal);
                if(xVal + yVal == 256) {
                    map[y][x] = Texture.draw(0, 255, 0);
                }
            }
        }

        for (int y = nextY; y < nextY+512; y++) {
            for (int x = 1054; x < 1054 + 512; x++) {
                int xVal = (x-1054) / 2;
                int yVal = (y-nextY) / 2;

                map[y][x] = Texture.draw(xVal, yVal, 0);
                if(xVal + yVal == 256) {
                    map[y][x] = Texture.draw(0, 0, 255);
                }
            }
        }
        
        


        return map;
    }

    public int[][] allMaxStripes() {
        int[][] map = new int[edgeLength][edgeLength];
        for (int y = 0; y < edgeLength; y++) {
            int[] line = map[y];
            for (int x = 0; x < edgeLength; x++) {
                line[x] = Texture.GRASS.draw();

                if(x < 2030 && y < 12) {
                    line[++x] = Texture.GRASS.draw();
                    line[++x] = Texture.ROUGH_GRASS.draw(0.5f);
                    line[++x] = Texture.ROUGH_GRASS.draw();
                    line[++x] = Texture.DIRT.draw(0.5f);
                    line[++x] = Texture.DIRT.draw();
                    line[++x] = Texture.MUD.draw(0.5f);
                    line[++x] = Texture.MUD.draw();
                    line[++x] = Texture.PAVEMENT.draw();
                    line[++x] = Texture.PAVEMENT.draw();
                    line[++x] = Texture.BLACK.draw();

//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.PAVEMENT.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());

                    line[++x] = Texture.MIX_DARK_MUD.draw(0.5f);
                    line[++x] = Texture.MIX_DARK_MUD.draw();
                    line[++x] = Texture.MIX_DARK_DIRT.draw(0.5f);
                    line[++x] = Texture.MIX_DARK_DIRT.draw();
                    line[++x] = Texture.MIX_PINK.draw(0.5f);
                    line[++x] = Texture.MIX_PINK.draw();
                    line[++x] = Texture.MIX_MAGENTA.draw(0.5f);
                    line[++x] = Texture.MIX_MAGENTA.draw();
                }
            }
        }
        return map;
    }

    public int[][] stripesAllSat() {
        int[][] map = new int[edgeLength][edgeLength];
        for (int y = 0; y < edgeLength; y++) {
            int[] line = map[y];

            int hue = 0;
            for (int x = 0; x < edgeLength; x++) {

                line[x] = Texture.GRASS.draw(hue / 255f);

                if(hue <= 255) {
                    line[++x] = Texture.GRASS.draw(hue / 255f);

                    line[++x] = Texture.MIX_MAGENTA.draw();

                    if (hue % 5 == 0) {
                        line[++x] = Texture.MIX_PINK.draw();
                    }
                    if (hue % 10 == 0) {
                        line[++x] = Texture.MIX_PINK.draw();
                    }
                    if (hue % 50 == 0) {
                        line[++x] = Texture.MIX_PINK.draw();
                    }

                    hue++;
                }
            }

        }
        return map;
    }

}
