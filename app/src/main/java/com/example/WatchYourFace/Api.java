package com.example.WatchYourFace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class Api {
    private static final String TAG = "Api";

    public Api() {
        super();
    }

    public void setContext(Context context){
    }

    public String handleImageMethod(String filePath) throws Exception {

        Log.d(TAG, "HandleImageMethod: filePath:" + filePath);
        final File file = new File(filePath);
        byte[] buff = getBytesFromFile(file);

        Log.d(TAG, "HandleImageMethod: buff initial data"+buff);
        Log.d(TAG, "HandleImageMethod: buff initial size " + buff.length);
        Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);

        PictureHandle pictureHandle = new PictureHandle();
        buff=pictureHandle.thumbnailHandleMethod(filePath);

        final HashMap<String, String> map = new HashMap<>();
        final HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "AxF15KjERwe2GWKooNk4Cnj5LhbKAJn4");
        map.put("api_secret", "vz98AwClLn6r1S1F213kot-vbORtGjeL");
        map.put("return_attributes", "gender,age,emotion,ethnicity,beauty");
        byteMap.put("image_file", buff);


        Log.d(TAG, "HandleImageMethod: buff end size " + buff.length);
        Log.d(TAG, "HandleImageMethod: buff end data"+buff);

        final MyThread myThread = new MyThread();
        return new String(myThread.setByteMap(byteMap).setMap(map).RunTheThread().getBacd());
    }


    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1) {
                out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
}
