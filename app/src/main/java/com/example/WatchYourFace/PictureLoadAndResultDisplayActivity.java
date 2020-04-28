package com.example.WatchYourFace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author Dust
 */
public class PictureLoadAndResultDisplayActivity extends AppCompatActivity {

    private static final String STRING_FOR_DETECT="性别：Male"+"\n";
    private static final String STRING_FOR_MALE_APPEND="性别：男"+"\n";
    private static final String STRING_FOR_FEMALE_APPEND="性别：女"+"\n";
    private static final String TAG="PictureLoadAndResultDisplayActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pictureload,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.out:
                Toast.makeText(this,"退出",Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
            default:
                break;
        }
        return true;
    }



    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_used);
        ActivityCollector.addActivity(this);

        Intent intent=getIntent();
        String imagePath=intent.getStringExtra("imagePath");

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.VISIBLE);


        Api api=new Api();
        api.setContext(this);
        String backData=null;

        try {
            //API调用
            backData=api.handleImageMethod(imagePath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int faceNumber=0;
        String errorMessage=null;
        if (backData!=null)
        {
            JSONObject jsonObject = JSON.parseObject(backData);
            errorMessage = jsonObject.getString("error_message");
            faceNumber = jsonObject.getIntValue("face_num");
        }

        if(backData==null)
        {
            Toast.makeText(this,"未收到服务器返回数据，请检查您的网络连接状态",Toast.LENGTH_SHORT).show();
            this.finish();
            ActivityCollector.finishByIndex(ActivityCollector.getListOfActivity().indexOf(this));
        }
        else if(errorMessage!=null)
        {
            StringBuilder messageToOut=new StringBuilder();
            switch (errorMessage)
            {
                case "INVALID_IMAGE_URL":
                    messageToOut.append("您选择的图片无效，请重新选择");
                    break;
                case "IMAGE_DOWNLOAD_TIMEOUT":
                    messageToOut.append("网络状态不佳，请确保网络连接通畅");
                    break;
                case "IMAGE_ERROR_UNSUPPORTED_FORMAT":
                    messageToOut.append("图像解析失败，该图片或已损坏，请更换您选择的图片");
                    break;
                case "INTERNAL_ERROR":
                    messageToOut.append("服务错误，请重试。若多次出现该问题，请等待服务维修");
                    break;
                default:
                    break;
            }
            Toast.makeText(PictureLoadAndResultDisplayActivity.this,messageToOut.toString(),Toast.LENGTH_SHORT).show();
            this.finish();
            ActivityCollector.finishByIndex(ActivityCollector.getListOfActivity().indexOf(this));
        }

        else if (faceNumber!=1)
        {
            Toast.makeText(PictureLoadAndResultDisplayActivity.this,"请确保您选择的图像中有且只有一张脸",Toast.LENGTH_SHORT).show();
            this.finish();
            ActivityCollector.finishByIndex(ActivityCollector.getListOfActivity().indexOf(this));
        }

        else
        {
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);

            ImageView imageView=(ImageView)findViewById(R.id.handled_picture);
            imageView.setImageBitmap(bitmap);

            TextView textView=(TextView)findViewById(R.id.data_of_handled_picture);

            assert backData != null;
            JsonDataGet jsonDataGet=new JsonDataGet();
            String[] get=jsonDataGet.jsonDataGet(backData);

            if (get[0].equals(STRING_FOR_DETECT))
            {
                get[0]=STRING_FOR_MALE_APPEND;
            }
            else
            {
                get[0]=STRING_FOR_FEMALE_APPEND;
            }

            StringBuilder x=new StringBuilder();
            String sumOfData;

            final int parameter=6;
            for(int i=0;i<parameter;i++)
            {
                x.append(get[i]);
            }
            sumOfData=x.toString();

            FileOutputStream out=null;
            BufferedWriter writer=null;
            try {
                out=openFileOutput(get[6], Context.MODE_APPEND);
                writer=new BufferedWriter(new OutputStreamWriter(out));
                writer.write(backData);
            }catch (IOException e){e.printStackTrace();}
            finally {
                try {
                    if(writer!=null) {writer.close();}
                }catch (IOException e){e.printStackTrace();}
            }

            progressBar.setVisibility(View.GONE);

            textView.setText(sumOfData);
            Log.d(TAG, "onCreate: "+backData);
            Log.d(TAG, "onCreate: "+sumOfData);
        }
    }
    @Override
    public void onStart()
    {
        super.onStart();
    }

}
