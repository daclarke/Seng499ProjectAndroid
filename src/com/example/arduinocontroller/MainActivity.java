package com.example.arduinocontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    
	Button Connect;
    ToggleButton OnOff;
    TextView Result;

    private static final String NAME = "ArduinoController";
    
    private static final String TAG = "Main";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
	public String address = "00:07:80:50:78:BE";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	Handler handler = new Handler();
	byte delimiter = 10;
	boolean stopWorker = false;
	int readBufferPosition = 0;
	byte[] readBuffer = new byte[1024];

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    
	// Buttons
	private Button mBackMenuButton;
	
	private Button mForwardButton;
	private Button mUpButton;
	private Button mDownButton;
	private Button mReverseButton;
	private Button mLeftButton;
	private Button mRightButton;
	
	private Button mOnePlayerButton;
	private Button mTwoPlayerButton;
	private Button mThreePlayerButton;

	private Button mPlayerOneButton;
	private Button mPlayerTwoButton;
	private Button mPlayerThreeButton;
	
	//Arduino command codes
	private static Byte cForward = 70;		//F
	private static Byte cStopForward = 102;	//f
	private static Byte cRight = 82;		//R
	private static Byte cStopRight = 114;	//r
	private static Byte cLeft = 76;			//L
	private static Byte cStopLeft = 108;	//l
	private static Byte cReverse = 86;		//V
	private static Byte cStopReverse = 118;	//v
	private static Byte cUp = 85;			//U
	private static Byte cStopUp = 117;		//u
	private static Byte cDown = 68;			//D
	private static Byte cStopDown = 100;	//d
	
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;
	private ReadThread mReadThread;
    private BluetoothAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

		SetUpPlayerOptionMenu();
        
/*        setContentView(R.layout.activity_main);

        Connect = (Button) findViewById(R.id.connect);
        Result = (TextView) findViewById(R.id.msgJonduino);

        Connect.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Connect();				        
		        //beginSendCommandsThread();
			}
		});*/

        CheckBt();
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.e("onCreate", device.toString());
	}
	
	private void CheckBt() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
		if (!mBluetoothAdapter.isEnabled()) {
			Toast.makeText(getApplicationContext(), "Bluetooth Disabled !",
					Toast.LENGTH_SHORT).show();
	               /* It tests if the bluetooth is enabled or not, if not the app will show a message. */
		}
	
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),
					"Bluetooth null !", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	public void Connect() {
				
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		Log.d("", "Connecting to ... " + device);
		mBluetoothAdapter.cancelDiscovery();
		try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            /* Here is the part the connection is made, by asking the device to create a RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
			btSocket.connect();
			Log.d("", "Connection made.");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e2) {
				Log.d("", "Unable to end the connection");
			}
			Log.d("", "Socket creation failed");
		}
    }
	
	public void Disconnect(){
		if(btSocket != null){
			try {
				btSocket.close();
			} catch (IOException e) {
			}
		}
	}
	
	private void writeData(byte data) {
		
		try {
			outStream = btSocket.getOutputStream();
		} catch (IOException e) {
			Log.d(TAG, "Bug BEFORE Sending stuff", e);
		}
		
		byte[] msgBuffer = {data};

		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			Log.d(TAG, "Bug while sending stuff", e);
		}
	}
	 
    @Override
    protected void onDestroy() {
            super.onDestroy();   
                    try {
                            btSocket.close();
                    } catch (IOException e) {
                    }
    }


	private void setupHost() {
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mReadThread != null) {mReadThread.cancel(); mReadThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
		
		Connect();
		
		mAcceptThread = new AcceptThread();
		mAcceptThread.start();		
	}
	
	private void setupClient(){
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mReadThread != null) {mReadThread.cancel(); mReadThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, 1);    
	}
	
	private void connectToServer(String address){
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mConnectThread = new ConnectThread(device);
        mConnectThread.start();
	}
	
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                connectToServer(address);
            }
            break;
        }
    }
	 
	private class ReadThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ReadThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    mmInStream.read(buffer);
                    writeData(buffer[0]);
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    break;
                }
            }
        }       
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
	

	private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (socket == null) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {                    
                    mReadThread = new ReadThread(socket);
                    mReadThread.start();                            
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }
		
	private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            btSocket = tmp;
        }

        public void run() {
            setName("ConnectThread");

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
            	btSocket.connect();
            } catch (IOException e) {
                try {
                	btSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                return;
            }
        }

        public void cancel() {
            try {
            	btSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
	
    public void SetUpPlayerOptionMenu(){
    	Disconnect();
    	setContentView(R.layout.player_menu);
    	
    	mOnePlayerButton = (Button) findViewById(R.id.button_num_players_one);
    	mOnePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpOnePlayer();
			}
    	});
    	mTwoPlayerButton = (Button) findViewById(R.id.button_num_players_two);
    	mTwoPlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpTwoPlayer();
			}
    	});
    	mThreePlayerButton = (Button) findViewById(R.id.button_num_players_three);
    	mThreePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpThreePlayer();
			}
    	});
    }
    
    public void SetUpTwoPlayer(){
    	Disconnect();
    	setContentView(R.layout.two_player_menu);
    	
    	mPlayerOneButton = (Button) findViewById(R.id.button_player_one);
    	mPlayerOneButton.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View arg0){
    			SetUpTwoPlayer_PlayerOne();
    		}
    	});
    	mPlayerTwoButton = (Button) findViewById(R.id.button_player_two);
    	mPlayerTwoButton.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View arg0){
    			SetUpTwoPlayer_PlayerTwo();
    		}
    	});
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpPlayerOptionMenu();
			}
		});
    }
    
	public void SetUpThreePlayer(){
    	Disconnect();
    	setContentView(R.layout.three_player_menu);
    	
    	mPlayerOneButton = (Button) findViewById(R.id.button_player_one);
    	mPlayerOneButton.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View arg0){
    			SetUpThreePlayer_PlayerOne();
    		}
    	});
    	mPlayerTwoButton = (Button) findViewById(R.id.button_player_two);
    	mPlayerTwoButton.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View arg0){
    			SetUpThreePlayer_PlayerTwo();
    		}
    	});
    	mPlayerThreeButton = (Button) findViewById(R.id.button_player_three);
    	mPlayerThreeButton.setOnClickListener(new OnClickListener(){
    		@Override
    		public void onClick(View arg0){
    			SetUpThreePlayer_PlayerThree();
    		}
    	});
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpPlayerOptionMenu();
			}
		});
    }
    
	public void SetUpOnePlayer() {
		Connect();				        
		setContentView(R.layout.one_player);
		
		mUpButton = (Button) findViewById(R.id.button_vertical_up);
		mUpButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cUp);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopUp);
		        }
				return false;
		     }
		});		
		mDownButton = (Button) findViewById(R.id.button_vertical_down);
		mDownButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cDown);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopDown);
		        }
				return false;
		     }
		});		
		mForwardButton = (Button) findViewById(R.id.button_up);
		mForwardButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) { 
		        	writeData(cForward);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) { 
		        	writeData(cStopForward);
		        }
				return false;
		     }
		});
		mReverseButton = (Button) findViewById(R.id.button_down);
		mReverseButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cReverse);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopReverse);
		        }
				return false;
		     }
		});		
		mLeftButton = (Button) findViewById(R.id.button_left);
		mLeftButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cLeft);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopLeft);
		        }
				return false;
		     }
		});		
		mRightButton = (Button) findViewById(R.id.button_right);
		mRightButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cRight);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopRight);
		        }
				return false;
		     }
		});		
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpPlayerOptionMenu();
			}
		});
	}
	

    protected void SetUpTwoPlayer_PlayerTwo() {
    	setupClient();
		setContentView(R.layout.twoplayer_playertwo);
		
		mUpButton = (Button) findViewById(R.id.button_vertical_up);
		mUpButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cUp);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopUp);
		        }
				return false;
		     }
		});		
		mDownButton = (Button) findViewById(R.id.button_vertical_down);
		mDownButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cDown);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopDown);
		        }
				return false;
		     }
		});
		mLeftButton = (Button) findViewById(R.id.button_left);
		mLeftButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cLeft);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopLeft);
		        }
				return false;
		     }
		});		
		mRightButton = (Button) findViewById(R.id.button_right);
		mRightButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cRight);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopRight);
		        }
				return false;
		     }
		});		
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpTwoPlayer();
			}
		});
	}

	protected void SetUpTwoPlayer_PlayerOne() {
		setupHost();				        
		setContentView(R.layout.twoplayer_playerone);

		mForwardButton = (Button) findViewById(R.id.button_up);
		mForwardButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) { 
		        	writeData(cForward);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) { 
		        	writeData(cStopForward);
		        }
				return false;
		     }
		});
		mReverseButton = (Button) findViewById(R.id.button_down);
		mReverseButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cReverse);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopReverse);
		        }
				return false;
		     }
		});		
		
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpTwoPlayer();
			}
		});
	}

    
	protected void SetUpThreePlayer_PlayerThree() {
    	setupClient();
		setContentView(R.layout.threeplayer_playerthree);
		
		mUpButton = (Button) findViewById(R.id.button_vertical_up);
		mUpButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cUp);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopUp);
		        }
				return false;
		     }
		});		
		mDownButton = (Button) findViewById(R.id.button_vertical_down);
		mDownButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cDown);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopDown);
		        }
				return false;
		     }
		});	
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}

	protected void SetUpThreePlayer_PlayerTwo() {
    	setupClient();
		setContentView(R.layout.threeplayer_playertwo);
		
		mLeftButton = (Button) findViewById(R.id.button_left);
		mLeftButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cLeft);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopLeft);
		        }
				return false;
		     }
		});		
		mRightButton = (Button) findViewById(R.id.button_right);
		mRightButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cRight);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopLeft);
		        }
				return false;
		     }
		});
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}

	protected void SetUpThreePlayer_PlayerOne() {
		setupHost();
		setContentView(R.layout.threeplayer_playerone);
		
		mForwardButton = (Button) findViewById(R.id.button_up);
		mForwardButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) { 
		        	writeData(cForward);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) { 
		        	writeData(cStopForward);
		        }
				return false;
		     }
		});
		mReverseButton = (Button) findViewById(R.id.button_down);
		mReverseButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	writeData(cReverse);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	writeData(cStopForward);
		        }
				return false;
		     }
		});
		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}
}

