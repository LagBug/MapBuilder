package me.lagbug.common.builders;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.bukkit.map.MapView.Scale;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MapBuilder {

    public static final String VERSION = "1.2";

    private MapView map;
    private BufferedImage image;
    private List<Text> texts;
    private MapCursorCollection cursors;
    private boolean rendered;
    private boolean renderOnce;

    public MapBuilder() {
        cursors = new MapCursorCollection();
        texts = new ArrayList<>();
        rendered = false;
        renderOnce = true;
    }

    /**
     * Get the image that's being used
     *
     * @return the image used
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Set and image to be used
     *
     * @param image the buffered image to use
     * @return the instance of this class
     */
    public MapBuilder setImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    /**
     * Set and image to be used
     *
     * @param x,   y the coordinates to add the text
     * @param font the font to be used
     * @param text the string that will be displayed
     * @return the instance of this class
     */
    public MapBuilder addText(int x, int y, MapFont font, String text) {
        this.texts.add(new Text(x, y, font, text));
        return this;
    }

    /**
     * Gets the list of all the texts used
     *
     * @return an array list of all the texts
     */
    public List<Text> getTexts() {
        return texts;
    }

    /**
     * Adds a cursor to the map
     *
     * @param x,        y the coordinates to add the cursor
     * @param direction the direction to display the cursor
     * @param type      the type of the cursor
     * @return the instance of this class
     */
    @SuppressWarnings("deprecation")
    public MapBuilder addCursor(int x, int y, CursorDirection direction, CursorType type) {
        cursors.addCursor(x, y, (byte) direction.getId(), (byte) type.getId());
        return this;
    }

    /**
     * Gets all the currently used cursors
     *
     * @return an array list of all the texts
     */
    public MapCursorCollection getCursors() {
        return cursors;
    }

    /**
     * Sets whether the image should only be rendered once.
     * Good for static images and reduces lag.
     *
     * @param renderOnce the value to determine if it's going to be rendered once
     * @return the instance of this class
     */
    public MapBuilder setRenderOnce(boolean renderOnce) {
        this.renderOnce = renderOnce;
        return this;
    }


    /**
     * Builds an itemstack of the map.
     *
     * @return an itemstack of the map containing what's been set from the above methods
     */
    @SuppressWarnings("deprecation")
    public ItemStack build() {
        ItemStack item = new ItemStack(Material.MAP);

        map = Bukkit.createMap(Bukkit.getWorlds().get(0));
        List<MapRenderer> old = map.getRenderers();

        map.setScale(Scale.NORMAL);
        map.getRenderers().forEach(map::removeRenderer);

        map.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                if (rendered && renderOnce) {
                    return;
                }

                if (player == null || !player.isOnline()) {
                    old.forEach(map::addRenderer);
                } else {
                    if (image != null) {
                        mapCanvas.drawImage(0, 0, image);

                    }
                    texts.forEach(text -> mapCanvas.drawText(text.getX(), text.getY(), text.getFont(), text.getText()));
                    mapCanvas.setCursors(cursors);
                    rendered = true;
                }
            }
        });

        if (Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14")) {
            MapMeta mapMeta = (MapMeta) item.getItemMeta();
            mapMeta.setMapId(getMapId(map));
            item.setItemMeta(mapMeta);
        } else {
            item.setDurability(getMapId(map));
        }

        return item;
    }

    /**
     * Gets a map id cross-version using reflection
     *
     * @param mapView the map to get the id
     * @return the instance of this class
     */
    private short getMapId(MapView mapView) {
        try {
            return (short) mapView.getId();
        } catch (NoSuchMethodError ex) {
            try {
                return (short) Class.forName("org.bukkit.map.MapView").getMethod("getId", (Class<?>[]) new Class[0])
                        .invoke(mapView, new Object[0]);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public enum CursorDirection {
        SOUTH(0), SOUTH_WEST_SOUTH(1), SOUTH_WEST(2), SOUTH_WEST_WEST(3), WEST(4), NORTH_WEST_WEST(5), NORTH_WEST(6),
        NORTH_WEST_NORTH(7), NORTH(8), NORTH_EAST_NORTH(9), NORTH_EAST(10), NORTH_EAST_EAST(11), EAST(12),
        SOUTH_EAST_EAST(13), SOUNT_EAST(14), SOUTH_EAST_SOUTH(15);

        private final int id;

        private CursorDirection(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    public enum CursorType {
        WHITE_POINTER(0), GREEN_POINTER(1), RED_POINTER(2), BLUE_POINTER(3), WHITE_CLOVER(4), RED_BOLD_POINTER(5),
        WHITE_DOT(6), LIGHT_BLUE_SQUARE(7);

        private final int id;

        private CursorType(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }
}

class Text {

    private int x;
    private int y;
    private MapFont font;
    private String string;

    public Text(int x, int y, MapFont font, String text) {
        setX(x);
        setY(y);
        setFont(font);
        setString(text);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public MapFont getFont() {
        return font;
    }

    public void setFont(MapFont font) {
        this.font = font;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
