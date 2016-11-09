package AppearanceRecognition;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/5/13
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarkingImagePanel extends ImagePanel {
    private Point rightX;
    private Point leftX;

    public MarkingImagePanel() {
        super(300,360);
        leftX = null;
        rightX = null;
    }

    public Point getRightX() {
        return rightX;
    }

    public void setRightX(Point rightX) {
        this.rightX = rightX;
    }

    public Point getLeftX() {
        return leftX;
    }

    public void setLeftX(Point leftX) {
        this.leftX = leftX;
    }

    public void addX(Point p) {
        if (leftX==null) {
            leftX = new Point(p);
        }
        else if (rightX==null) {
            rightX = new Point(p);
        }
        else {
            leftX = null;
            rightX = null;
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(bi, 0, 0, null);

        if (rightX != null) {
            g.setColor(Color.CYAN);
            g.drawLine((int)(rightX.getX() - 4), (int)rightX.getY(), (int)(rightX.getX() + 4), (int)rightX.getY());
            g.drawLine((int)rightX.getX(), (int)(rightX.getY() - 4), (int)rightX.getX(), (int)(rightX.getY() + 4));
        }

        if (leftX != null) {
            g.setColor(Color.CYAN);
            g.drawLine((int)(leftX.getX() - 4), (int)leftX.getY(), (int)(leftX.getX() + 4), (int)leftX.getY());
            g.drawLine((int)leftX.getX(), (int)(leftX.getY() - 4), (int)leftX.getX(), (int)(leftX.getY() + 4));
        }

    }
}