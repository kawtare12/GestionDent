package com.machmoum.projetsnina;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat6;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends Activity implements View.OnClickListener{
    //private List<Point> selectedPoints = new ArrayList<>();


    {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Toast.makeText(HomeActivity.this, "OpenCV initialization failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_SELECTED_POINTS = 4;
    private ImageView imageView;
    private Mat originalMat;
    private TextView  anglesTextView;
    private TextView  anglesTextView2;

    private List<Point> selectedPoints = new ArrayList<>();
    private Bitmap originalBitmap;
    private String pwid,studentid;
    Button btnSubmitTP;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pwid = intent.getStringExtra("PWid");
        studentid = intent.getStringExtra("STUDENTid");
        System.out.println(pwid);
        System.out.println(studentid);

        btnSubmitTP=findViewById(R.id.btnSubmitTP);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);
        anglesTextView=findViewById(R.id.anglesTextView);
        anglesTextView2=findViewById(R.id.anglesTextView2);

        btnSubmitTP.setOnClickListener(this);


                btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                processImage(  originalBitmap );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void addAverageSizePoints(Mat dilatedImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            for (Point point : contour.toList()) {
                Imgproc.circle(dilatedImage, point, 3, new Scalar(255, 255, 0), -1);
            }
        }
    }

    private void selectPoints(Mat lines, Mat dilatedImage) {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    double x = event.getX();
                    double y = event.getY();

                    double[] imagePoint = imageViewToImage(x, y);
                    Point touchedPoint = new Point(imagePoint[0], imagePoint[1]);

                    boolean isOnContour = isPointOnContour(touchedPoint, dilatedImage);

                    if (!isOnContour) {
                        Point closestContourPoint = findClosestContourPoint(touchedPoint, dilatedImage);

                        if (closestContourPoint != null && selectedPoints.size() < MAX_SELECTED_POINTS) {
                            selectedPoints.add(closestContourPoint);

                            if (selectedPoints.size() == MAX_SELECTED_POINTS) {
                                // Afficher le Toast une fois que 4 points ont été sélectionnés
                                Toast.makeText(HomeActivity.this, "4 points sélectionnés", Toast.LENGTH_SHORT).show();
                            }
                            processImage(originalBitmap);
                        }
                    } else {
                        addAverageSizePoints(dilatedImage);
                        processImage(originalBitmap);
                    }
                }
                return true;
            }
        });
    }


    private boolean isPointOnContour(Point point, Mat dilatedImage) {
        int neighborhoodSize = 5;
        double[] pixelValue = dilatedImage.get((int) point.y, (int) point.x);

        for (int i = -neighborhoodSize; i <= neighborhoodSize; i++) {
            for (int j = -neighborhoodSize; j <= neighborhoodSize; j++) {
                double[] currentPixel = dilatedImage.get((int) point.y + i, (int) point.x + j);
                if (currentPixel[0] != pixelValue[0]) {
                    return true;
                }
            }
        }
        return false;
    }

    private Point findClosestContourPoint(Point touchPoint, Mat dilatedImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dilatedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Point closestContourPoint = null;
        double minContourDistance = Double.MAX_VALUE;

        for (MatOfPoint contour : contours) {
            for (Point contourPoint : contour.toList()) {
                double distance = Math.sqrt(Math.pow(contourPoint.x - touchPoint.x, 2) + Math.pow(contourPoint.y - touchPoint.y, 2));

                if (distance < minContourDistance) {
                    minContourDistance = distance;
                    closestContourPoint = contourPoint;
                }
            }
        }

        return closestContourPoint;
    }

    // Méthode pour convertir les coordonnées de l'écran en coordonnées de l'image
    private double[] imageViewToImage(double x, double y) {
        double[] imagePoint = new double[2];
        if (imageView.getDrawable() != null) {
            int viewWidth = imageView.getWidth();
            int viewHeight = imageView.getHeight();
            int imageWidth = imageView.getDrawable().getIntrinsicWidth();
            int imageHeight = imageView.getDrawable().getIntrinsicHeight();

            double scaleX = (double) imageWidth / (double) viewWidth;
            double scaleY = (double) imageHeight / (double) viewHeight;

            imagePoint[0] = x * scaleX;
            imagePoint[1] = y * scaleY;
        }
        return imagePoint;
    }

    // Méthode pour dessiner les points sélectionnés en rouge
    private void drawSelectedPoints(Mat image, List<Point> points) {
        for (Point point : points) {
            Imgproc.circle(image, point, 4,new Scalar(128, 0, 255), -1);

        }
    }


    // Méthode pour trouver le point le plus proche parmi les lignes détectées
    private Point findClosestPoint(Point touchPoint, Mat lines) {
        double minDistance = Double.MAX_VALUE;
        Point closestPoint = new Point();

        for (int x = 0; x < lines.cols(); x++) {
            double[] vec = lines.get(0, x);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];

            double distance1 = Math.sqrt(Math.pow(x1 - touchPoint.x, 2) + Math.pow(y1 - touchPoint.y, 2));
            double distance2 = Math.sqrt(Math.pow(x2 - touchPoint.x, 2) + Math.pow(y2 - touchPoint.y, 2));

            if (distance1 < minDistance) {
                minDistance = distance1;
                closestPoint.set(new double[]{x1, y1});
            }

            if (distance2 < minDistance) {
                minDistance = distance2;
                closestPoint.set(new double[]{x2, y2});
            }
        }

        return closestPoint;
    }
    private void processImage(Bitmap bitmap) {
        // Convertir le Bitmap en Mat
        Mat imageMat = new Mat();
        Utils.bitmapToMat(bitmap, imageMat);

        // Convertir l'image en niveaux de gris
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_BGR2GRAY);

        // Appliquer un flou gaussien
        Imgproc.GaussianBlur(imageMat, imageMat, new Size(5, 5), 0);

        // Appliquer la détection de bord de Canny
        Imgproc.Canny(imageMat, imageMat, 50, 150);

        // Appliquer une dilatation
        Mat dilatedImage = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(imageMat, dilatedImage, kernel);

        // Utiliser la transformation de Hough pour détecter les lignes
        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilatedImage, lines, 1, Math.PI / 180, 50, 50, 10);

        // Appel correct à selectPoints() avec les deux arguments
        selectPoints(lines, dilatedImage);

        // Dessiner les contours sur une nouvelle image
        Mat contoursImage = new Mat(imageMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        Imgproc.cvtColor(imageMat, contoursImage, Imgproc.COLOR_GRAY2BGR);

        // Dessiner les points sélectionnés en rouge
        drawSelectedPoints(contoursImage, selectedPoints);

        // Vérification que nous avons sélectionné au moins 4 points
        if (selectedPoints.size() >= 4) {
            // Première droite passant par les deux premiers points (index 0 et 1)
            Point p1 = selectedPoints.get(0);
            Point p2 = selectedPoints.get(1);

            // Deuxième droite passant par les deux derniers points (index 2 et 3)
            Point p3 = selectedPoints.get(2);
            Point p4 = selectedPoints.get(3);
            double extendLength = 1000.0; // Longueur de l'extension des droites

            // Pour la première droite
            Point extensionP1 = new Point(p1.x - extendLength, p1.y - extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));
            Point extensionP2 = new Point(p2.x + extendLength, p2.y + extendLength * ((p2.y - p1.y) / (p2.x - p1.x)));

            // Pour la deuxième droite
            Point extensionP3 = new Point(p3.x - extendLength, p3.y - extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));
            Point extensionP4 = new Point(p4.x + extendLength, p4.y + extendLength * ((p4.y - p3.y) / (p4.x - p3.x)));

            // Dessiner les lignes correspondant aux droites prolongées
            Imgproc.line(contoursImage, extensionP1, extensionP2, new Scalar(0, 255, 0), 2);
            Imgproc.line(contoursImage, extensionP3, extensionP4, new Scalar(0, 255, 0), 2);


            // Tri des points par coordonnées x
            Collections.sort(selectedPoints, new Comparator<Point>() {
                @Override
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.x, p2.x);
                }
            });



            // Recherche du point le plus à gauche et du point le plus à droite parmi les quatre points sélectionnés
            Point leftMostPoint = selectedPoints.get(0);
            Point rightMostPoint = selectedPoints.get(0);

            for (Point point : selectedPoints) {
                if (point.x < leftMostPoint.x) {
                    leftMostPoint = point;
                }
                if (point.x > rightMostPoint.x) {
                    rightMostPoint = point;
                }
            }

            // Dessiner les lignes verticales passant par les points les plus à gauche et à droite
            Imgproc.line(contoursImage, new Point(leftMostPoint.x, 0), new Point(leftMostPoint.x, contoursImage.rows()), new Scalar(255, 0, 0), 2);
            Imgproc.line(contoursImage, new Point(rightMostPoint.x, 0), new Point(rightMostPoint.x, contoursImage.rows()), new Scalar(255, 0, 0), 2);


// Calcul des angles gauche et droit (angle de dépouille)
            double deltaX = Math.abs(selectedPoints.get(0).x - selectedPoints.get(1).x);
            double deltaY = Math.abs(selectedPoints.get(0).y - selectedPoints.get(1).y);
            double taperAngleRad = Math.atan(deltaY / deltaX);
            double taperAngleDeg = Math.toDegrees(taperAngleRad);
            double angleGauche = 90 - taperAngleDeg;

            double deltaX2 = Math.abs(selectedPoints.get(2).x - selectedPoints.get(3).x);
            double deltaY2 = Math.abs(selectedPoints.get(2).y - selectedPoints.get(3).y);
            double taperAngleRad2 = Math.atan(deltaY2 / deltaX2);
            double taperAngleDeg2 = Math.toDegrees(taperAngleRad2);
            double angleDroit = 90 - taperAngleDeg2;

// Affichage des angles dans le TextView
            anglesTextView.setText(" gauche : " + angleGauche + " degrés\n");
            anglesTextView.append(" droit : " + angleDroit + " degrés\n");





        }

        // Convertir le Mat avec les contours en Bitmap
        Bitmap contoursBitmap = Bitmap.createBitmap(contoursImage.cols(), contoursImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contoursImage, contoursBitmap);

        // Afficher l'image avec les contours dans l'ImageView
        imageView.setImageBitmap(contoursBitmap);
    }





    // Override onDestroy to release OpenCV resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (originalMat != null) {
            originalMat.release();
 }
}

        @Override
        public void onClick(View v) {
            String url = "http://128.10.1.143:8087/etudiant/saveStudentPW";
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("resultat", "angle gauche = " + anglesTextView.getText().toString() + " angle droit = " + anglesTextView2.getText().toString());
                String base64Image = bitmapToBase64(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                jsonBody.put("imageFront", base64Image);
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());

                String currentDate = dateFormat.format(calendar.getTime());
                String currentTime = timeFormat.format(calendar.getTime());

                // Put date and time in the JSON body
                jsonBody.put("date", currentDate);
                jsonBody.put("time", currentTime);
                JSONObject studentobject = new JSONObject();
                studentobject.put("id", Integer.parseInt(studentid));
                jsonBody.put("student", studentobject);
                JSONObject pwobject = new JSONObject();
                pwobject.put("id", Integer.parseInt(pwid));
                jsonBody.put("pw", pwobject);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(HomeActivity.this, "Tp soummis avec succes", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(HomeActivity.this, "Erreur de soumission", Toast.LENGTH_SHORT).show();
                            }
                        }
                );


                Volley.newRequestQueue(HomeActivity.this).add(request);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String bitmapToBase64(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

}
