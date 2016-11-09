package AppearanceRecognition;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/5/13
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImagePanel extends JPanel {
    private Dimension size;
    private BufferedImage image;

    public ImagePanel() {
        size = new Dimension(150,180);
        this.setPreferredSize(new Dimension(size.width, size.height));
    }

    public ImagePanel(int width, int height) {
        size = new Dimension(width,height);
        this.setPreferredSize(new Dimension(size.width, size.height));
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setImageFromFile(File f) {
        try {
            image = ImageIO.read(f);
        } catch (Exception e) {
            image = null;
        }
        if (image != null) {
//            setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
            setPreferredSize(new Dimension(size.width, size.height));
        }
        this.revalidate();
        this.repaint();
    }

    public BufferedImage getImage() {
        return image;
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, size.width, size.height, null);
    }
}