package com.example.haris.httpserve;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements WebServer.WebServerListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WebServer server;

    private TextView textMessage;

    private OnFragmentInteractionListener mListener;

    private String TAG = this.getClass().getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_detail, container, false);

        textMessage = (TextView) layout.findViewById(R.id.text_message);

        checkConnection();

        return layout;
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
        server.closeAllConnections();
    }

    @Override
    public void onResponse(String message) {
        textMessage.setText("Responding: " + message + "\n" + textMessage.getText().toString());
        if (mListener != null)
            mListener.onServerResponse(message);
    }

    private void connect() {
        server = new WebServer(App.get().prefsRead(App.PORT, App.DEFAULT_PORT), this);
        try {
            server.start(60000);
        } catch (Exception ex) {
            Log.e(TAG, "The server could not start. " + ex.getMessage());
        }
        Log.d(TAG, "Web server initialized.");
    }

    private void checkConnection() {

        String message;

        if (server.isAlive())
            message = "Server is connected on port " + server.getListeningPort();
        else
            message = "ERROR! Server is not connected";

        textMessage.setText(textMessage.getText() + "\n" + message);

        if (mListener != null) {
            if (server.isAlive())
                mListener.onServerConnected(message);
            else
                mListener.onServerError(message);
        }
    }

    public void reconnect() {
        server.closeAllConnections();
        connect();
        checkConnection();
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
        void onServerConnected(String message);

        void onServerError(String message);

        void onServerResponse(String message);
    }
}
