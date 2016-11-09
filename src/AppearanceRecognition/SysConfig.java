package AppearanceRecognition;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/10/13
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class SysConfig {
    private static final String CONFIG_FILE = "conf.xml";
    //private File file;
    private static SysConfig instance;
    private String dBPath;
    private int eyeDistance;
    private int eigenFaceWidth;
    private int eigenFaceHeight;
    private int eigenVectorsNumber;

    private SysConfig() throws URISyntaxException {
        if (instance != null) {
            throw new Error();
        }

        try {
			Document doc = getConfigDoc();
	        dBPath = doc.selectSingleNode("//conf/db_path").getText();
	        eyeDistance = Integer.parseInt(doc.selectSingleNode("//conf/eye_distance").getText());
	        eigenFaceWidth = Integer.parseInt(doc.selectSingleNode("//conf/eigen_face_width").getText());
	        eigenFaceHeight = Integer.parseInt(doc.selectSingleNode("//conf/eigen_face_height").getText());
	        eigenVectorsNumber = Integer.parseInt(doc.selectSingleNode("//conf/num_of_eigenfaces").getText());
        } catch (MalformedURLException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
    }

    private static Document getConfigDoc() throws URISyntaxException, MalformedURLException {
//        System.out.println(SysConfig.class.getClass() + " asdads");
//        System.out.println(SysConfig.class.getProtectionDomain().hashCode() + " asdads");
//        System.out.println(SysConfig.class.getProtectionDomain().getCodeSource().hashCode() + " code source");
//        System.out.println(SysConfig.class.getProtectionDomain().getCodeSource().getLocation() + " asdads");
//        System.out.println(SysConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI() + " asdads");
//        File jarFile = new File(SysConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI());
//        System.out.println(jarFile.getParent() + " asdads");
        URL fileURL = new URL(SysConfig.class.getProtectionDomain().getCodeSource().getLocation() + CONFIG_FILE);
        SAXReader reader = new SAXReader();
        Document doc = null;

        try {
            if (fileURL == null) {
                File xml = new File(Utils.getJarParentDir() + CONFIG_FILE);
                doc = reader.read(xml);
            }
            else {
                doc = reader.read(fileURL);
            }
        } catch (DocumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        System.out.println("aasdasdasdasd" + fileURL.getPath());
//        File xml = new File(fileURL.getPath().replaceAll("!", ""));
        //File xml = new File(CONFIG_FILE);

        return doc;
    }

    public String getDBPath() {
//        Document doc = getConfigDoc();
//        return doc.selectSingleNode("//conf/db_path").getText();
        return dBPath;
    }

//    public static double getTreshold() {
//        Document doc = getConfigDoc();
//        return Double.parseDouble(doc.selectSingleNode("//conf/treshold").getText());
//    }

    public int getEyeDistance() {
//        Document doc = getConfigDoc();
//        return Integer.parseInt(doc.selectSingleNode("//conf/eye_distance").getText());
        return eyeDistance;
    }

    public int getEigenFaceWidth() {
//        Document doc = getConfigDoc();
//        return Integer.parseInt(doc.selectSingleNode("//conf/eigen_face_width").getText());
        return eigenFaceWidth;
    }

    public  int getEigenFaceHeight() {
//        Document doc = getConfigDoc();
//        return Integer.parseInt(doc.selectSingleNode("//conf/eigen_face_height").getText());
        return eigenFaceHeight;
    }

    public  int getEigenVectorsNumber() {
//        Document doc = getConfigDoc();
//        return Integer.parseInt(doc.selectSingleNode("//conf/num_of_eigenfaces").getText());
        return eigenVectorsNumber;
    }

    public static synchronized SysConfig getInstance()  {
        if (instance == null)
            try {
                instance = new SysConfig();
            } catch (URISyntaxException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        return instance;
    }
}
