package com.example.fridgesnap;





import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.tensorflow.lite.*;

import com.example.fridgesnap.ml.Model;

import androidx.camera.core.Camera;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.provider.BlockedNumberContract;
import android.util.Log;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.hardware.lights.LightsManager;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fridgesnap.ml.Model;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import android.os.Bundle;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class CameraUse extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture; //initializes variables to be used throughout code
    PreviewView previewView;
    Button butContinue;
    Button bTakePicture;
    Button closeApp;
    private ImageCapture imageCapture;
    private Image capturedImage;
    ImageView imageView;
    int imageSize = 224;
    private Image savedImage;
    TextView result, resultList;
    private List<String> predictionList = new ArrayList<>();
    private Interpreter tfliteInterpreter;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private  TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_use);
        closeApp = findViewById(R.id.closeApp); //sets an action for the exit button - cannot exit the app, so takes the user back to the main landing page
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CameraUse.this, MainActivity.class));
            }
        });

        imageView = findViewById(R.id.imageView); //sets the initializes variables to the objects from the camerause xml file
        result = findViewById(R.id.result);
        resultList = findViewById(R.id.resultList);



        butContinue = (Button) findViewById(R.id.bContinue);
        butContinue.setVisibility(View.GONE); //hides the continue button upon launch - only show when the user has taken an image.

        bTakePicture = findViewById(R.id.bCapture); //gets permissions to use the devices camera when the take picture button is clicked, then opens the devices camera if accepted.
        bTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butContinue.setVisibility(View.VISIBLE);
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Please accept the camera permissions to continue", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) { //if the image is taken
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap); //displays a preview of the image on the screen

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true);

            Interpreter tflite = null; //initializes the interpreter with a null value
            try {
                tflite = new Interpreter(loadModelFile(CameraUse.this, "modelfull.tflite")); //runs the interpreter using the tensorflow lite model.
            } catch (IOException e) {
                e.printStackTrace();
            }

            int numLabels = 20; //set to the same number of output classes as the model
            outputProbabilityBuffer = TensorBuffer.createFixedSize(new int[]{1, numLabels}, DataType.FLOAT32); //creates an output probability buffer which has the same parameters as the tflite model



            ByteBuffer inputBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3); //allocates the bytebuffer with the same image size as images passed through the tflite model.
            inputBuffer.order(ByteOrder.nativeOrder());
            inputBuffer.rewind();
            for (int y = 0; y < imageSize; y++) {
                for (int x = 0; x < imageSize; x++) {
                    int pixel = resizedBitmap.getPixel(x, y);
                    inputBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); //the 3 values within this block relate to the 3 colour channels: red, blue, and green
                    inputBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
                    inputBuffer.putFloat((pixel & 0xFF) / 255.0f);
                }
            }

            float[][] outputBuffer = new float[1][20];
            tflite.run(inputBuffer, outputBuffer); //runs the tflite model on the input and output buffers

            int maxIndex = 0;
            float maxValue = outputBuffer[0][0];
            for (int i = 1; i < 3; i++) {
                if (outputBuffer[0][i] > maxValue) {
                    maxIndex = i;
                    maxValue = outputBuffer[0][i];
                }
            }

            float[] probabilities = outputBuffer[0];

            // gets the list of labels for each of the food items within the dataset.
            if (labels == null) {
                labels = Arrays.asList(loadLabels());
            }

            Map<String, Float> labeledProb = new HashMap<>();
            for (int i = 0; i < labels.size(); ++i) {
                labeledProb.put(labels.get(i), probabilities[i]);
            } //gets the probability for each of the labels and sorts the predictions in a descending order
            Object[] sortedLabels = labeledProb.entrySet().stream()
                    .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                    .toArray();

            //a message box is displayed asking the user to choose the correct item from a list of predicitions
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the correct item");

            //creates a list of the top 3 predictions to be stored within a box
            List<String> listOfPreds = new ArrayList<>();
            for (int i = 0; i < 3; ++i) {
                String label = ((Map.Entry<String, Float>)sortedLabels[i]).getKey();
                float probability = ((Map.Entry<String, Float>)sortedLabels[i]).getValue();
                listOfPreds.add(String.format("%s (%.2f%%)", label, probability * 100));
            }

            //adds the selected item to the list to be further processed.
            builder.setItems(listOfPreds.toArray(new String[0]), (dialog, which) -> {
                String selectedLabel = ((Map.Entry<String, Float>)sortedLabels[which]).getKey();
                resultList.setText(selectedLabel);
                predictionList.add(selectedLabel);

            });

            //shows the prediction box.
            builder.show();


            String[] labels = loadLabels();
            String className = labels[maxIndex];



            //when the user presses the continue button they are taken to the food item list screen and the contents of the prediction list is passed.
            butContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendintent = new Intent (CameraUse.this, FoodItemList.class);
                    sendintent.putStringArrayListExtra("predictionList", new ArrayList<String>(predictionList));
                    startActivity(sendintent);
                }
            });


        }
    }

    //gets the labels from the labels.text file
    private String[] loadLabels() {
        String[] labels = null;
        try {
            InputStream inputStream = getAssets().open("labels.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String text = new String(buffer);
            labels = text.split("\n");
        } catch (IOException e) {
            Log.e(TAG, "Failed to load labels.", e);
        }
        return labels;
    }


    //loads the tensorflow lite model
    private MappedByteBuffer loadModelFile(Context context, String fileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("modelfull.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }



}
