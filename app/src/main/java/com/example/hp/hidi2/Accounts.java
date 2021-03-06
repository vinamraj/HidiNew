package com.example.hp.hidi2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.hp.hidi2.SessionManager.KEY_ADMIRE;
import static com.example.hp.hidi2.SessionManager.KEY_BLOCKS;
import static com.example.hp.hidi2.SessionManager.KEY_HIDIES;
import static com.example.hp.hidi2.SessionManager.KEY_LOVE;
import static com.example.hp.hidi2.SessionManager.KEY_POPULARITY;
import static com.example.hp.hidi2.SessionManager.KEY_VISITORS;

public class Accounts extends AppCompatActivity {
    SessionManager session;
    float x1, x2, y1, y2;
    DonutProgress progress;
    byte[] byteArray;
    Uri URI;
    HashMap<String, String> user;
    String[] FILE;
    String ImageDecode;
    ImageView imageView_plus, userdp;
    TextView admire, love, visitors, hidies, blocks, actionbars;
    Button see_notifications, my_journey;
    static int PICK_IMAGE_REQUEST = 1;
    static int CAMERA_REQUEST = 2;
    TextView block_text, txtdovisit;
    TextView visitorview, blockview;
    String mPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String result = "";
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    public int flag = 0;
    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isNetworkAvailable()) {

        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage("No internet connection on your device. Would you like to enable it?")
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton(" Enable Internet ",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {


                                    Intent in = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(in);

                                }
                            });

            alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darkTheme);
        } else {
            setTheme(R.style.AccountsTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != MockPackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session = new SessionManager(getApplicationContext());
//        session.checkLogin();
        toolbar = getSupportActionBar();
        toolbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        toolbar.setCustomView(R.layout.set_middle_title);
        actionbars = findViewById(R.id.actionBarTitles);
        actionbars.setText("Posts");
        TextView abc = findViewById(R.id.clearbtn);
        abc.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userdp = findViewById(R.id.profilepic);
        progress = findViewById(R.id.popularProgress);
        admire = findViewById(R.id.admireCount);
        love = findViewById(R.id.loveCount);
        visitors = findViewById(R.id.visitorsCount);
        hidies = findViewById(R.id.hidiCount);
        visitorview = findViewById(R.id.visitorsbtn);
        blockview = findViewById(R.id.blockbtn);
        blocks = findViewById(R.id.blockCount);
        block_text = findViewById(R.id.block);
        txtdovisit = findViewById(R.id.dovisit);
        see_notifications = findViewById(R.id.notificationButton);
        imageView_plus = findViewById(R.id.statusButton);
        my_journey = findViewById(R.id.journeyButton);
        user = session.getUserDetails();
        admire.setText(user.get(KEY_ADMIRE));
        love.setText(user.get(KEY_LOVE));
        progress.setProgress(Float.parseFloat(user.get(KEY_POPULARITY)));
        visitors.setText(user.get(KEY_VISITORS));
        hidies.setText(user.get(KEY_HIDIES));
        blocks.setText(user.get(KEY_BLOCKS));
        actionbars.setText(session.getSecname());
        //see_notifications.setEnabled(false);
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.index);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), myBitmap);
        roundedBitmapDrawable.setCornerRadius(55f);
        blockview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Accounts.this, BlockActivity.class));
            }
        });
        userdp.setImageDrawable(roundedBitmapDrawable);
        new HttpAsyncTask().execute("http://hidi.org.in/hidi/account/myaccount.php");
        see_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0) {
                    Toast.makeText(Accounts.this, "Enable the notification content", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Accounts.this, AllNotificationsView.class);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showFileChooser()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.putExtra("crop","true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        Log.d("Intent",""+intent);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK)
        {
            Uri selectedMediaUri = data.getData();
            Log.e("uri", selectedMediaUri + "");
            String type = this.getContentResolver().getType(selectedMediaUri);
            Log.e(type + " ddd", selectedMediaUri.getEncodedPath());
            URI = data.getData();
            FILE = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(URI, FILE, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            Log.d("paths",ImageDecode);
            cursor.close();
            try {
                InputStream is=getContentResolver().openInputStream(selectedMediaUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(is);
                selectedImage = getResizedBitmap(selectedImage, 400);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                byteArray = outputStream.toByteArray();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUpload(selectedMediaUri);
        }
        else if(requestCode==CAMERA_REQUEST)
        {
            if(data==null)
            {

            }
            else
            {
                Log.d("Enter ","Checked");
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                byteArray = outputStream.toByteArray();
                cameraUpload(photo);
            }
        }
    }
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private void cameraUpload(final Bitmap pic)
    {
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), byteArray);
        Log.d("Request1", "" + requestFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", ""+session.getUID(), requestFile);
        Log.d("Multipart", "" + body);
        String descriptionString = "hello, this is description speaking";
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + session.getUID());
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
        Log.d("Description", "" + description);
        Call<ResponseBody> call = service.upload(description, body, id);
        session.setPic();
        Log.d("Call service", "" + call);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.v("Upload", "success");
                session.setPic();
                userdp.setImageBitmap(pic);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
    private void imageUpload(final Uri fileUri)
    {
        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
        File file = new File(ImageDecode);
        RequestBody requestFile =RequestBody.create(MediaType.parse("*/*"), byteArray);
        Log.d("Request1",""+requestFile);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", file.getName(), requestFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", ""+session.getUID(), requestFile);
        Log.d("Multipart",""+body);
        String descriptionString = "hello, this is description speaking";
        RequestBody id=RequestBody.create(MediaType.parse("text/plain"),""+session.getUID());
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
        Log.d("Description",""+description);
        Call<ResponseBody> call = service.upload(description, body,id);
        Log.d("Call service",""+call);
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.v("Upload", "success");
                userdp.setImageURI(fileUri);
                session.setPic();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

//            private void imageUpload(final Bitmap pic) {
//        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
////        File file = new File(ImageDecode);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), byteArray);
//        Log.d("Request1", "" + requestFile);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", ""+session.getUID(), requestFile);
//        Log.d("Multipart", "" + body);
//        String descriptionString = "hello, this is description speaking";
//        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + session.getUID());
//        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
//        Log.d("Description", "" + description);
//        Call<ResponseBody> call = service.upload(description, body, id);
//        session.setPic();
//        Log.d("Call service", "" + call);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
//            {
//                Log.v("Upload", "success");
//                session.setPic();
//                userdp.setImageBitmap(pic);
////                userdp.setImageURI(fileUri);
////                new HttpAsyncTask().execute("http://hidi.org.in/hidi/account/myaccount.php");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("Upload error:", t.getMessage());
//            }
//        });
//    }
//    private void imageUpload(final Uri fileUri) {
//        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
//        File file = new File(ImageDecode);
//        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
//        Log.d("Request1", "" + requestFile);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("profile", file.getName(), requestFile);
//        Log.d("Multipart", "" + body);
//        String descriptionString = "hello, this is description speaking";
//        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "" + session.getUID());
//        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
//        Log.d("Description", "" + description);
//        Call<ResponseBody> call = service.upload(description, body, id);
//        Log.d("Call service", "" + call);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Log.v("Upload", "success");
//                userdp.setImageURI(fileUri);
////                new HttpAsyncTask().execute("http://hidi.org.in/hidi/account/myaccount.php");
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.e("Upload error:", t.getMessage());
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (session.getNotificationContentState()) {
            flag = 1;
            menu.findItem(R.id.notifi_content).setChecked(true);
            see_notifications.setEnabled(true);
        }
        if (session.getNotificationState()) {
            flag = 1;
            menu.findItem(R.id.notification).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.createpass) {
            Intent intent = new Intent(this, CreatePassword.class);
            startActivity(intent);
        }
        if (id == R.id.notifi_content) {
            if (item.isCheckable()) {
                if (!item.isChecked()) {
                    flag = 1;
                    item.setChecked(true);
                    session.saveNotificationContentState(true);
                } else {
                    flag = 0;
                    item.setChecked(false);
                    session.saveNotificationContentState(false);
                }
            }
        }
        if (id == R.id.notification) {
            if (item.isCheckable()) {
                if (!item.isChecked()) {
                    item.setChecked(true);
                    session.saveNotificationState(true);
                } else {
                    item.setChecked(false);
                    session.saveNotificationState(false);
                }
            }
        }
//        if (id == R.id.createpass) {
//            Intent intent = new Intent(this, CreatePassword.class);
//            startActivity(intent);
//        }
//        if (id == R.id.notifi_content) {
//            if (item.isCheckable()) {
//                if (!item.isChecked()) {
//                    flag = 1;
//                    item.setChecked(true);
//                    see_notifications.setEnabled(true);
//                } else {
//                    flag = 0;
//                    item.setChecked(false);
//                    see_notifications.setEnabled(false);
//                }
//            }
//        }
////        if (id == R.id.notification)
////        {
////            if(item.isCheckable())
////            {
////                if(!item.isChecked())
////                {
////                    item.setChecked(true);
////                }
////                else
////                {
////                    item.setChecked(false);
////                }
////            }
////        }
////        if (id == R.id.night)
////        {
////            if(item.isCheckable())
////            {
////                if(!item.isChecked())
////                {
////                    item.setChecked(true);
////                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
////                    restartActivity();
////                }
////                else
////                {
////                    item.setChecked(false);
////                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
////                    restartActivity();
////                }
////            }
////        }
        if (id == R.id.logout) {
            new LogOut().execute("http://hidi.org.in/hidi/Auth/logout.php");
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                if (x1 > x2) {
                    Intent intent = new Intent(Accounts.this, PostActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    onBackPressed();
                }
                break;
            default:
                System.out.println();
        }
        return false;
    }

    public void restartActivity() {
        Intent mIntent = getIntent();
        startActivity(mIntent);
        finish();
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {

            return POST(urls[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Result", result);
            try {
                JSONObject res = new JSONObject(result);
                JSONObject info = res.getJSONObject("info");
                if (info.getString("status").equals("success")) {
                    JSONObject records = res.getJSONObject("records");
                    Picasso.with(getApplicationContext()).load(records.getString("profilepic")).into(userdp);
                    databaseReference.child("users").child(session.getUID() + "").child("profilepic").setValue(records.getString("profilepic"));
                    admire.setText("" + records.getInt("admire"));
                    love.setText("" + records.getInt("love"));
                    visitors.setText("" + records.getInt("visitors"));
                    hidies.setText("" + records.getInt("hidies"));
                    blocks.setText("" + records.getInt("blocks"));
                    progress.setProgress((float) records.getDouble("popularity"));
//                    toolbar.setTitle(records.getString("secname"));
                    actionbars.setText(records.getString("secname"));
                    session.accountDetails(records.getString("profilepic"), records.getString("secname"),
                            records.getInt("admire"), records.getInt("love"), records.getInt("visitors"),
                            records.getDouble("popularity"), records.getInt("hidies"), records.getInt("blocks"), records.getInt("indexpath"));
                } else {
                    user = session.getUserDetails();
                    admire.setText(user.get(KEY_ADMIRE));
                    visitors.setText(user.get(KEY_VISITORS));
                    hidies.setText(user.get(KEY_HIDIES));
                    blocks.setText(user.get(KEY_BLOCKS));
                    love.setText(user.get(KEY_LOVE));
                    progress.setProgress(Float.parseFloat(user.get(KEY_POPULARITY)));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            imageView_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Accounts.this, StatusActivity.class);
                    startActivity(intent);
                }
            });
            userdp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Accounts.this);
                    View view = getLayoutInflater().inflate(R.layout.camera_dialog, null);
                    Button btncamera, btngallery, btncancel;
                    TextView txtcamera, txtgallery, txtcancel;
                    LinearLayout linearLayoutcamera, linearLayoutgallery;
                    btncamera = view.findViewById(R.id.camera);
                    btngallery = view.findViewById(R.id.gallery);
                    txtcamera = view.findViewById(R.id.camera_img);
                    txtgallery = view.findViewById(R.id.gallery_img);
                    linearLayoutcamera = view.findViewById(R.id.linearlayoutcamera);
                    linearLayoutgallery = view.findViewById(R.id.linearlayoutgallery);
                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    btncamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    txtcamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    btngallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser();
                            dialog.dismiss();
                        }
                    });
                    txtgallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser();
                            dialog.dismiss();
                        }
                    });
                    linearLayoutcamera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            dialog.dismiss();
                        }
                    });
                    linearLayoutgallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser();
                            dialog.dismiss();
                        }
                    });

//                    showFileChooser();
                }
            });
            my_journey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Accounts.this, MyJourneyActivity.class);
                    startActivity(intent);
                }
            });
            visitorview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Accounts.this, UnlockVisitors.class));
                }
            });
        }
    }

    private class LogOut extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            return POST1(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject info = jsonObject.getJSONObject("info");
                if (info.getString("status").equals("success")) {
                    session.logoutUser();
                    finish();
                    Toast.makeText(Accounts.this, "" + info.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Accounts.this, "" + info.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String POST1(String url) {
        InputStream inputStream = null;
        String json = "";
        result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("UID", session.getUID());
            json = jsonObject.toString();
            Log.d("json", json);
            StringEntity se = new StringEntity(json);
            Log.d("Entity", "" + se);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.d("Post", "" + httpPost);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("Response", httpResponse.toString());
            inputStream = httpResponse.getEntity().getContent();
            Log.d("inputStream", inputStream.toString());
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String POST(String url) {
        InputStream inputStream = null;
        String json = "";
        result = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("uid", session.getUID());
            jsonObject.accumulate("request", "get");
            json = jsonObject.toString();
            Log.d("json", json);
            StringEntity se = new StringEntity(json);
            Log.d("Entity", "" + se);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.d("Post", "" + httpPost);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.d("Response", httpResponse.toString());
            inputStream = httpResponse.getEntity().getContent();
            Log.d("inputStream", inputStream.toString());
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, PostActivity.class));
        finish();
        super.onBackPressed();
    }
}
