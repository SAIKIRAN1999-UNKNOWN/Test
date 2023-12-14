package com.example.mytestpeoject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytestpeoject.Utils.BitmapScalingUtil;
import com.example.mytestpeoject.Utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.jvm.Throws;

public class ocr_capture_activity extends AppCompatActivity {

    private TextView extractedText;
    Context context;
    Uri imageUri;
    File photoFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_capture);
        context = ocr_capture_activity.this;
        extractedText = (TextView)findViewById(R.id.tv_extracted_text);
        findViewById(R.id.btn_launch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    startCameraForOcr();
                }else{
                    ActivityCompat.requestPermissions(ocr_capture_activity.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
    }

    public void startCameraForOcr(){
        try {
            imageUri = null;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            ContentValues values = new ContentValues();
            String p1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/";
            File path = new File(p1);
            String abbPath = path.getAbsolutePath() + "/capture_image.jpg";
            photoFile = new File(abbPath);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            takePictureIntent.putExtra(
                    MediaStore.EXTRA_SCREEN_ORIENTATION,
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            );
            startActivityForResult(takePictureIntent, 101);
//        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            startCameraForOcr();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_OK){
            if(resultCode == 101){
                if(imageUri == null)
                    return;

                try {
                    Bitmap bitmap = BitmapScalingUtil.bitmapFromUri(context,imageUri);
                    uploadImage(bitmap);
                    int count;
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                        count = getContentResolver().delete(imageUri,null,null);
                    }else{
                        count = getContentResolver().delete(imageUri,null);
                    }
                    Utils.logD(count+"");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    void uploadImage(Bitmap bitmap){
        if(bitmap != null){
            try {
                Bitmap image = resizeBitmap(bitmap);
                if(image != null){
                    //paid ocr

                    //free ocr
                    runTextRecognition(image);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public Bitmap resizeBitmap(Bitmap bitmap) {
        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth =
                    (resizedHeight * originalWidth / originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight =
                    (resizedWidth * originalHeight/ originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
    private void runTextRecognition(Bitmap bitmap) {
        if (bitmap != null) {

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            //            mTextButton.setEnabled(false);
            recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                @Override
                public void onSuccess(Text text) {
                    if(checkOcrConfidence(text)) {
                        Utils.logE("confidence check passed ");
                        processTextRecognitionResult(text);
                    }
                    else {
                        Utils.logE("confidence check failed ");
                        callCloudVision(bitmap);//for hand written where ocr performance is very low
                        processTextRecognitionResult(text);//can comment this when paid ocr key is recieved
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }
    }

    private void callCloudVision( Bitmap bitmap) {
//        resultTextView.setText("Retrieving results from cloud")
        String ocr_key = "";//need to generate ocr key from google ml ket website and it is paid and costly
        if(!ocr_key.isEmpty()) {
            new uploadTask(ocr_key).execute();

        }else{
            Toast.makeText(context,"OCR Key Expired",Toast.LENGTH_LONG).show();
        }
    }

    class uploadTask extends AsyncTask<Object,Object,String>{
        private String ANDROID_CERT_HEADER = "X-Android-Cert";
        private String ANDROID_PACKAGE_HEADER = "X-Android-Package";
        String ocr_key;
        uploadTask(String ocr_key){
            this.ocr_key = ocr_key;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Object... objects) {
            /*try {
                VisionRequestInitializer requestInitializer = new VisionRequestInitializer(ocr_key){
                    *//**
                     * Initializes Vision request.
                     *
                     * <p>
                     * Default implementation does nothing. Called from
                     * {@link #initializeJsonRequest(AbstractGoogleJsonClientRequest)}.
                     * </p>
                     *
                     * @param request
                     * @throws IOException I/O exception
                     *//*
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> request) throws IOException {
                        super.initializeVisionRequest(request);
                        String packageName = packageName;
                        request.getRequestHeaders()[ANDROID_PACKAGE_HEADER] =
                                packageName;
                        val sig: String =
                                PackageManagerUtils.getSignature(packageManager, packageName)
                        visionRequest.requestHeaders[ANDROID_CERT_HEADER] =
                                sig
                    }
                };



                val httpTransport = AndroidHttp.newCompatibleTransport()
                val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()
                val builder = Vision.Builder(httpTransport, jsonFactory, null)
                val vision = builder.setVisionRequestInitializer(requestInitializer).build()
                val featureList: MutableList<Feature> = ArrayList()

                val textDetection = Feature()
                textDetection.type = "TEXT_DETECTION"
                textDetection.maxResults = 10
                featureList.add(textDetection)


                val imageList: MutableList<AnnotateImageRequest> = ArrayList()
                val annotateImageRequest = AnnotateImageRequest()
                val base64EncodedImage: Image? = getBase64EncodedJpeg(bitmap)!!
                        annotateImageRequest.image = base64EncodedImage
                annotateImageRequest.features = featureList

                val imageContext = ImageContext()
                        .setLanguageHints(listOf("en"))
                annotateImageRequest.imageContext = imageContext

                imageList.add(annotateImageRequest)
                val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
                batchAnnotateImagesRequest.requests = imageList
                val annotateRequest = vision.images().annotate(batchAnnotateImagesRequest)
                // Due to a bug: requests to Vision API containing large images fail when GZipped.
                annotateRequest.disableGZipContent = true
                Log.d("LOG_TAG", "sending request")
                val response = annotateRequest.execute()
                return convertResponseToString(response, isconsignor)
            } catch (e: GoogleJsonResponseException) {
                Log.e("LOG_TAG", "Request failed: " + e.content)
            } catch (IOException e) {
                Log.d("LOG_TAG", "Request failed: " + e.message);
            }*/
            return "Cloud Vision API request failed.";

        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            if(o != null){
                processTextRecognitionResult(o);
            }
        }

    }
    public boolean checkOcrConfidence(Text texts){
        int lines60Below = 0;
        int lines70Below = 0;
        int size = 0;
        for(Text.TextBlock i : texts.getTextBlocks()) {
            for(Text.Line j  : i.getLines()){
                Utils.logE(j.getConfidence()+"");
                if(j.getConfidence() < 0.60){
                    lines60Below ++;
                }else if(j.getConfidence() < 70){
                    lines70Below ++;
                }
                size ++;
            }
        }

        if(lines60Below > 1)
            return false;
        else return lines70Below > size/2;
    }

    private void processTextRecognitionResult(String str) {
        String name = "";
        String pincode = "";
        String address1 = "";
        String address2 = "";
        String mobile = "";
        List<String> addressList = new ArrayList();

        if (!str.isEmpty()) {
            List<String> list = Arrays.asList(str.split("\n"));
            int i = -1;
            String pin = "";
            String phone = "";
            String fromto = "";

            for (String s : list) {

                Matcher phoneMatcher = Pattern.compile("(\\+\\d{1,3}[- ]?)?\\d{10}").matcher(s.replace(" ", ""));

                if (phoneMatcher.find()) {
                    phone = s;
                    mobile = phoneMatcher.group();
                }

                if(!s.trim().contains("ph") && !s.trim().contains("mob")) {
                    Matcher pinMatcher =
                            Pattern.compile("([1-9]{1}[0-9]{5}|[1-9]{1}[0-9]{3}\\\\s[0-9]{3})")
                                    .matcher(s.replace(" ", ""));
                    if (pinMatcher.find()) {
                        if(!mobile.contains(pinMatcher.group())) {
                            pin = s;
                            pincode = pinMatcher.group();
                        }
                    }
                }

                if (s.trim().contains("From")) {
                    i = list.indexOf(s);
                    fromto = s;
                    String[] arr = s.split(" ");
                    if(arr.length == 1) {
                        i += 1;
                        if(name.isEmpty()) {
                            if (list.size()-1 > i) {
                                name = list.get(i);
                            }
                        }
                    }
                    else{
                        if(name.isEmpty())
                            name = arr[1];
                    }

                }
                else if (s.trim().contains("To")) {
                    i = list.indexOf(s);
                    fromto = s;
                    String[] arr = s.split(" ");
                    if(arr.length == 1) {
                        i += 1;
                        if(name.isEmpty()) {
                            if (list.size()-1 > i) {
                                name = list.get(i);
                            }
                        }
                    } else{
                        if(name.isEmpty())
                            name = arr[1];
                    }
                }

            }

            list.remove(pin);
            list.remove(phone);
            list.remove(fromto);
            list.remove(name);

            if(i == -1 && !list.isEmpty()) {
                i = 0;
                name = list.get(0);
            }

            for (int a = 0; a <list.size();a++) {

                if (!(list.get(a).contains(pincode) && list.get(a).contains(mobile) && list.get(a).contains(name) && list.get(a).length() <3
                        && (list.get(a).contains("From") || list.get(a).contains("To"))
                )) {
                    addressList.add(list.get(a));
                }
            }

            name.replace(":","");

            if(name.contains("To")) {
                name.replace("To","");
            }

            if(name.contains("From")){
                name.replace("From","");
            }

            if(!addressList.isEmpty()) {
                if(addressList.size() == 1) {
                    address1 = addressList.get(0);
                } else if(addressList.size() == 2){
                    address1 = addressList.get(0);
                    address2 = addressList.get(1);
                }else{
                    for (int e = 0 ; e<= addressList.size() / 2;e++)
                    address1 += addressList.get(e);
                    for(int e = (addressList.size()/2)+1 ;e<  addressList.size();e++)
                    address2 += addressList.get(e);
                }
            }

            System.out.println("Add1 :- "+address1);

            System.out.println("Add2 :- "+address2);

            for(String e : addressList)
                address1 +=  e;

            String output =
                    "Name = "+name+"\n"+
                    "Pincode = "+pincode+"\n"+
                    "Mobile Number = "+mobile+"\n"+
                    "Address = "+address1;


            extractedText.setText(output);
        }
    }
    private void processTextRecognitionResult(Text texts) {
        // Replace with code from the codelab to process the text recognition result.
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(context,"No text found",Toast.LENGTH_LONG).show();
            return;
        }
        String str = "";
        //        mGraphicOverlay.clear();
        for (Text.TextBlock i : blocks) {
            List<Text.Line> lines = i.getLines();
            for (Text.Line j : lines) {
                List<Text.Element> elements = j.getElements();
                for (Text.Element k : elements) {
//                    GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
//                    mGraphicOverlay.add(textGraphic);
                    str = str + k.getText();
                }
                str = str+" \n";
            }
        }

        String name = "";
        String pincode = "";
        String address1 = "";
        String address2 = "";
        String mobile = "";
        List<String> addressList = new ArrayList();

        if (!str.isEmpty()) {
            List<String> list = Arrays.asList(str.split("\n"));
            int i = -1;
            String pin = "";
            String phone = "";
            String fromto = "";

            for (String s : list) {

                Matcher phoneMatcher = Pattern.compile("(\\+\\d{1,3}[- ]?)?\\d{10}").matcher(s.replace(" ", ""));

                if (phoneMatcher.find()) {
                    phone = s;
                    mobile = phoneMatcher.group();
                }

                if(!s.trim().contains("ph") && !s.trim().contains("mob")) {
                    Matcher pinMatcher =
                            Pattern.compile("([1-9]{1}[0-9]{5}|[1-9]{1}[0-9]{3}\\\\s[0-9]{3})")
                                    .matcher(s.replace(" ", ""));
                    if (pinMatcher.find()) {
                        if(!mobile.contains(pinMatcher.group())) {
                            pin = s;
                            pincode = pinMatcher.group();
                        }
                    }
                }

                if (s.trim().contains("From")) {
                    i = list.indexOf(s);
                    fromto = s;
                    String[] arr = s.split(" ");
                    if(arr.length == 1) {
                        i += 1;
                        if(name.isEmpty()) {
                            if (list.size()-1 > i) {
                                name = list.get(i);
                            }
                        }
                    }
                    else{
                        if(name.isEmpty())
                            name = arr[1];
                    }

                }
                else if (s.trim().contains("To")) {
                    i = list.indexOf(s);
                    fromto = s;
                    String[] arr = s.split(" ");
                    if(arr.length == 1) {
                        i += 1;
                        if(name.isEmpty()) {
                            if (list.size()-1 > i) {
                                name = list.get(i);
                            }
                        }
                    } else{
                        if(name.isEmpty())
                            name = arr[1];
                    }
                }

            }

            list.remove(pin);
            list.remove(phone);
            list.remove(fromto);
            list.remove(name);

            if(i == -1 && !list.isEmpty()) {
                i = 0;
                name = list.get(0);
            }

            for (int a = 0; a <list.size();a++) {

                if (!(list.get(a).contains(pincode) && list.get(a).contains(mobile) && list.get(a).contains(name) && list.get(a).length() <3
                        && (list.get(a).contains("From") || list.get(a).contains("To"))
                )) {
                    addressList.add(list.get(a));
                }
            }

            name.replace(":","");

            if(name.contains("To")) {
                name.replace("To","");
            }

            if(name.contains("From")){
                name.replace("From","");
            }

            if(!addressList.isEmpty()) {
                if(addressList.size() == 1) {
                    address1 = addressList.get(0);
                } else if(addressList.size() == 2){
                    address1 = addressList.get(0);
                    address2 = addressList.get(1);
                }else{
                    for (int e = 0 ; e<= addressList.size() / 2;e++)
                        address1 += addressList.get(e);
                    for(int e = (addressList.size()/2)+1 ;e<  addressList.size();e++)
                        address2 += addressList.get(e);
                }
            }

            System.out.println("Add1 :- "+address1);

            System.out.println("Add2 :- "+address2);

            for(String e : addressList)
                address1 +=  e;

            String output =
                    "Name = "+name+"\n"+
                            "Pincode = "+pincode+"\n"+
                            "Mobile Number = "+mobile+"\n"+
                            "Address = "+address1;


            extractedText.setText(output);
//            if (isconsignor) {
//                pickupRegistrationBinding.pickupIncludeConsigneeViewCustomer.tvConsignorName.setText(name)
//                pickupRegistrationBinding.pickupIncludeConsigneeViewCustomer.tvConsignorAdderess.setText(address1)
//                pickupRegistrationBinding.pickupIncludeConsigneeViewCustomer.tvOriginPincode.setText(pincode)
//                pickupRegistrationBinding.pickupIncludeConsigneeViewCustomer.tvContactPhoneNum.setText(mobile)
//            } else {
//                pickupRegistrationBinding.includepickupDestinationDetails.edtPincodeDest.setText(pincode)
//                pickupRegistrationBinding.includepickupDestinationDetails.tvConsigneeName.setText(name)
//                pickupRegistrationBinding.includepickupDestinationDetails.tvConsigneeAddress.setText(address1)
//                pickupRegistrationBinding.includepickupDestinationDetails.tvContactPhoneNum.setText(mobile)
//            }
        }
    }
}