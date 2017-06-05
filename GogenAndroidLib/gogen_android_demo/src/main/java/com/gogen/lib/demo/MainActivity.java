package com.gogen.lib.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gogen.lib.demo.pictureselector.PicSelMainActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_main);

        Button main_pic_sel_btn = (Button) findViewById(R.id.main_pic_sel_btn);
        main_pic_sel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PicSelMainActivity.class);
                startActivity(intent);
            }
        });

    }


}
