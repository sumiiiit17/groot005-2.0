package com.example.groot005;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.time.Instant;

public class AcatagoryActivity extends AppCompatActivity {
    private ImageView Books,Notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acatagory);
        Books =(ImageView)findViewById(R.id.Books);
        Notes =(ImageView)findViewById(R.id.Notes);
        Books.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instant instant= new Instant(AcatagoryActivity.this,AddNewProductActivity.class)
                        intent.putextra(name:"category",value:"Books");
                startActivity(instant);
            }
        });
        Notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Instant instant= new Instant(AcatagoryActivity.this,AddNewProductActivity.class)
                intent.putextra(name:"category",value:"Notes");
                startActivity(instant);
            }
        });
}
