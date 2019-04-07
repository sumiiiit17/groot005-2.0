package com.example.groot005;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.HashMap;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class AddNewProductActivity extends AppCompatActivity
{

    private  String CategoreName,discription,price,pname,saveCurrentDate,savecurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProdutDiscription,InputProductPrice;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private  String productRandomKey,downloadImageUrl;
    private StorageReference productImagesref;
    private DatabaseReference Productref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        CategoreName=getIntent().getExtras().get("Category").toString();
        productImagesref = FirebaseStorage.getInstance().getReference().child("Product Images");
       Productref = FirebaseDatabase.getInstance().getReference().child("Products");



        AddNewProductButton=(Button)findViewById(R.id.add_new_product);
        InputProductImage=(ImageView) findViewById(R.id.product);
        InputProductImage= (ImageView) findViewById(R.id.product_name);
        InputProdutDiscription=(EditText)findViewById(R.id.product_info);
        InputProductPrice=(EditText)findViewById(R.id.product_price);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        });
        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateproductData();
            }
        });
    }



    private void opengallery()
    {
        Intent galleryIntent= new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode==GalleryPick && requestCode==RESULT_OK && data!=Null)
       {
             ImageUri=data.getData();
             InputProductImage.setImageURI(ImageUri);
       }
    }
    private void validateproductData()
    {

            discription =InputProdutDiscription.getText().toString();
            price =InputProductPrice.getText().toString();
            pname =InputProductName.getText().toString();
            if(ImageUri==Null)
            {
                Toast.makeText(this, "product Image is mandetory..", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(discription))
            {
                Toast.makeText(this, "Please Write Product discription..", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(price))
            {
                Toast.makeText(this, "Please Write Product price..", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(pname))
            {
                Toast.makeText(this, "Please Write Product name..", Toast.LENGTH_SHORT).show();
            }
            else
            {

                storeProductInformation();
            }

    }


    private  void storeProductInformation()
    {
       Calendar    calender = calender.getInstance();
        SimpleDateFormat currentdate =new SimpleDateFormat("MMM DD,YYYY");
        saveCurrentDate=currentdate.format(calender.getTime());

        SimpleDateFormat currentTime =new SimpleDateFormat("HH:MM:ss a");
        savecurrentTime=currentTime.format(calender.getTime());
         productRandomKey=saveCurrentDate + savecurrentTime;

        final StorageReference filpath =productImagesref.child(ImageUri.getLastPathSegment() +productRandomKey +"jpg");
         final UploadTask uploadTask = filpath.putFile(ImageUri);
         uploadTask.addOnFailureListener(new OnFailureListener()
         {
             @Override
             public void onFailure(@NonNull Exception e)
             {
                 String message = e.toString();
                     Toast.makeText(AddNewProductActivity.this, "Error : " +message, Toast.LENGTH_SHORT).show();
             }
         }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
             {
                 Toast.makeText(AddNewProductActivity.this," Product Image Uploaded Successfully..",Toast.LENGTH_SHORT).show();
                 Task<Uri> uriTask= uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                 {
                     @Override
                     public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                     {
                         if(!task.isSuccessful())
                         {
                                throw  task.getException();
                         }
                         downloadImageUrl= filpath.getPath().toString();
                         return filpath.getDownloadUrl();
                     }
                 }).addOnCompleteListener(new OnCompleteListener<Uri>()
                 {
                     @Override
                     public void onComplete(@NonNull Task<Uri> task)
                     {
                              if(task.isSuccessful())
                              {
                                  downloadImageUrl =task.getResult().toString()
                                  Toast.makeText(AddNewProductActivity.this,"got the Product ImageUrl save  Successfully..",Toast.LENGTH_SHORT);
                                  saveproductifTodatabase();
                              }

                     }
                 });
             }
         });


    }

    private void saveproductifTodatabase()
    {
        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("save",savecurrentTime);
        productMap.put("discription",discription);
        productMap.put("image",downloadImageUrl);
        productMap.put("Category",CategoreName);
        productMap.put("price",price);
        productMap.put("pname",pname);

        Productref.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void  onComplete(@NonNull Task<Void> task)
            {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AddNewProductActivity.this,"Product is Added Succesfully..",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String message =task.getException().toString();
                        Toast.makeText(AddNewProductActivity.this,"Error:" +message,Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }
}
