
# `*.map`-File Format

maps4cim generates map files that can be parsed by Cities in Motion 2.
Unfortunately, the developers of CiM 2 did not intend to give access to map
files for modders, and therefore no information about the format of these 
files is officially available. Still, using a hex editor, some patience and a
lot of time, some of the mysteries were uncovered...

A map file consists of four sections, which may be called

* Header: Contains metadata like the map's name, dates, timers and the prieview image
* Elevation: Definition of the height of each control point of the map
* Ground Texture: Definition of the ground texture that is attached to each control point of the map
* Game Objects: All objects that can be placed on the map (trees, buildings, roads, rails, ...)

## Header

### Datatypes

* String
  - every String starts with two 0-bytes
  - followed by the length of the String in a single (unsigned) byte
  - then the ASCII-chars, where each char is preceded by a single 0-byte
  - therefore, String length is always: 2 * count(char) + 3
  - example: 00 00 04 00 74 00 65 00 73 00 74 represents "test".
  - example explained: two 0-bytes, followed by 04 (length) and then 00 and char pairs.
* Date & Timers
  - Date: .net 64 bit DateTime ("Ticks"),
	see [DateTime](http://msdn.microsoft.com/en-us/library/system.datetime.aspx)
  - Timer: .net 64b bit TimeSpan ("Ticks")
	see [TimeSpan](http://msdn.microsoft.com/en-us/library/system.timespan.aspx)

### Analysis

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
								but also seen shorter... distiction not clear!
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
	--- index reset: relative to the next free index % 4096 = 0 ---
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

### European Cities

Differences for European maps

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
	--- index reset: relative to the next free index % 4096 = 0 ---
	0   	7		static		Hex: fd 77 fd c9 84 fe fe
								(fd c9 84 instead of fc b6 e8)
	(continue; end of diff)

To sum up the changes for european building styles:
* slightly different intro
* static Strings "EnvX14" and "cim2.europe" (plus 4 bytes of data)

## Elevation

...

## Ground Texture

...

## Game Objects

...
