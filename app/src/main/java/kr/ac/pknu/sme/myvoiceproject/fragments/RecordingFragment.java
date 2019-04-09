package kr.ac.pknu.sme.myvoiceproject.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.activities.CalActivity;
import kr.ac.pknu.sme.myvoiceproject.activities.RecordingActivity;
import kr.ac.pknu.sme.myvoiceproject.models.PitchRange;
import kr.ac.pknu.sme.myvoiceproject.models.Recording;
import kr.ac.pknu.sme.myvoiceproject.models.database.RecordingDB;
import kr.ac.pknu.sme.myvoiceproject.utils.AudioRecorder;
import kr.ac.pknu.sme.myvoiceproject.utils.PitchCalculator;
import kr.ac.pknu.sme.myvoiceproject.utils.SampleRateCalculator;

/**
 * fragment containing text for reading, record/stop and cancel buttons
 */
public class RecordingFragment extends Fragment {
    private static final String LOG_TAG = RecordingFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private PitchCalculator calculator = new PitchCalculator();
    private boolean isRecording = false;
    private Thread recordThread;
    private AudioDispatcher dispatcher;
    private int sampleRate;
    private int bufferRate = 4096;
    private AudioRecorder recorder;
    private String recordingFile;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 235;
    private static final int MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS = 183;


    public RecordingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);
    }


    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);

        this.requestRecordingPermission();


        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (stopRecording()) {
                        ((Button) view.findViewById(R.id.record_button)).setText(getResources().getString(R.string.start_recording));

                        calculator.getPitches().clear();
                        // TODO: delete file
                        // new File(recordingFile).delete();
                    }
                }

                mListener.onCancel();

            }
        });


        ///주파수->음계 버튼
        Button freqToNote = (Button) view.findViewById(R.id.freqToNote);
        freqToNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalActivity.class);
                startActivity(intent);
            }
        });




        ////////// 디테일 버튼을 새로 만들기.

        Button detailButton = (Button) view.findViewById(R.id.detail_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a;
//                Intent intent = new Intent(getActivity(), RecordViewActivity.class);
//                startActivity(intent);
                RecordingActivity recordingActivity = (RecordingActivity) getActivity();

                Log.v("넘어 왔냐 확인해보자~~", recordingActivity.getRec() + "");
                recordingActivity.startAc();    // 레코딩액티비티 객체 하나 만들어주고 그 객체에서 startAc 메써드 호출! 성공!!!!!

            }
        });

        //////////


        Button recordButton = (Button) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    if (stopRecording()) {
                        ((Button) v).setText(getResources().getString(R.string.start_recording));


                        PitchRange range = new PitchRange();
                        range.setPitches(calculator.getPitches());
                        range.setMin(calculator.calculateMinAverage());
                        range.setMax(calculator.calculateMaxAverage());
                        range.setAvg(calculator.calculatePitchAverage());

                        Recording currentRecord = new Recording(new Date());
                        currentRecord.setRange(range);
                        currentRecord.setRecording(recordingFile);

                        RecordingDB recordingDB = new RecordingDB(getActivity());
                        currentRecord = recordingDB.saveRecording(currentRecord);

                        v.setVisibility(View.INVISIBLE);

                        if (mListener != null) {
                            mListener.onRecordFinished(currentRecord.getId());
                        }
                    }
                } else {
                    if (recordPitch()) {
                        ((Button) v).setText(getResources().getString(R.string.stop_recording));
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setSampleRate() {
        this.sampleRate = SampleRateCalculator.getMaxSupportedSampleRate();

        Log.d("sample rate", String.valueOf(this.sampleRate));
    }

    private void requestRecordingPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            this.requestModifyAudioSettingsPermission();
        } else {
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
            //                    Manifest.permission.RECORD_AUDIO))
            //            {
            //
            //                // Show an expanation to the user *asynchronously* -- don't block
            //                // this thread waiting for the user's response! After the user
            //                // sees the explanation, try again to request the permission.
            //
            //            }
            //
            //            else
            //            {
            Log.i(LOG_TAG, "request recording permission");

            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            //            }
        }
    }

    private void requestModifyAudioSettingsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.MODIFY_AUDIO_SETTINGS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            this.setSampleRate();
            this.enableRecordButton();
        } else {
            //            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
            //                    Manifest.permission.MODIFY_AUDIO_SETTINGS))
            //            {
            //
            //                // Show an expanation to the user *asynchronously* -- don't block
            //                // this thread waiting for the user's response! After the user
            //                // sees the explanation, try again to request the permission.
            //
            //            }
            //
            //            else
            //            {
            this.requestPermissions(new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},
                    MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS);
            //            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i(LOG_TAG, String.format("permission result in: %s", requestCode));

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if recording permission was granted, also request permission to modify audio settings
                    this.requestModifyAudioSettingsPermission();
                    Log.i(LOG_TAG, "permission granted");

                } else {
                    // disable "record" button if recording permission was denied
                    Log.i(LOG_TAG, "permission denied");

                    this.disableRecordButton();

                    // show explanation why mic permission is needed
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.mic_permission_explanation)
                            .setTitle(R.string.mic_permission_title)
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // get user back to recording overview
                                    // will only affect user after permissions screen is left
                                    mListener.onCancel();

                                    // start app settings activity
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("")));
                                }

                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // get user back to recording overview
                                    mListener.onCancel();
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return;
            }

            case MY_PERMISSIONS_REQUEST_MODIFY_AUDIO_SETTINGS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // if permission for record audio & modify audio settings was granted,
                    // enable "record" button & set sampling rate
                    this.setSampleRate();
                    this.enableRecordButton();
                } else {
                    // disable "record" button if modify audio settings permission was denied
                    this.disableRecordButton();
                }

                return;
            }
        }
    }

    private void disableRecordButton() {
        Log.i(LOG_TAG, "disable record button");
        Button recordButton = (Button) this.getActivity().findViewById(R.id.record_button);
        recordButton.setEnabled(false);
    }

    private void enableRecordButton() {
        Button recordButton = (Button) this.getView().findViewById(R.id.record_button);
        recordButton.setEnabled(true);
    }

    public boolean recordPitch() {
        if (!this.isRecording) {
            this.isRecording = true;
            while (this.dispatcher == null) {
                try {
                    this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(this.sampleRate, this.bufferRate, 0);
                } catch (Exception exception) {
                    Integer usedSampleRate = 0;
                    ArrayList<Integer> testSampleRates = SampleRateCalculator.getAllSupportedSampleRates();
                    for (Integer testSampleRate : testSampleRates) {
                        try {
                            this.dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(testSampleRate, this.bufferRate, 0);
                        } catch (Exception exception_) {

                        }

                        if (this.dispatcher != null) {

                            break;
                        }
                    }

                }
            }

            if (this.dispatcher == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.device_sample_rate_not_supported)
                        .setTitle(R.string.device_sample_rate_not_supported_title);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            PitchDetectionHandler pdh = new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult result, AudioEvent e) {
                    final float pitchInHz = result.getPitch();
                    //                    result.
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onPitchDetected(pitchInHz);

                            Log.i(LOG_TAG, String.format("Pitch: %s", pitchInHz));
                            calculator.addPitch((double) pitchInHz);
                            Log.i(LOG_TAG, String.format("Avg: %s (%s - %s)", calculator.calculatePitchAverage().toString(), calculator.calculateMinAverage().toString(), calculator.calculateMaxAverage().toString()));
                        }
                    });
                }
            };

            AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, this.sampleRate, this.bufferRate, pdh);
            FileOutputStream fos = null;
            this.recordingFile = UUID.randomUUID().toString() + ".pcm";
            try {
                fos = getActivity().openFileOutput(this.recordingFile, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //this.recorder = new AudioRecorder(this.sampleRate, this.bufferRate, fos);
            dispatcher.addAudioProcessor(p);
            //dispatcher.addAudioProcessor(recorder);
            this.recordThread = new Thread(dispatcher, "Audio Dispatcher");
            this.recordThread.start();

            if (this.recordThread.isAlive()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    private boolean stopRecording() {
        if (this.isRecording) {
            try {
                if (this.dispatcher != null) {
                    this.dispatcher.stop();
                }

                if (this.recordThread != null) {
                    this.recordThread.stop();
                }
            } catch (Exception ex) {
                if (ex.getMessage() != null) {
                    Log.i(LOG_TAG, ex.getMessage());
                }
                Log.i(LOG_TAG, ex.getStackTrace().toString());
            }
        }

        if (this.recordThread.isAlive()) {
            Log.i(LOG_TAG, "still recording");
            return false;
        } else {
            this.isRecording = false;
            Log.i(LOG_TAG, "not recording");
            return true;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onPitchDetected(float pitchInHz);

        void onRecordFinished(long recordID);

        void onCancel();
    }
}
