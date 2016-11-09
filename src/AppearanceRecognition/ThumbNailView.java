package AppearanceRecognition; /**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/5/13
 * Time: 8:17 PM
 * To change this template use File | Settings | File Templates.
 */

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

public class ThumbNailView extends FileView {
    @Override
    public ImageIcon getIcon(File f) {
        ImageIcon  icon=null;
        if (f.getPath() != null) {
            icon = new ImageIcon(f.getPath());
            Image img = icon.getImage() ;
            Image newimg = img.getScaledInstance( 16, -1,  java.awt.Image.SCALE_DEFAULT) ;
            return new ImageIcon(newimg);
        } else {
            return null;
        }
    }
}
