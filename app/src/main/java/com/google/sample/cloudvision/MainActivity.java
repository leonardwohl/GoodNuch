/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.cloudvision;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyC_JtL07OXi6oqRAO0fGgD8Td8e8s3e5jY";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;

    private class Nutrition {
        int calories;
        int fat;
        int sat_fat;
        int trans_fat;
        int cholesteral;
        int sodium;
        int carbs;
        int fiber;
        int sugar;
        int protein;

        Nutrition() {
            calories = 0;
            fat = 0;
            sat_fat = 0;
            trans_fat = 0;
            cholesteral = 0;
            sodium = 0;
            carbs = 0;
            fiber = 0;
            sugar = 0;
            protein = 0;
        }
    }

    private static Nutrition stats;
    private static Nutrition total_stats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stats = new Nutrition();
        total_stats = new Nutrition();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int serv;
                EditText edit = (EditText)findViewById(R.id.servings_number);
                serv = Integer.parseInt(edit.getText().toString());

                edit = (EditText)findViewById(R.id.calories_number);
                if (edit.getText().length() == 0) {
                    stats.calories = 0;
                } else {
                    stats.calories = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.fat_number);
                if (edit.getText().length() == 0) {
                    stats.fat = 0;
                } else {
                    stats.fat = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.sat_fat_number);
                if (edit.getText().length() == 0) {
                    stats.sat_fat = 0;
                } else {
                    stats.sat_fat = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.trans_fat_number);
                if (edit.getText().length() == 0) {
                    stats.trans_fat = 0;
                } else {
                    stats.trans_fat = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.cholesterol_number);
                if (edit.getText().length() == 0) {
                    stats.cholesteral = 0;
                } else {
                    stats.cholesteral = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.sodium_number);
                if (edit.getText().length() == 0) {
                    stats.sodium = 0;
                } else {
                    stats.sodium = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.carbohydrate_number);
                if (edit.getText().length() == 0) {
                    stats.carbs = 0;
                } else {
                    stats.carbs = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.fiber_number);
                if (edit.getText().length() == 0) {
                    stats.fiber = 0;
                } else {
                    stats.fiber = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.sugar_number);
                if (edit.getText().length() == 0) {
                    stats.sugar = 0;
                } else {
                    stats.sugar = serv*Integer.parseInt(edit.getText().toString());
                }
                edit = (EditText)findViewById(R.id.protein_number);
                if (edit.getText().length() == 0) {
                    stats.protein = 0;
                } else {
                    stats.protein = serv*Integer.parseInt(edit.getText().toString());
                }
                fileSystem();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFile().delete();
            }
        });

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature textDetection = new Feature();
                            textDetection.setType("TEXT_DETECTION");
                            textDetection.setMaxResults(10);
                            add(textDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {


                mImageDetails.setText("Values loaded from image. Correct them if necessary.");
                EditText edit = (EditText)findViewById(R.id.calories_number);
                edit.setText(Integer.toString(stats.calories), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.fat_number);
                edit.setText(Integer.toString(stats.fat), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.sat_fat_number);
                edit.setText(Integer.toString(stats.sat_fat), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.trans_fat_number);
                edit.setText(Integer.toString(stats.trans_fat), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.cholesterol_number);
                edit.setText(Integer.toString(stats.cholesteral), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.sodium_number);
                edit.setText(Integer.toString(stats.sodium), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.carbohydrate_number);
                edit.setText(Integer.toString(stats.carbs), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.fiber_number);
                edit.setText(Integer.toString(stats.fiber), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.sugar_number);
                edit.setText(Integer.toString(stats.sugar), TextView.BufferType.EDITABLE);
                edit = (EditText)findViewById(R.id.protein_number);
                edit.setText(Integer.toString(stats.protein), TextView.BufferType.EDITABLE);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void fillNutri(BatchAnnotateImagesResponse response) {
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            int total = 0;
            for (int i = 1; i < labels.size(); i++) {
                if(labels.get(i).getDescription().toLowerCase().equals("calories")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.calories == 0) {
                        stats.calories = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("total")) {
                    i++;
                    if(labels.get(i).getDescription().toLowerCase().equals("fat")) {
                        i++;
                        total = 0;
                        for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                            if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                                total *= 10;
                                total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                            } else {
                                break;
                            }
                        }
                        if(stats.fat == 0) {
                            stats.fat = total;
                        }
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("saturated")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.sat_fat == 0) {
                        stats.sat_fat = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("trans")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.trans_fat == 0) {
                        stats.trans_fat = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("cholesterol")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.cholesteral == 0) {
                        stats.cholesteral = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("sodium")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.sodium == 0) {
                        stats.sodium = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("carbohydrate")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.carbs == 0) {
                        stats.carbs = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("fiber")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.fiber == 0) {
                        stats.fiber = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("sugars")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.sugar == 0) {
                        stats.sugar = total;
                    }
                }
                if(labels.get(i).getDescription().toLowerCase().equals("protein")) {
                    i++;
                    total = 0;
                    for (int j = 0; j < labels.get(i).getDescription().length(); j++) {
                        if (Character.isDigit(labels.get(i).getDescription().charAt(j))) {
                            total *= 10;
                            total += Character.getNumericValue(labels.get(i).getDescription().charAt(j));
                        } else {
                            break;
                        }
                    }
                    if (stats.protein == 0) {
                        stats.protein = total;
                    }
                }
            }
        }
    }

    private File getFile(){
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        Context context = this;
        //filename will be year month day with no spaces
        String filename = year + month + day + ".txt";
        File file = new File(context.getFilesDir(), filename);
        return file;
    }

    private void fileSystem() {
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        Context context = this;
        //filename will be year month day with no spaces
        String filename = year + month + day + ".txt";
        File file = new File(context.getFilesDir(), filename);
        if (file.exists()) {
            try {
                InputStream inputStream = context.openFileInput(filename);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.calories = stats.calories + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.fat = stats.fat + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.sat_fat = stats.sat_fat + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.trans_fat = stats.trans_fat + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.cholesteral = stats.cholesteral + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.sodium = stats.sodium + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.carbs = stats.carbs + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.fiber = stats.fiber + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.sugar = stats.sugar + Integer.parseInt(receiveString);
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        total_stats.protein = stats.protein + Integer.parseInt(receiveString);
                    }
                    inputStream.close();
                    try {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
                        outputStreamWriter.write(total_stats.calories + "\n" +
                                total_stats.fat + "\n" +
                                total_stats.sat_fat + "\n" +
                                total_stats.trans_fat + "\n" +
                                total_stats.cholesteral + "\n" +
                                total_stats.sodium + "\n" +
                                total_stats.carbs + "\n" +
                                total_stats.fiber + "\n" +
                                total_stats.sugar + "\n" +
                                total_stats.protein + "\n");
                        outputStreamWriter.close();
                    }
                    catch (IOException e) {
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
        } else {
            //create file first time
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
                outputStreamWriter.write(stats.calories + "\n" +
                        stats.fat + "\n" +
                        stats.sat_fat + "\n" +
                        stats.trans_fat + "\n" +
                        stats.cholesteral + "\n" +
                        stats.sodium + "\n" +
                        stats.carbs + "\n" +
                        stats.fiber + "\n" +
                        stats.sugar + "\n" +
                        stats.protein + "\n");
                outputStreamWriter.close();
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

        Intent intent = new Intent(this, CompleteActivity.class);
        startActivity(intent);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        fillNutri(response);
        String message = "I found these things:\n\n";
        message += "calories: ";
        message += stats.calories;
        message += "\n";
        message += "fat: ";
        message += stats.fat;
        message += "\n";
        message += "sat_fat: ";
        message += stats.sat_fat;
        message += "\n";
        message += "trans_fat: ";
        message += stats.trans_fat;
        message += "\n";
        message += "cholesteral: ";
        message += stats.cholesteral;
        message += "\n";
        message += "sodium: ";
        message += stats.sodium;
        message += "\n";
        message += "carbs: ";
        message += stats.carbs;
        message += "\n";
        message += "fiber: ";
        message += stats.fiber;
        message += "\n";
        message += "sugar: ";
        message += stats.sugar;
        message += "\n";
        message += "protein: ";
        message += stats.protein;
        message += "\n";
        message += "\n";

        message += "calories: ";
        message += total_stats.calories;
        message += "\n";
        message += "fat: ";
        message += total_stats.fat;
        message += "\n";
        message += "sat_fat: ";
        message += total_stats.sat_fat;
        message += "\n";
        message += "trans_fat: ";
        message += total_stats.trans_fat;
        message += "\n";
        message += "cholesteral: ";
        message += total_stats.cholesteral;
        message += "\n";
        message += "sodium: ";
        message += total_stats.sodium;
        message += "\n";
        message += "carbs: ";
        message += total_stats.carbs;
        message += "\n";
        message += "fiber: ";
        message += total_stats.fiber;
        message += "\n";
        message += "sugar: ";
        message += total_stats.sugar;
        message += "\n";
        message += "protein: ";
        message += total_stats.protein;
        message += "\n";
        message += "\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            for (int i = 1; i < labels.size(); i++) {
                message += String.format(Locale.US, "%s", labels.get(i).getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}
