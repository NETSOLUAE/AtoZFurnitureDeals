package com.netsol.atoz.Activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.netsol.atoz.R;
import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by macmini on 10/29/17.
 */

public class ImageCropActivity extends AppCompatActivity implements CropImageView.OnGetCroppedImageCompleteListener, CropImageView.OnSetImageUriCompleteListener  {

    Button done;
    Button cancel;
    String uriString = "";
    private CropImageView mCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        done = (Button) findViewById(R.id.button_crop_done);
        cancel = (Button) findViewById(R.id.button_crop_cancel);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uriString = bundle.getString("ImageUri", "");
            if (!uriString.equalsIgnoreCase("")) {
                Uri cropImage = Uri.parse(uriString);
                mCropImageView.setImageUriAsync(cropImage);
            }
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropped =  mCropImageView.getCroppedImage(500, 500);
                if (cropped != null)
                    HomeActivity.profileAction.onProfileReceived(cropped, uriString);
                    finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {

    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {

    }
}
