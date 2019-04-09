package kr.ac.pknu.sme.myvoiceproject.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.activities.RecordingListActivity;
import kr.ac.pknu.sme.myvoiceproject.adapters.SwipeAdapter;
import kr.ac.pknu.sme.myvoiceproject.callbacks.ItemTouchHelperCallback;
import kr.ac.pknu.sme.myvoiceproject.models.Recording;
import kr.ac.pknu.sme.myvoiceproject.models.database.RecordingDB;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RecordingListFragment extends Fragment
{
    private List<Recording> recordings = new ArrayList<Recording>();
    private OnFragmentInteractionListener listener;

    /**
     * The fragment's ListView/GridView.
     */
    //    private ListView listView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SwipeAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecordingListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings = recordingDB.getRecordings();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_recording_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getActivity().getApplicationContext(), this));

        this.adapter = new SwipeAdapter(getActivity().getApplicationContext(), (RecordingListActivity) getActivity(), this.recordings);
        recyclerView.setAdapter(this.adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this.adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



        if (this.adapter.isEmpty())
        {
            System.out.println("visibility set to visible");
            view.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }

        else
        {
            System.out.println("visibility set to invisible");
            view.findViewById(android.R.id.empty).setVisibility(View.GONE);
        }


        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        RecordingDB recordingDB = new RecordingDB(getActivity());
        this.recordings.clear();
        this.recordings.addAll(recordingDB.getRecordings());
        //                ((SwipeMenuAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }


    //    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (null != listener)
        {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            listener.onFragmentInteraction(this.recordings.get(position).getId());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText)
    {
        //        View emptyView = listView.getEmptyView();
        //
        //        if (emptyView instanceof TextView)
        //        {
        //            ((TextView) emptyView).setText(emptyText);
        //        }
    }

//    @Override
//    public void onItemClick(View view, int position)
//    {
//        if (null != listener)
//        {
//            // Notify the active callbacks interface (the activity, if the
//            // fragment is attached to one) that an item has been selected.
//            listener.onFragmentInteraction(this.recordings.get(position).getId());
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        public void onFragmentInteraction(long recordID);
    }
}
