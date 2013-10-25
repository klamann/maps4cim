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

import de.nx42.maps4cim.config.texture.ColorDef;

/**
 *
 * @author Sebastian Straub <sebastian-straub@gmx.net>
 */
public enum CiMTexture {

    // background
    GRASS(24) {
        @Override
        public int draw() {
            return base;
        }
        @Override
        public int draw(float opacity) {
            if(opacity <= 0.0 || opacity >= 1.0) {
                return base;
            } else {
                return (Math.round(opacity * 65) + 85) << 24;
            }
        }
    },

    // base textures
    ROUGH_GRASS(0),
    DIRT(8),
    MUD(16),

    // special textures
    PAVEMENT(24) {
        @Override
        public int draw() {
            return 0xFF000000;
        }
        @Override
        public int draw(float opacity) {
            if(opacity <= 0.0) {
                return base;
            } else if(opacity >= 1.0) {
                return 0xFF000000;
            } else {
                return (Math.round(opacity * 88) + 167) << 24;
            }
        }
    },
    BLACK(24) {
        @Override
        public int draw() {
            return 0x00000000;
        }
        @Override
        public int draw(float opacity) {
            if(opacity <= 0.0) {
                return base;
            } else if(opacity >= 1.0) {
                return 0x00000000;
            } else {
                return Math.round((1.0f - opacity) * 62) << 24;
            }
        }
    },

    
    // combinations
    
    
    /*
     * the following textures can contain combinations that cannot be created
     * using the ingame map editor. Handle with care.
     */
    MIX_DARK_MUD(0) {
        @Override
        public int draw() {
            return draw(255, 0, 255);
        }
        @Override
        public int draw(float opacity) {
            return draw(opacity, 0, opacity);
        }
    },
    MIX_DARK_DIRT(0) {
        @Override
        public int draw() {
            return draw(255, 255, 0);
        }
        @Override
        public int draw(float opacity) {
            return draw(opacity, opacity, 0);
        }
    },
    MIX_PINK(0) {
        @Override
        public int draw() {
            return draw(0, 255, 255);
        }
        @Override
        public int draw(float opacity) {
            return draw(0, opacity, opacity);
        }
    },
    MIX_MAGENTA(0) {
        @Override
        public int draw() {
            return 0x90FFFFFF;
        }
        @Override
        public int draw(float opacity) {
            return draw(opacity, opacity, opacity);
        }
    };

    
    protected static final int base = 0x90 << 24;

    protected int offset;

    CiMTexture(int offset) {
        this.offset = offset;
    }


    public int draw() {
        return base | (0xFF << offset);
    }

    public int draw(float opacity) {
        int byteValue = floatToByte(opacity);
        return base | (byteValue << offset);
    }


    public static int floatToByte(float opacity) {
        int byteValue = Math.round(opacity * 255);

        if(byteValue > 255) {
            return 255;
        }
        if(byteValue < 0) {
            return 0;
        }
        return byteValue;
    }

    public static int draw(int roughGrass, int mud, int dirt) {
        return roughGrass | mud << 8 | dirt << 16 | base;
    }

    public static int draw(int base, int roughGrass, int mud, int dirt) {
        return roughGrass | mud << 8 | dirt << 16 | base << 24;
    }

    public static int draw(float roughGrass, float mud, float dirt) {
        return floatToByte(roughGrass)
             | floatToByte(mud)  << 8
             | floatToByte(dirt) << 16
             | base;
    }

    public static int draw(float base, float roughGrass, float mud, float dirt) {
        return floatToByte(roughGrass)
             | floatToByte(mud)  << 8
             | floatToByte(dirt) << 16
             | floatToByte(base) << 24;
    }

    public static int mix(int texture1, int texture2) {
        return texture1 | texture2;
    }

    public static int mix(int texture1, int texture2, int texture3) {
        return texture1 | texture2 | texture3;
    }



    public static int draw(ColorDef def, float alpha) {
        if(def==null) {
            return GRASS.draw();
        } if(def.black != null && def.black > 0.0) {
            return BLACK.draw(def.black.floatValue() * alpha);
        } else if(def.pavement != null && def.pavement > 0.0) {
            return PAVEMENT.draw(def.black.floatValue() * alpha);
        } else {
            return draw(
                    def.getSafeRoughGrass(),
                    def.getSafeMud(),
                    def.getSafeDirt());
        }
    }

    public static int draw(ColorDef def) {
        return draw(def, 1.0f);
    }

}
