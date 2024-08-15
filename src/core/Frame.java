package core;

import java.awt.image.BufferedImage;
import java.io.Serializable;


public class Frame implements Serializable {
    private BufferedImage frame;

    public Frame(BufferedImage frame) {
        this.frame = frame;
    }

    public BufferedImage getFrame() {
        return frame;
    }

}
