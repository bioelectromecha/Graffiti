package com.example.roman.graffiti.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by stav on 8/17/2017.
 */
public class ImagesManager {
    private MainActivity.callbackInterface callbackInterface;

    private StorageReference storageRef;
    private FirebaseDatabase database;
    private static int loadedCounter = 0;



    public ImagesManager(final MainActivity.callbackInterface callbackInterface){
        this.callbackInterface = callbackInterface;


        final List<Image> images = new ArrayList<Image>();
        DatabaseReference imageReference = getDatabase().getReference();

        imageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isStart = true;
                for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                    isStart = false;
                    Object note = noteSnapshot.getValue(Object.class);

                    HashMap<String, Object> map = (HashMap<String, Object>) note;
                    if ((map.get("latitude") != null) && (map.get("longitude") != null) && (map.get("imageUrl") != null)){
                        loadedCounter++;
                        final Location location = new Location("s");
                        location.setLatitude((double) (Long) map.get("latitude"));
                        location.setLongitude((double) (Long) map.get("longitude"));

                        String url = "https://firebasestorage.googleapis.com"+(String)map.get("imageUrl")+ "?alt=media";
                        StorageReference storageRef = getStorage().getReferenceFromUrl(url);
                        long ONE_MEGABYTE = 1024 * 1024;

                        //download file as a byte array
                        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                images.add(new Image(bitmap, location));
                                loadedCounter--;
                                if (loadedCounter == 0){
                                    callbackInterface.showItems(images);
                                }
                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("stav", databaseError.getMessage());
            }
        });

    }


    public FirebaseStorage getStorage(){
        return FirebaseStorage.getInstance();
    }

    public  StorageReference getStorageRef(){
        if (storageRef == null){
            storageRef = getStorage().getReference();
        }
        return storageRef;
    }

    public FirebaseDatabase getDatabase(){
        if (database == null){
            database = FirebaseDatabase.getInstance();
        }
        return database;
    }

    public void uploadImage(Bitmap bitmap, final Location location){
        String imageName = Calendar.getInstance().getTimeInMillis() + ".jpg";
        StorageReference mountainsRef = getStorageRef().child(imageName);
//        imageView.setDrawingCacheEnabled(true);
//        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//        imageView.buildDrawingCache();
//        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("stav", "bad");

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contaWins file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                assert downloadUrl != null;
                saveInDB(downloadUrl.getPath(), location, "s" + Calendar.getInstance().getTimeInMillis());

            }
        });
    }

    private void saveInDB(String imageUrl,Location location,String name) {
        DatabaseReference imageReference = getDatabase().getReference(name + "/imageUrl");
        imageReference.setValue(imageUrl);
        DatabaseReference latitudeReference = getDatabase().getReference(name + "/latitude");
        latitudeReference.setValue(location.getLatitude());
        DatabaseReference longitudeReference = getDatabase().getReference(name + "/longitude");
        longitudeReference.setValue(location.getLongitude());
    }

//    public static List<Image> getImages(){
//
//    }
}
