package com.example.WatchYourFace;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private ImageView picture;
    private Uri imageUri;

    private static final String TAG="mainactivity";
    private static String NAME_OF_NEW_IMAGE;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        checkAndDelete();
        //ActivityCollector.finishAll();
    }

    //菜单生成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    //菜单按钮定义
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.out:
                Toast.makeText(this,"退出",Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
            case R.id.checkprompt:
                promptShow();
                break;
            default:
                break;
        }
        return true;
    }

    //显示注意事项
    private void promptShow()
    {

        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("提示");

        AssetsOperate assetsOperate=new AssetsOperate();
        AssetManager assetManager=getAssets();
        dialog.setMessage(assetsOperate.textFileGet("promptMessage.txt",assetManager));
        dialog.setCancelable(false);
        dialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ss");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCollector.addActivity(this);

        picture=(ImageView)findViewById(R.id.picture);

        //如果是第一次使用本软件，展示新手提示
        if(firstOrNot()){promptShow();}

//        //提前检查存储权限
//        if(ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//        }



        //拍照按钮点击事件
        Button takePhoto=(Button)findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAndDelete();

                File outputImage=null;
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }


                PictureHandle pictureHandle=new PictureHandle();
                NAME_OF_NEW_IMAGE="/"+pictureHandle.generateRandomFilename()+".jpg";
                fileNameWriteDown(NAME_OF_NEW_IMAGE);
                outputImage=new File(getExternalCacheDir(),NAME_OF_NEW_IMAGE);


                try
                {
                    assert outputImage != null;
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24)
                {
                    imageUri= FileProvider.getUriForFile(MainActivity.this,
                            "com.example.WatchYourFace.fileprovider",outputImage);
                }
                else
                {
                    imageUri=Uri.fromFile(outputImage);
                }

                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);

            }
        });

        //从相册选择按钮点击事件
        Button chooseFromAlbum=(Button)findViewById(R.id.choose_from_album);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else
                {
                    openAlbum();
                }
            }
        });
    }

    //检查是否是第一次打开app，以决定是否显示新手提示
    private boolean firstOrNot()
    {
        File firstCheck=new File(getExternalCacheDir(),"/first");
        try {
            if (firstCheck.exists())
            {
                return false;
            }
            else
            {
                firstCheck.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void checkAndDelete()
    {
        SharedPreferences preferences=getSharedPreferences("history",MODE_PRIVATE);
        String name=preferences.getString("historicalName","");
        if(!"".equals(name))
        {
            File file=new File(getExternalCacheDir()+name);
            Log.d(TAG, "checkAndDelete: "+getExternalCacheDir()+name);
            Bitmap bitmap= BitmapFactory.decodeFile(getExternalCacheDir()+name);

            if(bitmap==null)
            {
                file.delete();
            }
        }
    }

    //保存本次生成的文件名，用于下次开启时的检查，保证不生成一大堆的空文件
    private void fileNameWriteDown(String name)
    {
        SharedPreferences.Editor editor=getSharedPreferences("history",MODE_PRIVATE).edit();
        editor.putString("historicalName",name);
        editor.apply();
    }

    //打开相册
    private void openAlbum()
    {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    //动态权限获取
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openAlbum();
                }
                else
                {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    //调用相机activity和相册activity后接受返回数据
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    try
                    {
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        if(bitmap.getWidth()!=0)
                        {
                            picture.setImageBitmap(bitmap);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(getExternalCacheDir()+NAME_OF_NEW_IMAGE))));
                            displayImage(getExternalCacheDir()+NAME_OF_NEW_IMAGE);
                        }
                        else
                        {
                            File file=new File(getExternalCacheDir()+NAME_OF_NEW_IMAGE);
                            file.delete();
                            Intent intent=new Intent();
                            startActivity(intent);
                        }

                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    File file=new File(getExternalCacheDir()+NAME_OF_NEW_IMAGE);
                    file.delete();
                }
                break;
            case CHOOSE_PHOTO:
                //处理相册activity返回数据
                if(resultCode==RESULT_OK)
                {
                    if(Build.VERSION.SDK_INT>=19)
                    {
                        handleImageOnKitKat(data);
                    }
                    else
                    {
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }

    @TargetApi(19)
    //Android4.4以上版本图片处理
    private void handleImageOnKitKat(Intent data)
    {
        String  imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri))
        {
            String docId=DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if("com.android.providers.media.documents".equals(uri.getAuthority()))
            {
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority()))
            {
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else {
            assert uri != null;
            if("content".equalsIgnoreCase(uri.getScheme()))
            {
                imagePath=getImagePath(uri,null);
            }
            else if("file".equalsIgnoreCase(uri.getScheme()))
            {
                imagePath=uri.getPath();
            }
        }
        displayImage(imagePath);
    }

    //Android4.4以下版本图片处理
    private void handleImageBeforeKitKat(Intent data)
    {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection)
    {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //从相册选择图片后进行显示
    private void displayImage(String imagePath)
    {
        if(imagePath!=null)
        {
            Log.d(TAG, "displayImage: "+ imagePath);
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            Log.d(TAG, "displayImage: NoNo");

            Intent intent=new Intent(MainActivity.this, PictureLoadAndResultDisplayActivity.class);
            intent.putExtra("imagePath",imagePath);
            startActivity(intent);

        }
        else
        {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }
}

