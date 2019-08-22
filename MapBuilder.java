package me.lagbug.captchax.utils;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

public class MapBuilder {

    private MapView map;
    private BufferedImage image;
    private List<Text> texts;
    private MapCursorCollection cursors;
    private boolean rendered;

    public MapBuilder() {
        cursors = new MapCursorCollection();
        texts = new ArrayList<>();
        rendered = false;
    }

    public MapBuilder withImage(BufferedImage image) {
        this.image = image;
        return this;
    }

    public MapBuilder addText(int x, int y, MapFont font, String text) {
        this.texts.add(new Text(x, y, font, text));
        return this;
    }

    @SuppressWarnings("deprecation")
    public MapBuilder addCursor(int x, int y, CursorDirection direction, CursorType type) {
        cursors.addCursor(x, y, (byte) direction.getId(), (byte) type.getId());
        return this;
    }

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
                if (rendered) {
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
                return;
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

    private int x, y;
    private MapFont font;
    private String text;

    public Text(int x, int y, MapFont font, String text) {
        setX(x);
        setY(y);
        setFont(font);
        setText(text);
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}