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

import java.io.IOException;
import java.io.OutputStream;

import de.nx42.maps4cim.config.texture.ColorDef;
import de.nx42.maps4cim.map.TextureMap;
import de.nx42.maps4cim.map.ex.MapGeneratorException;
import de.nx42.maps4cim.map.texture.data.Texture;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public class SingleTexture extends TextureMap {

    protected int groundTexture;

    public SingleTexture() {
        this.groundTexture = Texture.GRASS.draw();
    }

    public SingleTexture(ColorDef groundTexture) {
        this.groundTexture = Texture.draw(groundTexture);
    }

    @Override
    public int[][] generateTexture() {
        int[][] map = new int[edgeLength][edgeLength];
        for (int y = 0; y < edgeLength; y++) {
            int[] line = map[y];
            for (int x = 0; x < edgeLength; x++) {
                line[x] = groundTexture;
            }
        }
        return map;
    }

    public static void write(OutputStream out) throws MapGeneratorException, IOException {
        TextureMap simple = new SingleTexture();
        simple.writeTo(out);
    }

}
