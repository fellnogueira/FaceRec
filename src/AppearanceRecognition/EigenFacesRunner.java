package AppearanceRecognition;

import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: Evgeny
 * Date: 3/4/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class EigenFacesRunner {
    //private AppearanceRecognition.Visualizer visualizer;
    //private FaceFinder faceFinder;
    EigenFacesRunner() throws URISyntaxException {
        FaceFinder faceFinder = new FaceFinder();
        Visualizer visualizer = new Visualizer(faceFinder);
        //visualizer.setOkHandler(new AppearanceRecognition.OkHandler(visualizer, faceFinder));
    }

//    public static int exponent(int base, int power) {
//        int ans = -1;
//        if (power >= 0) {
//            ans = 1;
//            for (int i = 0; i < power; i++){
//                ans *= base;
//            }
//        }
//
//        return ans;
//    }
//
//    public static int exponentRec(int base, int power) {
//        if (power == 0)
//            return 1;
//
//        return  base * exponentRec(base, power - 1);
//    }

    public static void main(String [] args) throws URISyntaxException {
//        int power = 0;
//        int base = 3;
//        System.out.println(base + "^" + power + " equals " + exponent(base, power));
//        System.out.println(base + "^" + power + " equals " + exponentRec(base, power));

        //AppearanceRecognition.Visualizer visualizer = new AppearanceRecognition.Visualizer();
        //FaceFinder faceFinder = new FaceFinder();
        EigenFacesRunner runner = new EigenFacesRunner();

    }
//        public static void debug(String msg){
//            System.out.println(msg);
//        }
}
