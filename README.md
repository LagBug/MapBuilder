# MapBuilder
As you might know, maps are an awesome and unique way to display images or text. In this resource, you're going to learn how to do it through my utility class and you're also going to be given the full utility class containing some really useful methods. Spigot thread: https://www.spigotmc.org/threads/mapbuilder.391888/

## Prerequisites
- Java 8 or above.
- Spigot 1.14 or above (But it will work on older version as well)

## Why
- Easy to use. This utility class basically provides you with easy to use methods to easily build and display your maps
- No need for players to right click to initialize the map. And no need to use MapInitializeEvent or create custom renderer classes.
- Ability to add custom images either from the web or from file.
- Ability to easily set any text you want.
- Ability to set cursors with ease.
- For the three above features custom enums, etc. have been added to make this really simple.
- Cross-version compatible. This can be used from 1.8 to 1.14 with no errors.
- Really good for static (non-updating) images.

## Usage
This is really simple to use and will return an ItemStack which you can then use to give it to a player for example. In this example, a map containing an image from the web is given to all online players.

```java
try {
    ItemStack item = new MapBuilder() //Initializing the MapBuilder class
    	.setRenderOnce(true) //Since this will be a static image, we only want it rendered once
	.setImage(ImageIO.read(new URL("https://site.com/image.png"))) //Setting an image from a URL as background
        .addText(0, 0, MinecraftFont.Font, "Hello there") //Adding some text with the Minecraft default font at 0, 0
        .addCursor(20, 20, CursorDirection.EAST, CursorType.WHITE_DOT) //Adding a cursor (in our case a white dot) to the map
	.build(); //Finally using the build method to generate an ItemStack 
   
    Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(item)); //Looping through all the online players and adding the ItemStack to their inventory
} catch (IOException e) { //Exception thrown if the URL provided is invalid
    e.printStackTrace();
}
```

## Result
You can really easily archive something like this
![alt text](https://i.ibb.co/qNnqC6C/Screenshot-1.png)
