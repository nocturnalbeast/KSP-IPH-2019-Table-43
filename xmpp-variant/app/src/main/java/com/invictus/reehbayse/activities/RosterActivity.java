package com.invictus.reehbayse.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.invictus.reehbayse.adapters.RosterAdapter;
import com.invictus.reehbayse.models.RosterEntryModel;
import com.invictus.reehbayse.R;
import com.invictus.reehbayse.xmpp.XMPP;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RosterActivity extends AppCompatActivity {

    private ListView lvRoster;
    private RosterAdapter adapter;

    private static RosterActivity instance;

    public static RosterActivity getInstance() {
        return instance;
    }

    @Override
    protected void onStart() {
        super.onStart();
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_roster);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvRoster = (ListView) findViewById(R.id.lvRoster);

        adapter = new RosterAdapter(this, R.layout.layout_roster_entry, getRoster(XMPP.roster));
        lvRoster.setAdapter(adapter);
    }

    private List<RosterEntryModel> getRoster(Roster roster) {
        Log.d("Roster", "------------------------------- in roster");
        List<RosterEntryModel> friendslist = new ArrayList<>();

        if (roster == null) return friendslist;

        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {
            RosterEntryModel model = new RosterEntryModel();
            model.setName(entry.getName());
            model.setJid(entry.getJid());
            model.setType(entry.getType().toString());
            model.setCanSeeMyPresence(entry.canSeeMyPresence());
            model.setCanSeeHisPresence(entry.canSeeHisPresence());
            model.setApproved(entry.isApproved());
            model.setSubscriptionPending(entry.isSubscriptionPending());

            Log.d("Roster", "-------------------------------");
            Log.d("Roster", "getName: " + entry.getName());
            Log.d("Roster", "getJid: " + entry.getJid());
            Log.d("Roster", "getType: " + entry.getType());
            Log.d("Roster", "getClass: " + entry.getClass());
            Log.d("Roster", "getGroups: " + entry.getGroups());
            Log.d("Roster", "canSeeHisPresence: " + entry.canSeeHisPresence());
            Log.d("Roster", "canSeeMyPresence: " + entry.canSeeMyPresence());
            Log.d("Roster", "isApproved: " + entry.isApproved());
            Log.d("Roster", "isSubscriptionPending: " + entry.isSubscriptionPending());
            Log.d("Roster", "-------------------------------");
            friendslist.add(model);
        }
        Log.d("Roster", "-------------------------------" + friendslist.size());
        return friendslist;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_roster, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_new_friend:
                new AddFriendDialog().show(getFragmentManager(), null);
                return true;
            case R.id.action_invite_friend:
                return true;
            case R.id.action_refresh:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void refreshRoster() {
        adapter = (RosterAdapter) getRoster(XMPP.roster);
        adapter.notifyDataSetChanged();

        Log.d("Roster", "------------------------------- roster refreshed");
    }

    @SuppressLint("ValidFragment")
    public static class AddFriendDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater

            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.layout_dialog, null);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            EditText input = (EditText) view.findViewById(R.id.inputFriendUsername);
                            String username = input.getText().toString();
                            if (username.isEmpty() || username.equals("")) {
                                Toast.makeText(getActivity(), "Please provide a valid username", Toast.LENGTH_SHORT).show();
                            } else {
                                XMPP.addBuddy(username);
                            }

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AddFriendDialog.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
}
