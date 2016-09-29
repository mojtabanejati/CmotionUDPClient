package udpclient.moji.com.udpclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * this class reads the information from registered application on the network and show the application's info and shows the binary information in the textview
 *
 */

public class udpclient extends Activity {

    public static final String TAG = "UDPClient";
    public static final String SERVICE_TYPE = "_cmotion._udp.";
    private boolean mIsSending = true;

    NsdHelperClient mNsdHelperClient;
    String receivedString = "";
    TextView recievedTextString;
    TextView textViewServices;
    private InetAddress mAdress;
    private static final int serverPort = 5050;
    private StringBuilder stringBuilder = new StringBuilder();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textViewServices = (TextView) findViewById(R.id.displayConnected2);
        recievedTextString = (TextView) findViewById(R.id.recievedString);

        mNsdHelperClient = new NsdHelperClient(this);
        mNsdHelperClient.initializeNsd();
        mNsdHelperClient.discoverServices();


        Button btn = (Button) findViewById(R.id.button1);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mNsdHelperClient.receivedServices != null) {
                        new Thread(runUdpClient).start();
                    }
                }
            });

        }


    private Runnable runUdpClient = new Runnable() {

        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];


        @Override
        public void run() {
            while (mIsSending) {
                DatagramSocket ds = null;
                try {


                    textViewServices.setText(NsdHelperClient.getInstance().serviceName());
                    mAdress = InetAddress.getByName("192.168.1.133");
                    ds = new DatagramSocket(serverPort);
                    DatagramPacket receivePacket;

                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    ds.receive(receivePacket);
                    receivedString = new String(receivePacket.getData());
                    stringBuilder.append(receivePacket);
                    stringBuilder.toString();
                    recievedTextString.setText("Data from SERVER: " +   stringBuilder);


                    DatagramPacket sendPacket;
                    sendPacket = new DatagramPacket(sendData, sendData.length, mAdress, serverPort);
                    ds.send(sendPacket);

                    Log.i("UDP packet received", receivedString);

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
            }
        }
    };


}

