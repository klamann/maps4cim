
# `*.map`-File Format

maps4cim generates map files that can be parsed by Cities in Motion 2.
Unfortunately, the developers of CiM 2 did not intend to give access to map
files for modders, and therefore no information about the format of these 
files is officially available. Still, using a hex editor, some patience and a
lot of time, some of the mysteries were uncovered...

A map file consists of four sections, which may be called

* Header: Contains metadata like the map's name, dates, timers and the prieview image
* Elevations: Definition of the height at each control point of the map
* Ground Texture: Definition of the ground texture at each control point of the map
* Game Objects: All objects that can be placed on the map (trees, buildings, roads, rails, ...)

## Header

### Datatypes

* String
  - every String starts with two 0-bytes
  - followed by the length of the String as a single (unsigned) byte
  - then the ASCII-chars, where each char is preceded by a single 0-byte
  - therefore, String length is always: 2 * count(char) + 3
  - example: `00 00 04 00 74 00 65 00 73 00 74` represents "test".  
    example explained: two 0-bytes, followed by `04` (length) and then `00` and
	char pairs.
* Date & Timers
  - Date: .net 64 bit DateTime ("Ticks"),
	see [DateTime](http://msdn.microsoft.com/en-us/library/system.datetime.aspx)
  - Timer: .net 64b bit TimeSpan ("Ticks"),
	see [TimeSpan](http://msdn.microsoft.com/en-us/library/system.timespan.aspx)

### Analysis

This is a description of the header contents of a regular map using the default
american building set (see next section for european cities).

	Start	Len		Type		Desc
	----------------------------------------------------------------------------------------------
	0   	7		static		Hex: fd 77 fc b6 e8 fe fe 
								in campaign maps, only the last 2 bytes are contained (fe fe)
								except for the tutorial map: fd 77 fc 49 24 fe fe
								--> other values (except byte 4,5) cause the map not to load, 
									but value is set to default after next save anyway
	7		63		String		len 30: "GameState+SerializableMetaData"
	70		4		?			might contain 4 bytes: 77 EE 64 1C
								--> does not break anything if added, cleared after next save
	70		4		gap			empty --> breaks the map if missing
	74		8		Date		static date: 08 cf fc c6 0a f3 c0 00
								-> 635004000000000000 --> 2013-04-01 08:00:00
								April fools? wtf?!
								campaign maps: 08 cf fc e7 92 05 60 00 --> 2013-04-01 12:00:00
	82		8		Date		same date as above
								--> changes to both of those are lost on next save
	90		8		Date		date when the map was last saved
	98		8		Date		date when the map was created
								--> one of these two dates does not exist in campaign maps
	106		8		Time		time worked on map --> nonexistent on first save!
	114		8		Time		slightly different time as above, usually a bit longer,
								but also seen shorter... distinction not clear!
								--> one of these two timers does not exist in campaign maps
	122		5		static		Hex: 00 00 01 fe fe
								--> necessary (pro tip: crash the game by flipping a bit ;D)
	127		23		String		len 10: "PlayerData"
	150		var		String		len varying: user defined map name
	--- index reset: relative to the end of the filename ---
	0		3		int24		PNG length in bytes (3 bytes, little endian)
	3		var		binary		Map overview image, encoded as PNG
								start: 89 50 4e 47 0d 0a 1a 0a ("PNG")
								end: 00 00 00 00 49 45 4e 44 ae 42 60 82 ("IEND")
	--- index reset: relative to the end of the PNG ---
	0		21		?			content unknown; maybe just some random magic numbers...
								ff 00 64 00 64 00 00 00 00 00 00 00 00 64 00 64 00 64 01 00 00
								  |-    Palindrome ;)    >x<                     -|
								for campaign maps this is just: ff 01
	21		29		String		len 13: "Editor Player"
	50		34		?			Empty
	84		11		static		Hex: ff ff 00 00 ff 00 00 ff 00 fe fe
	95		25		String		len 11: "CompanyData"
	122		31		String		len 14: "Editor Company"
	153		var		gap			empty until next free index % 4096 = 0
	--- index reset: relative to the next free index % 4096 == 0 ---
	0   	7		static		Hex: fd 77 fc b6 e8 fe fe 
								for campaign maps, this is: fe fe
	7		69		String		len 33: "GameState+SerializableTerrainData"
	76		3		static		Hex: 00 00 04
	79		104		String		multiple Strings, one after another:
								"Grass", "Rough Grass", "Mud", "Dirt", "Ruined", "Cliff", "Pavement"
	105		4		static		Hex: 00 00 08 01
	109		147		gap			Empty
								Campaign maps might contain after a short gap: 7f db bf f8
	--- index reset: 256 byte after the start of the previous section / last index reset ---
	0							Elevation

#### European Cities

These are the differences in the header section for maps using the europen
building set, which was introduced with the European Cities DLC.

	Start	Len		Type		Desc
	----------------------------------------------------------------------------------------------
	0   	7		static		Hex: fd 77 fd c9 84 fe fe
								(fd c9 84 instead of fc b6 e8)
	(continue to next index reset)
	--- index reset: relative to the end of the filename ---
	0		6		String		len 6: "EnvX14"
	(continue with PNG)
	--- index reset: relative to the end of the PNG ---
	0		21		?			(same static content)
	21		25		String		len 11: "cim2.europe"
	46		4		static		Hex: 00 04 53 8a
	(continue with "Editor Player")
	--- index reset: relative to the next free index % 4096 == 0 ---
	0   	7		static		Hex: fd 77 fd c9 84 fe fe
								(fd c9 84 instead of fc b6 e8)
	(continue; end of diff)

To sum up the changes for european building styles:

* slightly different intro
* static Strings "EnvX14" and "cim2.europe" (plus 4 bytes of data)

## The grid

Some common facts about the map and how elevations and the type of ground
texture are stored:

* each map represents a fixed area of 8×8 km or 64 km²
* it is divided into a grid of 2048×2048 equally sized fields (4×4m each)
* each node (crossing between edges) of the grid stores a height value
* each surface (plane surrounded by edges) is associated with a certain ground
  texture
* the values in between are smoothly interpolated
* all values are stored in two matrices, one for the elevations, then one for
  the ground texture, right after the header, without any metadata

Keep reading for details on how data about elevations and textures is stored.

### Elevations

Right after the header, the values of the map's elevations follow. For each node
of the map's grid with 2048×2048 fields, a height value is stored, ranging from
-1048.575 to +1048.576 meters. Each value below 0m is considered to be water,
though actually water is only displayed starting with heights below about -15m.

Internally, height values are stored as 32bit signed integers (little endian),
rounded to full millimeters (e.g. a value of 123500 would stand for a height
of 123.5m). Values that exceed the allowed range are ignored (capped to the
highest/lowest allowed value).

As there is always exactly one more row and column of nodes than there are
fields on a grid, there are 2049*2049 = 4198401 height values. With each value
stored as 32 bit integer, this results in an array size of 16793604 byte or
about 16 MB.

The 2D-projection of the array reads from left to right and bottom to top or
southwest to northeast (for those who know what
[Row-major order](https://en.wikipedia.org/wiki/Row-major_order) is: Rows are 
the same, columns are inverted). Each control point is located in the bottom
left or southwest corner of a field, except for those in the last (2049th) row
or column, which do not have a field to the top right. 

Drawbacks that are imposed by this design:

* no water above 0m. This means no rivers and no lakes above sea level
* only one height per surface square. This means no caves or overhangs of
  any kind

### Ground Texture

CiM 2 knows one base texture (grass) and three overlay textures (dirt, mud, rough grass):

![Grass](https://raw.githubusercontent.com/Klamann/maps4cim/master/maps4cim-gui/src/main/resources/de/nx42/maps4cim/res/img/texture-grass.png)
![Dirt](https://raw.githubusercontent.com/Klamann/maps4cim/master/maps4cim-gui/src/main/resources/de/nx42/maps4cim/res/img/texture-dirt.png)
![Mud](https://raw.githubusercontent.com/Klamann/maps4cim/master/maps4cim-gui/src/main/resources/de/nx42/maps4cim/res/img/texture-mud.png)
![Rough Grass](https://raw.githubusercontent.com/Klamann/maps4cim/master/maps4cim-gui/src/main/resources/de/nx42/maps4cim/res/img/texture-roughgrass.png)  
*From left to right: Grass, Dirt, Mud, Rough Grass*

Additionally, there are two special types: pavement, which always covers any
of the regular textures, and black or void, which is not really a texture
rather than the absence of a texture, a gap in the map with no particular use...

Grass is always the default ground texture. The three overlay textures
can be mixed with grass to define the new ground texture for a given control
point. If one of them is assigned an opacity of 100%, it replaces the grass
texture entirely. Multiple textures may be mixed, though their cumulated
opacity should not exceed 100% or strange textures may be generated
(worst case: dirt, mud and rough grass @ 100% create a bright pink color).

Each data point consists of a 32 bit value (4 bytes), with the first
3 bytes standing for the opacity (0-255) of the three textures dirt, mud and
rough grass (in this order). The fourth byte describes 3 different textures:
black (0-62), grass (63-166) and pavement (167-255).

In maps4cim, textures can be represented using a hex triplet, as known from web
colors (e.g. `ff0000` stands for 100% dirt, 0% mud, 0% rough grass).

As the control points for the texture color are located in the center of a
field, a matrix of exactly 2048×2048 = 2^22 entries is required.  With each
value stored as 32 bit word, this results in an array size of 2^24 byte or
exactly 16 MB.

Drawbacks that are imposed by this design:

* Choice between four (quite similar) ground textures is rather limited,
  even when mixtures are allowed
* Only one color per 16m²-square does not allow for detailed paintings on the
  ground
* No further ground textures can be expected to appear in the game, as this
  would break the map file format
* Together with the height values, each map has a minimum size of more
  than 32MB, even when it's empty (data compression, anyone?)

## Game Objects

After the texture matrix, the game objects (roads, buildings, vegetation, ...)
are stored.

As everything else in the map file, all game objects are stored in a
proprietary binary format. Reverse engineering the header and finding out how
the elevations and the texture map work were hard enough. But this is a black
box, which prevents maps4cim from placing all the pretty stuff that can be
extracted from the OpenStreetMap and other sources on the map.

Moreover, it was a conscious choice of the developers, not to provide any
resources for modders to work with map files and therefore this section will
remain empty. Creating a new, better version of CiM 2 would be easier than
reverse engineering everything that is hidden in here.
