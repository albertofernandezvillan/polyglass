package pt.fraunhofer.pulse.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class BpmView extends TextView {

    public BpmView(Context context) {
        super(context);
        init();
    }

    public BpmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BpmView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private double bpm;

    private Paint circlePaint;
    private ValueAnimator circlePaintAnimator;

    private void init() {
        setNoBpm();

        setTextSize(TypedValue.COMPLEX_UNIT_FRACTION_PARENT, 60f);
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/ds_digital/DS-DIGIB.TTF"));
        setGravity(Gravity.CENTER);

        circlePaint = initCirclePaint();

        circlePaintAnimator = ObjectAnimator.ofInt(circlePaint, "Alpha", 0, 256);
        circlePaintAnimator.setInterpolator(new AccelerateInterpolator());
        circlePaintAnimator.setDuration(1000);
        circlePaintAnimator.setRepeatCount(ValueAnimator.INFINITE);
        circlePaintAnimator.setRepeatMode(ValueAnimator.REVERSE);
        circlePaintAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (getText() == "-" && circlePaint.getAlpha() == 0) {
                    animation.cancel();
                }
            }
        });
        circlePaintAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });
    }

    private Paint initCirclePaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
        p.setAlpha(0);
        p.setStyle(Paint.Style.FILL);
        return p;
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
        long rounded = Math.round(bpm);
        String result;
        if (rounded == 0) {
        	result = "No Pulse Recorded";
        } else {
            result = String.valueOf(rounded);
        }
        setText(result);
        String message;
        if (rounded == 0) {
        	message = "No Pulse Recorded";
        } else {
        	message = "An average heart rate of " + String.valueOf(rounded) + 
        				" beats per minute was recorded.";
        }
        new HttpAsyncTask().execute(message);
    }

    public void setNoBpm() {
        setBpm(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(20, getHeight() / 2f, 10, circlePaint);
        if (getText() != "-" && !circlePaintAnimator.isStarted()) {
            circlePaintAnimator.start();
        }
    }

    public static String sendMail(String message){
        InputStream inputStream = null;
        String result = "";
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("fcbc065b0e72c6ded6bbecb05db4bae8", "aaa5f33c4ff8f8fad068486ab5d63eec"));
            //
        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.setCredentialsProvider(credProvider);
        HttpPost httppost = new HttpPost("https://api.mailjet.com/v3/send/message");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("from", "PolyGlass <polyglass@morris-suzuki.com>"));
            nameValuePairs.add(new BasicNameValuePair("to", "mailjet.test@morris-suzuki.com"));
            nameValuePairs.add(new BasicNameValuePair("subject", "Heart Rate Recording"));
            nameValuePairs.add(new BasicNameValuePair("text", message));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse httpResponse = httpclient.execute(httppost);
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
/* 
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(getActivity().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
                return true;
            else
                return false;  
    }
    */
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... messages) {
 
            return sendMail( messages[0] );
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            //etResponse.setText(result);
       }
    }
}
