package com.invictus.reehbayse.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.invictus.reehbayse.activities.StartActivity;
import com.invictus.reehbayse.R;
import com.invictus.reehbayse.xmpp.XMPP;

public class WizardFragment extends Fragment {

    private static final String POSITION = "position";
    private int WIZARD_PAGE_POSITION;

    private EditText inputLoginUser, inputLoginPassword, inputRegisterUser, inputRegisterPassword, inputRegisterConfirmPassword;
    private ProgressDialog mProgressDialog;

    private OnFragmentInteractionListener mListener;

    public WizardFragment() {
        // Required empty public constructor
    }

    public static WizardFragment newInstance(int position) {
        WizardFragment fragment = new WizardFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            WIZARD_PAGE_POSITION = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.form_wizard_start, container, false);

        switch (WIZARD_PAGE_POSITION) {
            case 0:
                rootView = inflater.inflate(R.layout.form_wizard_start, container, false);
                rootView.findViewById(R.id.btnStartChirping).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivity.viewPager.setCurrentItem(1, true);
                    }
                });

                break;

            case 1:
                rootView = inflater.inflate(R.layout.form_wizard_login, container, false);
                inputLoginUser = (EditText) rootView.findViewById(R.id.inputLoginUser);
                inputLoginPassword = (EditText) rootView.findViewById(R.id.inputLoginPassword);

                rootView.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = inputLoginUser.getText().toString();
                        String password = inputLoginPassword.getText().toString();
                        if (username.isEmpty() || password.isEmpty()
                                || username.equals(null) || password.equals(null)
                                || username.equals("") || password.equals("")) {
                            Toast.makeText(getContext(), "Please provide valid username and password.", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog = ProgressDialog.show(getContext(), "",
                                    "Authenticating , please wait...", true);

                            XMPP.username = username;
                            XMPP.password = password;

                            XMPP.getInstance(getActivity()).init();

                            mProgressDialog.dismiss();
                        }

                    }
                });

                rootView.findViewById(R.id.tvRegister).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivity.viewPager.setCurrentItem(2, true);
                    }
                });
                break;

            case 2:
                rootView = inflater.inflate(R.layout.form_wizard_register, container, false);

                inputRegisterUser = (EditText) rootView.findViewById(R.id.inputRegisterUser);
                inputRegisterPassword = (EditText) rootView.findViewById(R.id.inputRegisterPassword);
                inputRegisterConfirmPassword = (EditText) rootView.findViewById(R.id.inputRegisterConfirmPassword);

                rootView.findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = inputRegisterUser.getText().toString();
                        String password = inputRegisterPassword.getText().toString();
                        String confirmPass = inputRegisterConfirmPassword.getText().toString();

                        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()
                                || username.equals(null) || password.equals(null) || confirmPass.equals(null)
                                || username.equals("") || password.equals("") || confirmPass.equals("")) {
                            Toast.makeText(getContext(), "Please provide valid username and password", Toast.LENGTH_SHORT).show();
                        } else if (!password.equals(confirmPass)) {
                            Toast.makeText(getContext(), "Password mismatch", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog = ProgressDialog.show(getContext(), "",
                                    "Creating a new account, please wait...", true);

                            XMPP.username = username;
                            XMPP.password = password;

                            XMPP.register();

                            mProgressDialog.dismiss();
                        }

                    }
                });


                rootView.findViewById(R.id.tvLogin).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StartActivity.viewPager.setCurrentItem(1, true);
                    }
                });
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
