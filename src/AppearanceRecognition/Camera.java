package AppearanceRecognition; /**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/5/13
 * Time: 8:58 PM
 * To change this template use File | Settings | File Templates.
 */

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class Camera implements Runnable {
    final int INTERVAL=100;///you may use interval
    private IplImage image;
    private MarkingImagePanel panel;
    private boolean capture;

//    CanvasFrame canvas = new CanvasFrame("Web Cam");
    public Camera(MarkingImagePanel panel) {
        capture = true;
        this.panel = panel;
        //canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    }

    public void finishCapture() {
        capture = false;
    }

    @Override
    public void run() {
        FrameGrabber grabber = new OpenCVFrameGrabber(1); // 1 for next camera
        int i=0;
        try {
            grabber.start();
            IplImage img;
            while (capture) {
                img = grabber.grab();
                Thread.sleep(INTERVAL);
                panel.setImage(img.getBufferedImage());
                panel.repaint();
            }
        } catch (Exception e) {
        }
    }
}