package kr.ac.pknu.sme.myvoiceproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.pknu.sme.myvoiceproject.R;

public class CalActivity extends Activity {

    Button mButton;
    EditText mEdit;
    TextView mText;
    TextView Hz;
    TextView desc;

    private final double A = Math.pow(2, 1.0 / 12.0);
    private final double A4 = 440;
    private final double C4 = 261.626;
    private final String[] scale = {"C", "C♯/D♭", "D", "D♯/E♭", "E", "F",
            "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B"};

    private String note = "";
    private Boolean cont = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        mButton = (Button) findViewById(R.id.mButton);
        mEdit = (EditText) findViewById(R.id.mEdit);
        mText = (TextView) findViewById(R.id.mText);
        Hz = (TextView) findViewById(R.id.textView1);
        desc = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        Double max = intent.getExtras().getDouble("max");
        Double min = intent.getExtras().getDouble("min");



        Log.v("TTTTT", "max 인텐트 확인 " + max);
        Log.v("TTTTT", "min 인텐트 확인 " + min);

        TextView max_view_cal = (TextView) findViewById(R.id.max_View_Cal);
        max_view_cal.setText("" + max);


        TextView min_view_cal = (TextView) findViewById(R.id.min_View_Cal);
        min_view_cal.setText("" + min);

        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cont = false;
                double freq = 0.0;
                try {
                    String s = mEdit.getText().toString();
                    if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
                    freq = Double.parseDouble(s);
                    cont = true;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }
                if (cont) {
                    mText.setText(findNote(findSteps(freq)) + findOctave(findSteps(freq)));
                    Log.v("123123", "findStep 확인 : " + findNote(findSteps(freq)) + findOctave(findSteps(freq)));
                }
            }
        });
    }

    public int findSteps(double fn) {
        double n = (Math.log(fn / C4) / Math.log(A));
        return (int) n;
    }

    public String findNote(int n) {
        if (n > 0) {
            return scale[(n + 1) % scale.length];
        } else if (n == 0) {
            return "C";
        } else {
            return scale[scale.length - (Math.abs(n) % scale.length)];
        }
    }

    public int findOctave(int n) {
        if (n > 0) {
            return 4 + ((n + 1) / scale.length);
        } else if (n == 0) return 4;
        else {
            return 3 + ((n + 1) / scale.length);
        }
    }
}