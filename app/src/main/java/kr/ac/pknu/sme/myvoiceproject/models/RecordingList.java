package kr.ac.pknu.sme.myvoiceproject.models;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import kr.ac.pknu.sme.myvoiceproject.models.database.RecordingDB;
import kr.ac.pknu.sme.myvoiceproject.utils.EntryComparator;

/**
 * Created by Yuri on 30.01.16.
 */
public class RecordingList
{
    private Hashtable<DateTime, Recording> recordings = new Hashtable<>();
    private double avg = -1;
    private double min = -1;
    private double max = -1;
    // initialise to 'now' so that checking works correctly in constructor
    private long beginning = new Date().getTime();
    private long end = 0;

    public RecordingList(Context context)
    {
        List<Recording> list = new RecordingDB(context).getRecordings();

        for (Recording recording : list)
        {
            // set beginning & end correctly
            if (recording.getDate().getTime() < this.getBeginning())
            {
                this.setBeginning(recording.getDate().getTime());
            }

            if (recording.getDate().getTime() > this.getEnd())
            {
                this.setEnd(recording.getDate().getTime());
            }

            this.getRecordings().put(new DateTime(recording.getDate()), recording);
        }
    }

    /***
     * get the graph entries for progress fragment
     * only one entry per day
     * if there are more than one recordings per day,
     * only use the first entry of that day
     * for future: calculate average as this day's value
     *
     * @return
     */
    public List<Entry> getGraphEntries()
    {
        List<Entry> result = new ArrayList<Entry>();

        Log.i("test", String.format("duration: %s", (int) new Duration(new DateTime(this.getBeginning()), new DateTime(this.getEnd())).getStandardDays()));

        for (Hashtable.Entry<DateTime, Recording> record : this.getRecordings().entrySet())
        {
            // reset time of day so .getDuration() will always calculate a duration of one day between two different dates
            DateTime recordTime = new DateTime(record.getKey().getYear(), record.getKey().getMonthOfYear(), record.getKey().getDayOfMonth(), 0, 0, 0, 0);

            // list index as duration in days since first recording
            int index = (int) new Duration(this.getBeginningAsDate(), recordTime).getStandardDays();

            // check if there are multiple entries for this date
            // and only add date if recording contains any pitch data
            if (!this.containsIndex(result, index) && record.getValue().getRange().getAvg() > 0)
            {
                Log.i("RecordingList", String.format("beginning: %s", new DateTime(this.getBeginning()).toDateTime()));
                result.add(new Entry((float) record.getValue().getRange().getAvg(), index));
            }
        }

        Collections.sort(result, new EntryComparator());

        for (Entry entry : result)
        {
            Log.i("result", String.format("%s: %s", entry.getXIndex(), entry.getVal()));
        }

        return result;
    }

    /***
     * check if Entry with given index value is already in list
     *
     * @param list
     * @param index
     * @return
     */
    private boolean containsIndex(List<Entry> list, int index)
    {
        for (Entry entry : list)
        {
            if (entry.getXIndex() == index)
            {
                return true;
            }
        }

        return false;
    }

    public List<String> getDates()
    {
        List<String> result = new ArrayList<String>();
        int duration = (int) new Duration(this.getBeginningAsDate(), new DateTime(this.getEnd())).getStandardDays();

        for (int i = 0; i <= duration; i++)
        {
            result.add(DateFormat.getDateInstance().format(this.getBeginningAsDate().plusDays(i).toDate()));
        }

        return result;
    }

    public Hashtable<DateTime, Recording> getRecordings()
    {
        return this.recordings;
    }

    public void setRecordings(Hashtable<DateTime, Recording> recordings)
    {
        this.recordings = recordings;
    }

    public double getAvg()
    {
        return avg;
    }

    public void setAvg(double avg)
    {
        this.avg = avg;
    }

    public double getMin()
    {
        if (this.min < 0)
        {
            double min = 10000;

            for (Hashtable.Entry<DateTime, Recording> recording : this.getRecordings().entrySet())
            {
                double current = recording.getValue().getRange().getAvg();

                if (min > current)
                {
                    min = current;
                }
            }

            this.setMin(min);
        }

        return this.min;
    }

    public void setMin(double min)
    {
        this.min = min;
    }

    public double getMax()
    {
        if (this.max < 0)
        {
            double max = 0;

            for (Hashtable.Entry<DateTime, Recording> recording : this.getRecordings().entrySet())
            {
                double current = recording.getValue().getRange().getAvg();

                if (max < current)
                {
                    max = current;
                }
            }

            this.setMax(max);
        }

        return this.max;
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    public long getBeginning()
    {
        return this.beginning;
    }

    /**
     * get beginning as date time with time set to 00:00:00:00
     *
     * @return
     */
    public DateTime getBeginningAsDate()
    {
        DateTime beginning = new DateTime(this.beginning);
        return new DateTime(beginning.getYear(), beginning.getMonthOfYear(), beginning.getDayOfMonth(), 0, 0, 0, 0);
    }

    public long getEnd()
    {
        return this.end;
    }

    private void setEnd(long end)
    {
        this.end = end;
    }

    public void setBeginning(long beginning)
    {
        this.beginning = beginning;
    }
}
