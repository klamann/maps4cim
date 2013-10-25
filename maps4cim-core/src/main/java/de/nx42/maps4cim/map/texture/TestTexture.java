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

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class TestTexture extends TextureMap {


    @Override
    public int[][] generateTexture() {
        return allMaxStripes();
    }

    public int[][] allMaxStripes() {
        int[][] map = new int[edgeLength][edgeLength];
        for (int y = 0; y < edgeLength; y++) {
            int[] line = map[y];
            for (int x = 0; x < edgeLength; x++) {
                line[x] = CiMTexture.GRASS.draw();

                if(x < 2030 && y < 12) {
                    line[++x] = CiMTexture.GRASS.draw();
                    line[++x] = CiMTexture.ROUGH_GRASS.draw(0.5f);
                    line[++x] = CiMTexture.ROUGH_GRASS.draw();
                    line[++x] = CiMTexture.DIRT.draw(0.5f);
                    line[++x] = CiMTexture.DIRT.draw();
                    line[++x] = CiMTexture.MUD.draw(0.5f);
                    line[++x] = CiMTexture.MUD.draw();
                    line[++x] = CiMTexture.PAVEMENT.draw();
                    line[++x] = CiMTexture.PAVEMENT.draw();
                    line[++x] = CiMTexture.BLACK.draw();

//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.ROUGH_GRASS.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());
//                    line[++x] = CiMTexture.mix(CiMTexture.PAVEMENT.draw(), CiMTexture.DIRT.draw(), CiMTexture.MUD.draw());

                    line[++x] = CiMTexture.MIX_DARK_MUD.draw(0.5f);
                    line[++x] = CiMTexture.MIX_DARK_MUD.draw();
                    line[++x] = CiMTexture.MIX_DARK_DIRT.draw(0.5f);
                    line[++x] = CiMTexture.MIX_DARK_DIRT.draw();
                    line[++x] = CiMTexture.MIX_PINK.draw(0.5f);
                    line[++x] = CiMTexture.MIX_PINK.draw();
                    line[++x] = CiMTexture.MIX_MAGENTA.draw(0.5f);
                    line[++x] = CiMTexture.MIX_MAGENTA.draw();
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

                line[x] = CiMTexture.GRASS.draw(hue / 255f);

                if(hue <= 255) {
                    line[++x] = CiMTexture.GRASS.draw(hue / 255f);

                    line[++x] = CiMTexture.MIX_MAGENTA.draw();

                    if (hue % 5 == 0) {
                        line[++x] = CiMTexture.MIX_PINK.draw();
                    }
                    if (hue % 10 == 0) {
                        line[++x] = CiMTexture.MIX_PINK.draw();
                    }
                    if (hue % 50 == 0) {
                        line[++x] = CiMTexture.MIX_PINK.draw();
                    }

                    hue++;
                }
            }

        }
        return map;
    }

}
