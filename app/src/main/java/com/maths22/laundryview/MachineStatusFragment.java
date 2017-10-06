package com.maths22.laundryview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.maths22.laundryview.data.APIException;
import com.maths22.laundryview.data.LaundryRoom;
import com.maths22.laundryview.data.Machine;
import com.maths22.laundryview.data.MachineType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MachineStatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MachineStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAUNDRY_ROOM = "lr";
    private static final String ARG_TYPE = "type";

    private LaundryRoom lr;
    private MachineType type;

    private OnFragmentInteractionListener mListener;


    @Bind(R.id.machineStatusListView)
    ListView machineStatusListView;

    @Bind(R.id.refreshMachineStatusLayout)
    SwipeRefreshLayout refreshMachineStatusLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param lr   Parameter 1.
     * @param type Parameter 2.
     * @return A new instance of fragment MachineStatusFragment.
     */
    public static MachineStatusFragment newInstance(LaundryRoom lr, MachineType type) {
        MachineStatusFragment fragment = new MachineStatusFragment();
        fragment.lr = lr;
        fragment.type = type;
        Bundle args = new Bundle();
        args.putSerializable(ARG_LAUNDRY_ROOM, lr);
        args.putInt(ARG_TYPE, type.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    public MachineStatusFragment() {
        // Required empty public constructor
    }

    Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lr = (LaundryRoom) getArguments().getSerializable(ARG_LAUNDRY_ROOM);
            type = MachineType.values()[getArguments().getInt(ARG_TYPE)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_machine_status, container, false);
        ButterKnife.bind(this, v);

        refreshMachineStatusLayout.setOnRefreshListener(this);

        machineStatusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view,
                                    int position, long id) {
                final Switch alertSwitch = (Switch) view.findViewById(R.id.alertSwitch);
                alertSwitch.toggle();
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = activity;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onRefresh() {
        this.refresh();
    }

    public void onStart() {
        super.onStart();
        refresh();
    }

    ProgressDialog dialog;

    public void refresh() {
        if (machineStatusListView.getAdapter() == null) {
            dialog = ProgressDialog.show(getActivity(), "",
                    "Loading. Please wait...", true);
        }
        refreshMachineStatusLayout.setRefreshing(true);
        new LoadMachinesTask().execute(lr);
    }

    private class LoadMachinesTask extends AsyncTask<LaundryRoom, Integer, List<Machine>> {
        protected List<Machine> doInBackground(LaundryRoom... lrs) {
            try {
                lrs[0].refresh();
                switch (type) {
                    case WASHER:
                        return new ArrayList<>(lrs[0].getWashers());
                    case DRYER:
                        return new ArrayList<>(lrs[0].getDryers());
                    default:
                        return null;
                }
            } catch (APIException e) {
                return null;
            }
        }

        protected void onPostExecute(final List<Machine> result) {
            if (result == null) {
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("A network error has occured.  Please check your connection and try again.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();

                    }
                });

                alertDialog.show();
                return;
            }
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    MachineStatusArrayAdapter adapter = new MachineStatusArrayAdapter(getActivity(), result, lr);
                    machineStatusListView.setAdapter(adapter);
                    refreshMachineStatusLayout.setRefreshing(false);

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

}


