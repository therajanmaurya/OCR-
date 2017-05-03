package abbyy.ocrsdk.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

public class MainActivity extends Activity {

    String imageFilePath = null;

    TextView resutl;
    Button show;

    private String resultUrl = "result.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveToSDCard();

        resutl = (TextView) findViewById(R.id.OCRTextView);
        show = (Button) findViewById(R.id.OCRbutton);
        show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                new AsyncProcessTask(MainActivity.this).execute(imageFilePath, resultUrl);
            }
        });
    }

    public void updateResults(Boolean success) {
        if (!success)
            return;
        try {
            resutl.setText("");
            StringBuffer contents = new StringBuffer();
            FileInputStream fis = openFileInput(resultUrl);
            try {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null) {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally {
                fis.close();
            }
            displayMessage(contents.toString());
        } catch (Exception e) {
            displayMessage("Error: " + e.getMessage());
        }
    }

    public void displayMessage( String text ) {
        resutl.post( new MessagePoster( text ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }

    class MessagePoster implements Runnable {
        public MessagePoster( String message )
        {
            _message = message;
        }

        public void run() {
            resutl.append( _message + "\n" );
        }

        private final String _message;
    }

    public void saveToSDCard() {
        Bitmap bitmap;
        OutputStream output;

        // Retrieve the image from the res folder
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/OCR/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "ocr.png");

        imageFilePath = file.getAbsolutePath();

        // Show a toast message on successful save
        /*Toast.makeText(MainActivity.this, "Image Saved to SD Card",
                Toast.LENGTH_SHORT).show();*/
        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
