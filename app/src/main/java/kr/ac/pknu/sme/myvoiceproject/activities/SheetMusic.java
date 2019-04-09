package kr.ac.pknu.sme.myvoiceproject.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.pknu.sme.myvoiceproject.utils.PitchCalculator;


public class SheetMusic extends android.support.v7.widget.AppCompatImageView {


    private Paint currentPaint;
    public boolean drawRect = false;
    public float left;
    public float top;
    public float right;
    public float bottom;
    Button mButton;
    EditText mEdit;
    TextView mText;
    TextView Hz;
    TextView desc;
    String result;

    private final double A = Math.pow(2, 1.0 / 12.0);
    private final double A4 = 440;
    private final double C4 = 261.626;
    private final String[] scale = {"C", "C♯/D♭", "D", "D♯/E♭", "E", "F",
            "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B"};


    private String note = "";
    private Boolean cont = false;

    public String HzToScale222(String x) {
        cont = false;

        double freq = 0.0;
        try {
            String s = x;
            if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
            freq = Double.parseDouble(s);
            cont = true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
        if (cont) {
            result = (findNote(findSteps(freq)) + findOctave(findSteps(freq)));
        }
        return result;
    }

    public String HzToScaleNote(String x) {
        cont = false;

        double freq = 0.0;
        try {
            String s = x;
            if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
            freq = Double.parseDouble(s);
            cont = true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
        if (cont) {
            result = (findNote(findSteps(freq)));
        }
        return result;
    }

    public String HzToScaleOctave(String x) {
        cont = false;

        double freq = 0.0;
        try {
            String s = x;
            if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
            freq = Double.parseDouble(s);
            cont = true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
        if (cont) {
            result = ("" + findOctave(findSteps(freq)));
        }
        return result;
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


    public SheetMusic(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double average = RecordViewActivity.currentRecord.getRange().getAvg();
        PitchCalculator pitchCalculator = new PitchCalculator();
        pitchCalculator.setPitches(RecordViewActivity.currentRecord.getRange().getPitches());

        Double minPitch = pitchCalculator.getMin();
        Double maxPitch = pitchCalculator.getMax();

        String min = HzToScale222((Double.toString(Math.round(minPitch))));

        String min_note = HzToScaleNote(Double.toString(Math.round(minPitch)));
        String max_note = HzToScaleNote(Double.toString(Math.round(maxPitch)));

        String min_octave = HzToScaleOctave(Double.toString(Math.round(minPitch)));
        String max_octave = HzToScaleOctave(Double.toString(Math.round(maxPitch)));

//        "C", "C♯/D♭", "D", "D♯/E♭", "E", "F",
//                "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B"
        Log.v("VVVV", "check MAX_NOTE : " + max_note + "  check MIN_OCTAVE " + min_octave);


//        canvas.drawCircle(300, 675, 30, currentPaint);
//        canvas.drawCircle(400, 650, 30, currentPaint);
//        canvas.drawCircle(500, 620, 30, currentPaint);
//        canvas.drawCircle(600, 590, 30, currentPaint);
//        canvas.drawCircle(700, 560, 30, currentPaint);
//        canvas.drawCircle(800, 530, 30, currentPaint);
//        canvas.drawCircle(900, 500, 30, currentPaint);


        //갤럭시 그랜드 맥스 - 1280x720 에서



        //최소값 음표 그리기
        if (min_note == "C") {   //도
//            currentPaint.setStrokeWidth(11f);
//            canvas.drawCircle(400, 675, 30, currentPaint);
//            canvas.drawLine(350,675, 450,  675,currentPaint);
            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            currentPaint.setStrokeWidth(11f);
            canvas.drawLine(170, 340, 230, 340, currentPaint);
            canvas.drawCircle(200, 340, 15, currentPaint); //도

        } else if (min_note == "C♯/D♭") {
//            currentPaint.setStrokeWidth(11f);
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯",300,690,currentPaint);
//            canvas.drawCircle(400, 675, 30, currentPaint);
//            canvas.drawLine(350,675, 450,  675,currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawLine(170, 340, 230, 340, currentPaint);
            canvas.drawCircle(200, 340, 15, currentPaint); //도
            canvas.drawText("♯", 130, 360, currentPaint);

        } else if (min_note == "D") {   //레
            //canvas.drawCircle(400, 650, 30, currentPaint);
            canvas.drawCircle(200, 317, 15, currentPaint); //레

        } else if (min_note == "D♯/E♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 300, 675, currentPaint);
//            canvas.drawCircle(400, 650, 30, currentPaint);


            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(200, 317, 15, currentPaint); //레
            canvas.drawText("♯", 130, 337, currentPaint);

        } else if (min_note == "E") {   //미
            //canvas.drawCircle(400, 620, 30, currentPaint);
            canvas.drawCircle(200, 303, 15, currentPaint); //미
        } else if (min_note == "F") {   //파
            //canvas.drawCircle(400, 590, 30, currentPaint);
            canvas.drawCircle(200, 288, 15, currentPaint); //파
        } else if (min_note == "F♯/G♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 300, 615, currentPaint);
//            canvas.drawCircle(200, 590, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(200, 288, 15, currentPaint); //파
            canvas.drawText("♯", 130, 308, currentPaint);

        } else if (min_note == "G") {   //솔
            //canvas.drawCircle(400, 560, 30, currentPaint);
            canvas.drawCircle(200, 271, 15, currentPaint); //솔
        } else if (min_note == "G♯/A♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 300, 585, currentPaint);
//            canvas.drawCircle(200, 560, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(200, 271, 15, currentPaint); //솔
            canvas.drawText("♯", 130, 291, currentPaint);

        } else if (min_note == "A") {   //라
            //canvas.drawCircle(400, 530, 30, currentPaint);
            canvas.drawCircle(200, 258, 15, currentPaint); //라
        } else if (min_note == "A♯/B♭") {


//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 300, 560, currentPaint);
//            canvas.drawCircle(200, 530, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(200, 258, 15, currentPaint); //라
            canvas.drawText("♯", 130, 278, currentPaint);

        } else if (min_note == "B") {   //시
            //canvas.drawCircle(400, 500, 30, currentPaint);
            canvas.drawCircle(200, 242, 15, currentPaint); //시
        }
        else {
            canvas.drawText("범위를 초과했습니다.", 400, 100, currentPaint);
        }

//        else if (min_note == "C"&& min_octave == "3") {   //도
//            canvas.drawCircle(300, 470, 30, currentPaint);
//        }
//        else if (min_note == "D"&& min_octave == "3") {   //레
//            canvas.drawCircle(300, 440, 30, currentPaint);
//        }
//        else if (min_note == "E"&& min_octave == "3") {   //미
//            canvas.drawCircle(300, 410, 30, currentPaint);
//        }
//        else if (min_note == "F"&& min_octave == "3") {   //파
//            canvas.drawCircle(300, 390, 30, currentPaint);
//        }
//        else if (min_note == "G"&& min_octave == "3") {   //솔
//            canvas.drawCircle(300, 470, 30, currentPaint);
//        }
//        else if (min_note == "A"&& min_octave == "3") {   //라
//            canvas.drawCircle(300, 470, 30, currentPaint);
//        }
//        else if (min_note == "B" && min_octave == "3") {   //시
//            canvas.drawCircle(300, 470, 30, currentPaint);
//        }
//        else if (min_note == "C"&& min_octave == "4") {   //도
//            canvas.drawCircle(300, 470, 30, currentPaint);
//        }

        //최대값 음표 그리기
        if (max_note == "C") {   //도
//            currentPaint.setStrokeWidth(11f);
//            canvas.drawCircle(700, 675, 30, currentPaint);
//            canvas.drawLine(550, 675, 650, 675, currentPaint);
            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            currentPaint.setStrokeWidth(11f);
            canvas.drawLine(370, 340, 430, 340, currentPaint);
            canvas.drawCircle(400, 340, 15, currentPaint); //도

        } else if (max_note == "C♯/D♭") {
//            currentPaint.setStrokeWidth(11f);
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 600, 690, currentPaint);
//            canvas.drawCircle(700, 675, 30, currentPaint);
//            canvas.drawLine(650, 675, 750, 675, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawLine(370, 340, 430, 340, currentPaint);
            canvas.drawCircle(400, 340, 15, currentPaint); //도
            canvas.drawText("♯", 330, 360, currentPaint);

        } else if (max_note == "D") {   //레
//            canvas.drawCircle(700, 650, 30, currentPaint);
            canvas.drawCircle(400, 317, 15, currentPaint); //레

        } else if (max_note == "D♯/E♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 600, 675, currentPaint);
//            canvas.drawCircle(700, 650, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(400, 317, 15, currentPaint); //레
            canvas.drawText("♯", 330, 337, currentPaint);

        } else if (max_note == "E") {   //미
//            canvas.drawCircle(700, 620, 30, currentPaint);
            canvas.drawCircle(400, 303, 15, currentPaint); //미

        } else if (max_note == "F") {   //파
//            canvas.drawCircle(700, 590, 30, currentPaint);
            canvas.drawCircle(400, 288, 15, currentPaint); //파
        } else if (max_note == "F♯/G♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 600, 615, currentPaint);
//            canvas.drawCircle(700, 590, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(400, 288, 15, currentPaint); //파
            canvas.drawText("♯", 330, 308, currentPaint);

        } else if (max_note == "G") {   //솔
//            canvas.drawCircle(700, 560, 30, currentPaint);
            canvas.drawCircle(400, 271, 15, currentPaint); //솔

        } else if (max_note == "G♯/A♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 600, 585, currentPaint);
//            canvas.drawCircle(700, 560, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(400, 271, 15, currentPaint); //솔
            canvas.drawText("♯", 330, 291, currentPaint);

        } else if (max_note == "A") {   //라
//            canvas.drawCircle(700, 530, 30, currentPaint);
            canvas.drawCircle(400, 258, 15, currentPaint); //라

        } else if (max_note == "A♯/B♭") {
//            currentPaint.setTextSize(90);
//            currentPaint.setFakeBoldText(true);
//            canvas.drawText("♯", 600, 560, currentPaint);
//            canvas.drawCircle(700, 530, 30, currentPaint);

            currentPaint.setStrokeWidth(11f);
            currentPaint.setTextSize(50);
            currentPaint.setFakeBoldText(true);
            canvas.drawCircle(400, 258, 15, currentPaint); //라
            canvas.drawText("♯", 330, 278, currentPaint);

        } else if (max_note == "B") {   //시
//            canvas.drawCircle(700, 500, 30, currentPaint);
            canvas.drawCircle(400, 242, 15, currentPaint); //시
        }
        else {
            canvas.drawText("범위를 초과했습니다.", 400, 100, currentPaint);
        }
    }
}
