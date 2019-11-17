package com.invictus.reehbayse.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.invictus.reehbayse.activities.InboxActivity;
import com.invictus.reehbayse.activities.RosterActivity;
import com.invictus.reehbayse.R;

public class InboxPagerFragment extends Fragment {
    private static final String POSITION = "position";

    private static int mPosition;

    private OnFragmentInteractionListener mListener;

    public InboxPagerFragment() {
        // Required empty public constructor
    }

    public static InboxPagerFragment newInstance(int position) {
        InboxPagerFragment fragment = new InboxPagerFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.inbox_pager_threads, container, false);

        switch (mPosition) {
            case 0:
                rootView = inflater.inflate(R.layout.inbox_pager_threads, container, false);
                InboxActivity.fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), RosterActivity.class));
                    }
                });
                break;

            case 1:
                rootView = inflater.inflate(R.layout.inbox_pager_groups, container, false);
                break;
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
