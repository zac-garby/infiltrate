package uk.co.zacgarby.infiltrate.etc;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import uk.co.zacgarby.infiltrate.components.ui.UITextComponent;

import java.util.HashMap;

public class Font {
    public final Texture texture;
    public final HashMap<Character, Glyph> glyphs;

    public Font(FileHandle file, String chars) {
        glyphs = new HashMap<>();

        Pixmap pm = new Pixmap(file);

        byte[] bytes = chars.getBytes();
        int x = 0;

        for (int i = 0; i < chars.length(); i++) {
            Character c = (char) bytes[i];

            int sx = x;

            while (x < pm.getWidth()) {
                boolean isEmpty = true;

                for (int y = 0; y < pm.getHeight(); y++) {
                    if (pm.getPixel(x, y) != 0) {
                        isEmpty = false;
                        break;
                    }
                }

                x++;

                if (isEmpty) {
                    break;
                }
            }

            Glyph g = new Glyph(sx, 0, x - sx, pm.getHeight());

            glyphs.put(c, g);
        }

        texture = new Texture(pm);
    }

    public void draw(SpriteBatch batch, int x, int y, String text, UITextComponent.Align align) {
        switch (align) {
            case Left:
                draw(batch, x, y, text);
                break;
            case Right:
                drawRight(batch, x, y, text);
                break;
            case Center:
                drawCenter(batch, x, y, text);
                break;
        }
    }

    public void draw(SpriteBatch batch, int x, int y, String text) {
        for (byte b : text.getBytes()) {
            char c = Character.toLowerCase((char) b);

            if (c == ' ') {
                x += 3;
            } else if (glyphs.containsKey(c)) {
                Glyph g = glyphs.get(c);
                batch.draw(texture, x, y, g.x, g.y, g.w, g.h);
                x += g.w;
            } else {
                x++;
            }
        }
    }

    public void drawRight(SpriteBatch batch, int x, int y, String text) {
        draw(batch, x - measure(text) - 1, y, text);
    }

    public void drawCenter(SpriteBatch batch, int x, int y, String text) {
        draw(batch, x - measure(text) / 2, y, text);
    }

    public int measure(String text) {
        int x = 0;

        for (byte b : text.getBytes()) {
            char c = Character.toLowerCase((char) b);

            if (c == ' ') {
                x += 3;
            } else if (glyphs.containsKey(c)) {
                Glyph g = glyphs.get(c);
                x += g.w;
            } else {
                x++;
            }
        }

        return x + 1;
    }

    public static class Glyph {
        public int x, y, w, h;

        public Glyph(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
