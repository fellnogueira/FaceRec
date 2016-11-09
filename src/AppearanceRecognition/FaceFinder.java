package AppearanceRecognition;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

public class FaceFinder {
    private double[][] projectedFaces;
    private FaceDB faceDB;
    private String dbPath;
    private SysConfig sysConfig;


    public FaceFinder() {
        sysConfig = SysConfig.getInstance();
        dbPath = sysConfig.getDBPath();
        processDB();
    }


    public BufferedImage findMatchingImage(BufferedImage image){
        // turn to dense matrix and notrmalize
        DenseDoubleMatrix2D inputFace = doubleVectorToDenseMatrix(Utils.getImageDoublePixels(image));
        DenseDoubleMatrix2D avgFace = doubleVectorToDenseMatrix(faceDB.getAverageFace());
        // substract average face
        inputFace.assign(avgFace, cern.jet.math.Functions.functions.minus);

        // project face on eigen faces
        DenseDoubleMatrix2D projectedFace = projectToFacesSpace(inputFace);
        // calculate distance
        double[] distances = getDistances(projectedFace);
        // find minimal distance
        int matchedImageIndex = getClosesedImageIndex(distances);
        return Utils.loadImage(faceDB.getImageFileNamesList().get(matchedImageIndex));
    }

    private DenseDoubleMatrix2D doubleVectorToDenseMatrix(double[] vector) {
        double[][] inputFaceData = new double[1][];
        inputFaceData[0] = vector;
        DenseDoubleMatrix2D inputFace = new DenseDoubleMatrix2D(inputFaceData);
        return inputFace;
    }

    private int getClosesedImageIndex(double[] distances) {
        double minimumDistance = Double.MAX_VALUE;
        int index=0;
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] < minimumDistance) {
                minimumDistance = distances[i];
                index = i;
            }
        }
        return index;
    }


    private double[] getDistances(DenseDoubleMatrix2D projectedFace) {
        DenseDoubleMatrix2D tempFaces = new DenseDoubleMatrix2D(projectedFaces);
        double[] inputWtData = projectedFace.toArray()[0];//.flatten();
        tempFaces = Utils.subtractFromEachRow(tempFaces, inputWtData);
        tempFaces.assign(tempFaces, cern.jet.math.Functions.functions.mult);
        double[][] temp = tempFaces.toArray();
        double[] distances = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            double sum = 0.0;
            for (int j = 0; j < temp[0].length; j++) {
                sum += temp[i][j];
            }
            distances[i] = sum;
        }
        return distances;
    }

    public void processDB(){
        List<String> imglist = FaceDB.parseDirectory(this.dbPath);
        faceDB = buildFaceDB(imglist);
        // projectToFacesSpace is a procedure that multiplies input matrix with transposed eigenFaces matrix
        projectedFaces = projectToFacesSpace(new DenseDoubleMatrix2D(faceDB.getAdjustedFaces())).toArray();
    }


    private DenseDoubleMatrix2D projectToFacesSpace(DenseDoubleMatrix2D inputFace) {
        double[][] eigenFacesArray = faceDB.getEigenFaces();
        DenseDoubleMatrix2D eigenFacesMatrix = new DenseDoubleMatrix2D(eigenFacesArray);
        DenseDoubleMatrix2D eigenFacesMatrixTranspose = new DenseDoubleMatrix2D(eigenFacesMatrix.viewDice().toArray());
        DenseDoubleMatrix2D projectedFace = new DenseDoubleMatrix2D(inputFace.zMult(eigenFacesMatrixTranspose, null).toArray());
        return projectedFace;
    }



    public FaceDB buildFaceDB(List<String> filenames){
        BufferedImage[] bufimgs=FaceDB.getImagesFromDB(filenames);

        DenseDoubleMatrix2D imagesData = Utils.normalize(Utils.imagesToDenseMatrix(bufimgs));
        double[] averageFace = Utils.getAverageImage(imagesData);
        imagesData = Utils.subtractFromEachRow(imagesData, averageFace);
        EigenvalueDecomposition egdecomp = getEigenvalueDecomposition(imagesData);
        double[] eigenValues = Utils.getDiagonal(egdecomp.getD().toArray());
        double[][] eigenVectors = egdecomp.getV().toArray();

        sortEigenVectors(eigenValues, eigenVectors);
        DenseDoubleMatrix2D eigenFaces = getNormalisedEigenFaces(imagesData, new DenseDoubleMatrix2D(eigenVectors));

        return new FaceDB(filenames, eigenFaces.toArray(), imagesData.toArray(), averageFace, eigenValues);
    }

    private EigenvalueDecomposition getEigenvalueDecomposition(
            DenseDoubleMatrix2D imagesData) {
        DenseDoubleMatrix2D imagesDataTranspose = new DenseDoubleMatrix2D(imagesData.viewDice().toArray());
        DenseDoubleMatrix2D covarianceMatrix = new DenseDoubleMatrix2D(imagesData.zMult(imagesDataTranspose, null).toArray());
        return  new EigenvalueDecomposition(covarianceMatrix);
    }


    private DenseDoubleMatrix2D getNormalisedEigenFaces(DenseDoubleMatrix2D imagesData, DenseDoubleMatrix2D eigenVectors) {
        DenseDoubleMatrix2D eigenVectorsTranspose = new DenseDoubleMatrix2D(eigenVectors.viewDice().toArray());
        DenseDoubleMatrix2D eigenFaces = new DenseDoubleMatrix2D(eigenVectorsTranspose.zMult(imagesData, null).toArray());
        double[][] eigenFacesData=eigenFaces.toArray();
        for(int i=0;i<eigenFacesData.length;i++){
            // added sqrt to norm
            double norm = Utils.norm(eigenFacesData[i]);
            for(int j=0;j<eigenFacesData[i].length;j++){
                double v=eigenFacesData[i][j];
                eigenFacesData[i][j]=v/norm;
            }
        }
        return new DenseDoubleMatrix2D(eigenFacesData);
    }


    public void sortEigenVectors(double[] eigenValues,double[][]eigenVectors){
        Hashtable<Double,double[]> table =  new Hashtable<Double,double[]> ();
        Double[] evals=new Double[eigenValues.length];
        getEigenValuesAsDouble(eigenValues, evals);
        fillHashtable(eigenValues, eigenVectors, table, evals);
        ArrayList<Double> keylist = sortKeysInReverse(table);
        updateEigenVectors(eigenVectors, table, evals, keylist);
        Double[] sortedkeys=new Double[keylist.size()];
        keylist.toArray(sortedkeys);//store the sorted list elements in an array
        //use the array to update the original double[]eigValues
        updateEigenValues(eigenValues, sortedkeys);
    }
    private void getEigenValuesAsDouble(double[] eigenValue, Double[] evals) {
        for(int i=0;i<eigenValue.length;i++){
            evals[i]=new Double(eigenValue[i]);
        }
    }

    private ArrayList<Double> sortKeysInReverse(
            Hashtable<Double, double[]> table) {
        Enumeration<Double> keys=table.keys();
        ArrayList<Double> keylist=Collections.list(keys);
        Collections.sort(keylist,Collections.reverseOrder());//largest first
        return keylist;
    }

    private void updateEigenValues(double[] eigenValue, Double[] sortedKeys) {
        for(int i=0;i<sortedKeys.length;i++){
            Double dbl=sortedKeys[i];
            double dblval=dbl.doubleValue();
            eigenValue[i]=dblval;
        }
    }

    private void updateEigenVectors(double[][] eigenVector,
                                    Hashtable<Double, double[]> table, Double[] evals,
                                    ArrayList<Double> keylist) {
        for(int i = 0; i < evals.length; i++){
            double[] ret = table.get(keylist.get(i));
            setColumn(eigenVector,ret,i);
        }
    }

    private void fillHashtable(double[] eigenValues, double[][] eigenVectors,
                               Hashtable<Double, double[]> table, Double[] evals) {
        for(int i=0;i<eigenValues.length;i++){
            Double key=evals[i];
            double[] value=getColumn(eigenVectors ,i);
            table.put(key,value);
        }
    }

    private double[] getColumn( double[][] mat, int j ){
        int m = mat.length;
        double[] res = new double[m];
        for ( int i = 0; i < m; ++i ){
            res[i] = mat[i][j];
        }
        return(res);
    }

    private void setColumn(double[][] mat,double[] col,int c){
        int len=col.length;
        for(int row=0;row<len;row++){
            mat[row][c]=col[row];
        }
    }
}
