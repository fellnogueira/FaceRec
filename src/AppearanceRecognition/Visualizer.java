package AppearanceRecognition;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/4/13
 * Time: 7:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Visualizer extends JFrame implements ActionListener {
    private int EIGEN_FACE_WIDTH;
    private int EIGEN_FACE_HEIGHT;
    private int FINAL_EYE_DISTANCE;
    private static final int BUTTON_WIDTH = 30;
    private static final int BUTTON_HEIGHT = 40;
    private static final String FRAME_NAME = "Face Finder";

    private static final String MESSAGE_LOAD_PICTURE = "Please load picture";
    private static final String MESSAGE_MARK_EYES = "Please mark eyes (with left mouse button)";
    private static final String MESSAGE_CUT_PICTURE = "Press right button to cut the picture";
    private static final String MESSAGE_SAVE_OR_MATCH = "Save picture to DB or find a match from DB";


    private MarkingImagePanel selectedImagePanel;
    private ImagePanel scaledImagePanel;
    private ImagePanel matchingImagePanel;
    private JFileChooser filechooser;
    private JButton webCamButton;
    private JButton fileChooseButton;
    private boolean camFlag;
    private JButton save;
    private JButton match;
    private JButton stop;
    private Camera camera;
    private JEditorPane console;
    private FaceFinder faceFinder;

    private SysConfig sysConfig;


    public Visualizer(FaceFinder faceFinder) {
        super(FRAME_NAME);
        this.faceFinder = faceFinder;
        setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = (int)(screenSize.height/1.5);
        int width = (int)(screenSize.width/1.5);
        this.setSize(width, height);
        this.initButtons();
        this.initGrid();
        this.setVisible(true);

        sysConfig = SysConfig.getInstance();

        EIGEN_FACE_WIDTH = sysConfig.getEigenFaceWidth();
        EIGEN_FACE_HEIGHT = sysConfig.getEigenFaceHeight();
        FINAL_EYE_DISTANCE = sysConfig.getEyeDistance();
    }

    private void initGrid() {
        Insets insets = new Insets(0, 0, 0, 0);
        this.add(fileChooseButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
        this.add(webCamButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));

        this.add(selectedImagePanel,
                new GridBagConstraints(0, 1, 3, 4, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        this.add(scaledImagePanel,
                new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0, 10, GridBagConstraints.NONE, insets, 0, 0));
        this.add(matchingImagePanel,
                new GridBagConstraints(5, 1, 2, 1, 0.0, 0.0, 10, GridBagConstraints.NONE, insets, 0, 0));

        this.add(save,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
        this.add(match,
                new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
        this.add(stop,
                new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

        this.add(console,
                new GridBagConstraints(3, 3, 4, 2, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0));
    }

    private void initButtons() {
        selectedImagePanel = new MarkingImagePanel();
        selectedImagePanel.addMouseListener(new SelectedImagePanelListener());
        selectedImagePanel.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        selectedImagePanel.setImage(Utils.loadImage("icons\\finder_original.png"));

        scaledImagePanel = new ImagePanel();
        scaledImagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        scaledImagePanel.setImage(Utils.loadImage("icons\\finder_original.png"));
        //Color.black
        matchingImagePanel = new ImagePanel();
        matchingImagePanel.setBorder(BorderFactory.createLoweredBevelBorder());
        matchingImagePanel.setImage(Utils.loadImage("icons\\target.png"));


        ImageIcon icon = new ImageIcon(new String(Utils.getJarParentDir()+"icons\\web_camera.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH) ;
        webCamButton = new JButton(new ImageIcon(img));
        camFlag = true;

        icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\load.png"));
        img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT,  java.awt.Image.SCALE_SMOOTH) ;
        fileChooseButton = new JButton(new ImageIcon(img));
        webCamButton.addActionListener(this);
        fileChooseButton.addActionListener(this);

        icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\pictures.png"));
        img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT,  java.awt.Image.SCALE_SMOOTH) ;
        save = new JButton(new ImageIcon(img));
        save.addActionListener(this);

        icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\search.png"));
        img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT,  java.awt.Image.SCALE_SMOOTH) ;
        match = new JButton(new ImageIcon(img));
        match.addActionListener(this);

        icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\exit.png"));
        img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT,  java.awt.Image.SCALE_SMOOTH) ;
        stop = new JButton(new ImageIcon(img));
        stop.addActionListener(this);


        console = new JEditorPane();
        console.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        console.setForeground(Color.BLACK);
        console.setPreferredSize(new Dimension(300, 150));
        console.setText(MESSAGE_LOAD_PICTURE);
        console.setVisible(true);
    }

    public void displaySelectedImage(File f){
        selectedImagePanel.setImageFromFile(f);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == fileChooseButton){
            filechooser=new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg","gif","jpeg","png", "JPG","GIF","JPEG","PNG");
            filechooser.setFileFilter(filter);
            ThumbNailView thumbsView = new ThumbNailView();
            filechooser.setFileView(thumbsView);
            filechooser.setAccessory(new ImagePreview(filechooser));
            int option = filechooser.showDialog(Visualizer.this,"select an image");
            if (option == JFileChooser.APPROVE_OPTION){
                File file = filechooser.getSelectedFile();
                displaySelectedImage(file);
                console.setText(MESSAGE_MARK_EYES);
                selectedImagePanel.setRightX(null);
                selectedImagePanel.setLeftX(null);
            }
        } else if(evt.getSource() == webCamButton){
            if (camFlag) {
                camFlag = false;
                selectedImagePanel.setRightX(null);
                selectedImagePanel.setLeftX(null);
                ImageIcon icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\happy.png"));
                Image img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH) ;
                webCamButton.setIcon(new ImageIcon(img));
                camera = new Camera(selectedImagePanel);
                selectedImagePanel.setRightX(null);
                selectedImagePanel.setLeftX(null);
                (new Thread(camera)).start();
            }
            else {
                camFlag = true;
                ImageIcon icon = new ImageIcon(new String( Utils.getJarParentDir()+"icons\\web_camera.png"));
                Image img = icon.getImage().getScaledInstance(BUTTON_WIDTH, BUTTON_HEIGHT, java.awt.Image.SCALE_SMOOTH) ;
                webCamButton.setIcon(new ImageIcon(img));
                camera.finishCapture();
                camera = null;
            }
        }else if(evt.getSource() == stop){
            if (camera != null) {
                camera.finishCapture();
            }
            this.dispose();
        }else if (evt.getSource() == match){
            if (this.scaledImagePanel.getImage() != null) {
                BufferedImage matchedImage = faceFinder.findMatchingImage(this.scaledImagePanel.getImage());
                matchingImagePanel.setImage(matchedImage);
                matchingImagePanel.repaint();
                console.setText(MESSAGE_LOAD_PICTURE);
            }
        }else if (evt.getSource() == save){
            if (this.scaledImagePanel.getImage() != null) {
                File imgFile = null;
                try {
                    File dir = new File(Utils.getJarParentDir() + sysConfig.getDBPath());

                    String name = "pic" + (int)(Math.random()*10000000)+".png";
                    imgFile = new File(dir, name);

                    if (imgFile != null) {
                        ImageIO.write(this.scaledImagePanel.getImage(), "png", imgFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                faceFinder = new FaceFinder();
            }
        }
    }


    class SelectedImagePanelListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (camFlag) {
                    // checking if web cam isn't working.
                    selectedImagePanel.addX(e.getPoint());
                    selectedImagePanel.repaint();
                    if (selectedImagePanel.getRightX() != null) {
                        console.setText(MESSAGE_CUT_PICTURE);
                    }
                }
            }
            else if (e.getButton() == MouseEvent.BUTTON3) {
                BufferedImage before = selectedImagePanel.getImage();
                Point pLeft = selectedImagePanel.getLeftX();
                Point pRight = selectedImagePanel.getRightX();
                if (pLeft.x > pRight.x) {
                    pRight = pLeft;
                    pLeft = selectedImagePanel.getRightX();
                }

                int eyeY = (pLeft.y + pRight.y)/2;
                int eyeDistance = Math.abs(pRight.x - pLeft.x);
                if (eyeDistance == 0) {
                    throw new RuntimeException("Eyes have to be on horizontal distance from each other.");
                }

                float scaleFactorOrigX = ((float)selectedImagePanel.getWidth() / before.getRaster().getWidth());
                float scaleFactorOrigY = ((float)selectedImagePanel.getHeight() / before.getRaster().getHeight());
                float scaleFactorFinal = ((float)FINAL_EYE_DISTANCE / eyeDistance);
                Dimension leftCorner = null;
                Dimension rightCorner = null;


                int relativeEyeDistanceFromBorder = 6 * EIGEN_FACE_WIDTH / 25;
                int relativeEyeDepthFromBorder = EIGEN_FACE_HEIGHT / 6 + 20;
                leftCorner = new Dimension(
                        (int)((pLeft.x - (relativeEyeDistanceFromBorder / scaleFactorFinal)) / scaleFactorOrigX),
                        (int)((eyeY - relativeEyeDepthFromBorder / scaleFactorFinal) / scaleFactorOrigY));
                rightCorner = new Dimension(
                        (int)((pRight.x + (relativeEyeDistanceFromBorder / scaleFactorFinal)) / scaleFactorOrigX),
                        (int)((eyeY + 5*relativeEyeDepthFromBorder / scaleFactorFinal ) / scaleFactorOrigY));

                if (leftCorner.width < 0)  leftCorner.width = 0;
                if (leftCorner.height < 0)  leftCorner.height = 0;

                if (rightCorner.width > before.getRaster().getWidth())  rightCorner.width = before.getRaster().getWidth();
                if (rightCorner.height > before.getRaster().getHeight())  rightCorner.height = before.getRaster().getHeight();

                BufferedImage after = before.getSubimage(leftCorner.width, leftCorner.height,
                        rightCorner.width - leftCorner.width,
                        rightCorner.height - leftCorner.height);

                after = Utils.scaleToWindow(after, EIGEN_FACE_WIDTH, EIGEN_FACE_HEIGHT);

                BufferedImage grayscale = Utils.convertToGrayscale(after);
                scaledImagePanel.setImage(grayscale);
                scaledImagePanel.repaint();

                console.setText(MESSAGE_SAVE_OR_MATCH);
            }
        }
    }
}
