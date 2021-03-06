package com.example.hp.hidi2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class VerifyOtp extends AppCompatActivity
{
    AdapterViewFlipper adapterFlipper;
    SessionManager session;
    EditText n1,n2,n3,n4,n5,n6;
    ProgressDialog progress;
    Button log,verifi;
    String otp="",mobile="",result="",user_name="",user_password="";
    int request=0,mPosition=-1;
    ImageView checking;
    String fcmid;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    MyFirebaseInstanceIDService myFirebaseInstanceIDService;
    String texts[]={"Taste","Fuck","Love","Hidden","Look","Fear","Passion","Dreams","Suck","Adventure","Death",
                    "Friendship","Revenge","Lust","Freedom","Respect","Joke","Trust","Enemy"};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(isNetworkAvailable()){

        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage("No internet connection on your device. Would you like to enable it?")
                    .setTitle("No Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton(" Enable Internet ",
                            new DialogInterface.OnClickListener()
                            {

                                public void onClick(DialogInterface dialog, int id)
                                {


                                    Intent in = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(in);

                                }
                            });

            alertDialogBuilder.setNegativeButton(" Cancel ", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();
                }
            });

            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.onTokenRefresh();
        fcmid = myFirebaseInstanceIDService.refreshedToken;
        session=new SessionManager(getApplicationContext());
        log=findViewById(R.id.login);
        adapterFlipper=findViewById(R.id.adapterFlipper);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        progress=new ProgressDialog(this);
        progress.setMessage("Verifying....");
        progress.setCancelable(false);
        progress.setIndeterminate(false);
        log.setText("Register");
        FlipperAdapter adapter=new FlipperAdapter(this,texts);
        adapterFlipper.setAdapter(adapter);
        adapterFlipper.setAutoStart(true);
        log.setVisibility(View.INVISIBLE);
        checking=findViewById(R.id.check_img);
        Bundle bundle=getIntent().getExtras();
        request=bundle.getInt("request");
        if(request==1)
        {
            log.setText("Login");
            mobile=bundle.getString("mobile");
        }
        else if(request==21)
        {
            mobile=bundle.getString("mobile");
            user_name=bundle.getString("username");
        }
        else if(request==22)
        {
            user_name=bundle.getString("username");
            mobile=bundle.getString("mobile");
            user_password=bundle.getString("password");
        }
        n1=findViewById(R.id.no1);
        n2=findViewById(R.id.no2);
        n3=findViewById(R.id.no3);
        n4=findViewById(R.id.no4);
        n5=findViewById(R.id.no5);
        n6=findViewById(R.id.no6);
        n1.addTextChangedListener(new GenericTextWatcher(n1));
        n2.addTextChangedListener(new GenericTextWatcher(n2));
        n3.addTextChangedListener(new GenericTextWatcher(n3));
        n4.addTextChangedListener(new GenericTextWatcher(n4));
        n5.addTextChangedListener(new GenericTextWatcher(n5));
        n6.addTextChangedListener(new GenericTextWatcher(n6));
        verifi=findViewById(R.id.verify);
        verifi.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.e("entered","clicked ");
                otp=otp+n1.getText().toString()+n2.getText().toString()+n3.getText().toString()+n4.getText().toString()+n5.getText().toString()+n6.getText().toString();
                if(otp.length()!=0)
                {
                    progress.show();
                    if(request==1)
                    {
                        Log.d("req","1");
                        new Verification().execute("http://hidi.org.in/hidi/Auth/verifyotp.php");
                    }
                    else
                    {
                        new Verification().execute("http://hidi.org.in/hidi/Auth/verifyotp2.php");
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"OTP not entered",Toast.LENGTH_SHORT).show();
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
    class FlipperAdapter extends BaseAdapter
    {
        Context ctx;
        String text[];
        LayoutInflater inflater;
        public FlipperAdapter(Context context,String[] text)
        {
            this.ctx=context;
            this.text=text;
            inflater=LayoutInflater.from(context);
        }
        @Override
        public int getCount()
        {
            return text.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            view=inflater.inflate(R.layout.flipper_items,null);
            TextView textView=view.findViewById(R.id.flipperText);
            textView.setText(text[i]);
            return view;
        }
    }
    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equalsIgnoreCase("otp"))
            {
                final String message = intent.getStringExtra("message");
//                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                /*Pattern pattern = Pattern.compile("\\w+([0-9]+)\\w+([0-9]+)");
                Matcher matcher = pattern.matcher(message);
                for(int i = 0 ; i < matcher.groupCount(); i++) {
                    matcher.find();
                    //System.out.println(matcher.group());
                    textView1.setText(matcher.group());
                }*/
                String[] splited = message.split("\\s+");
                String x=splited[0];
                Log.d("split[0]",""+x);
                Log.d("split[0]",""+splited[1]);
                x=x.substring(0,6);
//                textView1.setText(""+x);
                char ch[]=x.toCharArray();
                n1.setText(""+ch[0]);
                n2.setText(""+ch[1]);
                n3.setText(""+ch[2]);
                n4.setText(""+ch[3]);
                n5.setText(""+ch[4]);
                n6.setText(""+ch[5]);

                //Do whatever you want with the code here
            }
        }
    };
    public class GenericTextWatcher implements TextWatcher
    {
        private View view;
        public GenericTextWatcher(View view)
        {
            this.view = view;
        }
        @Override
        public void afterTextChanged(Editable editable) {
            // TODO Auto-generated method stub
            String text = editable.toString();
            switch(view.getId())
            {

                case R.id.no1:
                    if(text.length()==1)
                    {
                        n2.requestFocus();
                    }
                    break;
                case R.id.no2:
                    if(text.length()==1)
                    {
                        n3.requestFocus();
                    }
                    break;
                case R.id.no3:
                    if(text.length()==1)
                    {
                        n4.requestFocus();
                    }
                    break;
                case R.id.no4:
                    if(text.length()==1)
                    {
                        n5.requestFocus();
                    }
                    break;
                case R.id.no5:
                    if(text.length()==1)
                    {
                        n6.requestFocus();
                    }
                    break;
                case R.id.no6:
                    if(text.length()==1)
                    {
                        verifi.requestFocus();
                    }
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        }
    }
    private class Verification extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... url)
        {
            return POST(url[0]);
        }
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Log.d("Result",result);
            //Toast.makeText(VerifyOtp.this,""+result,Toast.LENGTH_LONG).show();
            try
            {
                JSONObject ff=new JSONObject(result);
                JSONObject info=ff.getJSONObject("info");
                String otp_status=info.getString("otpStatus");
                progress.dismiss();
                if(otp_status.compareTo("success")==0)
                {
                    progress.dismiss();
                    checking.setVisibility(View.VISIBLE);
                    log.setVisibility(View.VISIBLE);
                    verifi.setEnabled(false);
                    log.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            progress.setMessage("Loading.....");
                            progress.show();
                            if(request==1)
                            {
                                new HttpAsyncTask().execute("http://hidi.org.in/hidi/Auth/login.php");
                            }
                            else
                            {
                                new HttpAsyncTask().execute("http://hidi.org.in/hidi/Auth/register.php");
                            }
                        }
                    });
                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(VerifyOtp.this,info.getString("otpMessage"), Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }
    private class HttpAsyncTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... urls)
        {

            return POST1(urls[0]);
        }
        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Log.d("Result",result);
            try
            {
                JSONObject res=new JSONObject(result);
                JSONObject info=res.getJSONObject("info");
                String status=info.getString("status");
                if(status.equals("success"))
                {
                    progress.dismiss();
                    JSONObject recordds=res.getJSONObject("records");
                    int uid=recordds.getInt("UID");
                    databaseReference.child("users").child(uid+"").child("fcm_token").setValue(fcmid);
                    if(request==1)
                    {
                        session.createLoginSession(uid,mobile,"login");
                        Intent intent=new Intent(VerifyOtp.this,PostActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Log.d("UID",""+uid);
                        Bundle bundle=new Bundle();
                        bundle.putInt("UID",uid);
                        session.createLoginSession(uid,mobile,"register");
                        Intent intent=new Intent(VerifyOtp.this,SelectName.class);
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(VerifyOtp.this,info.getString("message"),Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
    public String POST(String url)
    {
        InputStream inputStream=null;
        String json="";
        result="";
        try
        {

            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(url);
            JSONObject jsonObject=new JSONObject();
            jsonObject.accumulate("otp",Integer.parseInt(otp));
            jsonObject.accumulate("mobileno",mobile);
            json=jsonObject.toString();
            Log.d("json",json);
            StringEntity se=new StringEntity(json);
            Log.d("Entity",""+se);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.d("Post",""+httpPost);
            HttpResponse httpResponse=httpClient.execute(httpPost);
            Log.d("Response",httpResponse.toString());
            inputStream=httpResponse.getEntity().getContent();
            Log.d("inputStream",inputStream.toString());
            if(inputStream!=null)
                result=convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public  String POST1(String url)
    {
        result="";
        InputStream inputStream=null;
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(url);
            String json="";
            JSONObject jsonObject=new JSONObject();
            if(request==1)
            {
                jsonObject.accumulate("mobileno",mobile);
            }
            else if(request==21)
            {
                jsonObject.accumulate("mobileno",mobile);
                jsonObject.accumulate("username",user_name);
            }
            else
            {
                jsonObject.accumulate("mobileno",mobile);
                jsonObject.accumulate("username",user_name);
                jsonObject.accumulate("password",user_password);
            }
            jsonObject.accumulate("fcmid",fcmid);
//            JSONObject jss=new JSONObject();
//            jss.accumulate("info",jsonObject);
            json=jsonObject.toString();
            Log.d("Json",json);
            StringEntity se=new StringEntity(json);
            Log.d("Entity",""+se);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            Log.d("Post",""+httpPost);
            HttpResponse httpResponse=httpClient.execute(httpPost);
            Log.d("Response",httpResponse.toString());
            inputStream=httpResponse.getEntity().getContent();
            Log.d("inputStream",inputStream.toString());
            if(inputStream!=null)
                result=convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }
}
