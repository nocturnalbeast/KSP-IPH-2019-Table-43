package com.invictus.reehbayse.xmpp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.invictus.reehbayse.activities.InboxActivity;
import com.invictus.reehbayse.activities.RosterActivity;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PresenceTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Administrator on 2/15/2018.
 */

public class XMPP {

    private static final String TAG = "XMPP_Chirp";

    public static String AUTH = "auth";
    public static String USERNAME = "username";
    public static String PASSWORD = "password";

    public static String username = null;
    public static String password = null;


    //private static String host = "167.71.62.157";
    //private static String domain = "616.pub";

    private static String host = "192.168.43.224";
    private static String domain = "q2-reborn";
    private static int port = 5222;

    public static Context mContext;
    public static XMPP instance = null;
    public static AbstractXMPPConnection connection = null;
    public static Roster roster = null;

    public static XMPP getInstance(Context context){
        if(instance == null){
            mContext = context;
            instance = new XMPP();
            connect();
        }

        return instance;
    }

    public void init(){
        if(connection == null || !(connection.isConnected())){
            connect();
        }
    }

    public AbstractXMPPConnection getConnection(){
        if(connection == null || !(connection.isConnected())){
            connect();
        }
        return connection;
    }

    private static void connect(){

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                try {

                    InetAddress addr = InetAddress.getByName(host);
                    HostnameVerifier verifier = new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return false;
                        }
                    };
                    DomainBareJid serviceName = JidCreate.domainBareFrom(domain);
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setPort(port)
                            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                            //.setSocketFactory(new DummySSLSocketFactory())
                            .setXmppDomain(serviceName)
                            .setHostnameVerifier(verifier)
                            .setHostAddress(addr)
                            //.setDebuggerEnabled(true)
                            .build();

                    connection = new XMPPTCPConnection(config);
                    connection.addConnectionListener(new ConnectionListener() {
                        @Override
                        public void connected(XMPPConnection con) {
                            Log.d(TAG, "----------------- Connection established");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "Connection established", Toast.LENGTH_SHORT).show();
                                }
                            });


                            if(!con.isAuthenticated()){
                                login();
                            }
                        }

                        @Override
                        public void authenticated(XMPPConnection con, boolean resumed) {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(XMPP.AUTH, true);
                            editor.putString(XMPP.USERNAME, username);
                            editor.putString(XMPP.PASSWORD, password);
                            editor.apply();

                            Log.d(TAG, "----------------- authenticated");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "Authenticated", Toast.LENGTH_SHORT).show();
                                    mContext.startActivity(new Intent( mContext, InboxActivity.class));
                                }
                            });
                            if(roster == null){
                                getRoster();
                            }
                        }

                        @Override
                        public void connectionClosed() {
                            Log.d(TAG, "----------------- connection closed");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "Connection closed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void connectionClosedOnError(final Exception e) {
                            Log.d(TAG, "----------------- connection closed on error: "+e.getMessage());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "connection closed on erorr: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });

                    StanzaFilter subscribefilter = PresenceTypeFilter.SUBSCRIBE;
                    StanzaListener stanzaListener = new StanzaListener() {
                        @Override
                        public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
//                            if(packet.)
                        }
                    };

//                    connection.addSyncStanzaListener(subscribeListener, subscribefilter);


                    connection.connect();
                } catch (final IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "----------------- I/O: "+e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "I/O: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "----------------- interrupted: "+e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "interrupted: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final XMPPException e) {
                    e.printStackTrace();
                    Log.d(TAG, "----------------- XMPP: "+e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Connect XMPP: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final SmackException e) {
                    e.printStackTrace();
                    Log.d(TAG, "----------------- Smack: "+e.getMessage());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Smack: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return null;
            }
        }.execute();

    }

    private static void login(){
        try {
            connection.login(username, password);
        } catch (final InterruptedException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Interrupted: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "---------------------- Interrupted: "+e.getMessage());
                }
            });
            disconnect();
        } catch (final IOException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "I/O: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "---------------------- I/O: "+e.getMessage());
                }
            });
            disconnect();
        } catch (final SmackException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Smack: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "---------------------- Smack: "+e.getMessage());
                }
            });
            disconnect();
        } catch (final XMPPException e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "---------------------- XMPP: "+e.getMessage());
                }
            });
            disconnect();
        }
    }

    public static void register(){
        if(connection == null || !(connection.isConnected())){
            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... voids) {

                    try {

                        InetAddress addr = InetAddress.getByName(host);
                        HostnameVerifier verifier = new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return false;
                            }
                        };
                        DomainBareJid serviceName = JidCreate.domainBareFrom(domain);
                        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                                .setPort(port)
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .setXmppDomain(serviceName)
                                .setHostnameVerifier(verifier)
                                .setHostAddress(addr)
                                //.setDebuggerEnabled(true)
                                .build();

                        connection = new XMPPTCPConnection(config);
                        connection.addConnectionListener(new ConnectionListener() {
                            @Override
                            public void connected(XMPPConnection con) {
                                Log.d(TAG, "----------------- Connection established");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Connection established", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                if(!con.isAuthenticated()){
                                    createAccount();
                                }
                            }

                            @Override
                            public void authenticated(XMPPConnection con, boolean resumed) {
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean(XMPP.AUTH, true);
                                editor.putString(XMPP.USERNAME, username);
                                editor.putString(XMPP.PASSWORD, password);
                                editor.apply();

                                Log.d(TAG, "----------------- authenticated");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Authenticated", Toast.LENGTH_SHORT).show();
                                        mContext.startActivity(new Intent( mContext, InboxActivity.class));
                                    }
                                });
                                if(roster == null){
                                    getRoster();
                                }
                            }

                            @Override
                            public void connectionClosed() {
                                Log.d(TAG, "----------------- connection closed");
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "connection closed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void connectionClosedOnError(final Exception e) {
                                Log.d(TAG, "----------------- connection closed on error: "+e.getMessage());
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "connection closed on erorr: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        });
                        connection.connect();
                    } catch (final IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "----------------- I/O: "+e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "I/O: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "----------------- interrupted: "+e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "interrupted: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final XMPPException e) {
                        e.printStackTrace();
                        Log.d(TAG, "----------------- XMPP: "+e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "XMPP: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final SmackException e) {
                        e.printStackTrace();
                        Log.d(TAG, "----------------- Smack: "+e.getMessage());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Smack: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    return null;
                }
            }.execute();
        }
    }

    private static void createAccount(){
        AccountManager accountManager = AccountManager.getInstance(connection);
        try {
            if(accountManager.supportsAccountCreation()){
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.createAccount(Localpart.from(username), password);
                Log.d(TAG, "----------------- account created");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "account created", Toast.LENGTH_SHORT).show();
                    }
                });
                login();
            }
        } catch (final InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "----------------- interrupted: "+e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "interrupted: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            Log.d(TAG, "----------------- XMPP Error: "+e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "XMPP Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final XmppStringprepException e) {
            e.printStackTrace();
            Log.d(TAG, "----------------- XMPP Stringprep: "+e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "XMPP Stringprep: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.d(TAG, "----------------- Smack Notconnected: "+e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Smack Notconnected: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final SmackException.NoResponseException e) {
            e.printStackTrace();
            Log.d(TAG, "----------------- Smack Noresponse: "+e.getMessage());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Smack Noresponse: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static void getRoster(){

        roster.addSubscribeListener(new SubscribeListener() {
            @Override
            public SubscribeAnswer processSubscribe(final Jid from, final Presence subscribeRequest) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "From: "+from.toString()+" Presense: "+subscribeRequest.getStatus(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "----------------- From: "+from.toString()+" Presense: "+subscribeRequest.getStatus());
                    }
                });
                return SubscribeAnswer.ApproveAndAlsoRequestIfRequired;
            }
        });

        roster.addPresenceEventListener(new PresenceEventListener() {
            @Override
            public void presenceAvailable(FullJid address, Presence availablePresence) {

            }

            @Override
            public void presenceUnavailable(FullJid address, Presence presence) {

            }

            @Override
            public void presenceError(Jid address, Presence errorPresence) {

            }

            @Override
            public void presenceSubscribed(BareJid address, Presence subscribedPresence) {

            }

            @Override
            public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {

            }
        });

        roster = Roster.getInstanceFor(connection);

        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<Jid> addresses) {
                Log.d(TAG, "----------------- roster added");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "roster added", Toast.LENGTH_SHORT).show();
                    }
                });

//                InboxActivity.getInstance().runOnUiThread(new Runnable() {
//                    public void run()
//                    {
//                        InboxActivity.getInstance().updateBadge();
//                        RosterActivity.getInstance().refreshRoster();
//                    }
//                });
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
                Log.d(TAG, "----------------- roster updated");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "roster updated", Toast.LENGTH_SHORT).show();
                    }
                });

                InboxActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run()
                    {
                        InboxActivity.getInstance().updateBadge();
                        RosterActivity.getInstance().refreshRoster();
                    }
                });

            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
                Log.d(TAG, "----------------- roster deleted");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "roster deleted", Toast.LENGTH_SHORT).show();
                    }
                });


                InboxActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run()
                    {
                        InboxActivity.getInstance().updateBadge();
                        RosterActivity.getInstance().refreshRoster();
                    }
                });
            }

            @Override
            public void presenceChanged(Presence presence) {
                Log.d(TAG, "----------------- roster presence changed");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "roster presence changed", Toast.LENGTH_SHORT).show();
                    }
                });

                InboxActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run()
                    {
                        InboxActivity.getInstance().updateBadge();
                        RosterActivity.getInstance().refreshRoster();
                    }
                });
            }
        });

        if(!roster.isLoaded()){
            try{
                roster.reloadAndWait();
                Log.d(TAG, "----------------- roster loaded");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "roster loaded", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (final InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "----------------- Interrupted: "+e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Interrupted: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (final SmackException.NotLoggedInException e) {
                e.printStackTrace();
                Log.d(TAG, "----------------- Smack.NotLoggedIn: "+e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Smack.NotLoggedIn: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (final SmackException.NotConnectedException e) {
                e.printStackTrace();
                Log.d(TAG, "----------------- Smack.NotConnected: "+e.getMessage());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Smack.NotConnected: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    public static void addBuddy(String username){
        Presence subscribe = new Presence(Presence.Type.subscribe);
        subscribe.setTo(username+"@"+domain);
        try {
            connection.sendStanza(subscribe);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        connection.disconnect();
        roster = null;
        username = null;
        password = null;
    }

}