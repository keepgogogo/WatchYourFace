package com.example.WatchYourFace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Random;

/**
 * @author Dust
 */
public class PictureHandle extends AppCompatActivity {

    PictureHandle(){super();}
    private static final int DEFAULT_IGNORE_MINLEN=100;

    byte[] thumbnailHandleMethod(String imagepath)
    {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imagepath,options);
        int height=options.outHeight;
        int width=options.outWidth;
        int inSampleSize=2;
        int minlen=Math.min(height,width);
        if(minlen>DEFAULT_IGNORE_MINLEN)
        {
            float ratio=(float)minlen/100.0f;
            inSampleSize=(int)(ratio/3.0);
        }
        options.inJustDecodeBounds=false;
        options.inSampleSize=inSampleSize;
        Bitmap bitmap=BitmapFactory.decodeFile(imagepath,options);
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        return out.toByteArray();
    }

    public String generateRandomFilename(){
        String randomFilename;
        //生成随机数
        Random rand = new Random();
        int random = rand.nextInt();

        Calendar calCurrent = Calendar.getInstance();
        int intDay = calCurrent.get(Calendar.DATE);
        int intMonth = calCurrent.get(Calendar.MONTH) + 1;
        int intYear = calCurrent.get(Calendar.YEAR);
        String now = intYear + "_" + intMonth + "_" + intDay + "_";

        randomFilename = now + (random > 0 ? random : (-1) * random);

        return randomFilename;
    }
}
