package com.example.hp.hidi2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Accounts extends AppCompatActivity
{
    ProgressBar progressBar;
    ImageView imageView_plus;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        ImageView myImage = (ImageView) findViewById(R.id.image);
        imageView_plus = (ImageView) findViewById(R.id.plus_btn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(50);
        progressBar.setMax(100);
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), myBitmap);
        roundedBitmapDrawable.setCornerRadius(55f);
        myImage.setImageDrawable(roundedBitmapDrawable);
        imageView_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatepopupWindow();
            }
        });
    }
    private PopupWindow popupWindow;

    private void initiatepopupWindow() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) Accounts.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.pop_up_on_plus, (ViewGroup) findViewById(R.id.pop_up_on_plus));
            // create a 300px width and 470px height PopupWindow
            popupWindow = new PopupWindow(layout, 350, 300, true);
            // display the popup in the center
            popupWindow.showAtLocation(layout, Gravity.CENTER_HORIZONTAL, 50, 580);

            TextView textView_text = (TextView) layout.findViewById(R.id.text_text);
            ImageButton imageButton_text = (ImageButton) layout.findViewById(R.id.text_img);
            TextView textView_camera = (TextView) layout.findViewById(R.id.text_camera);
            ImageButton imageButton_camera = (ImageButton) layout.findViewById(R.id.text_camera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
