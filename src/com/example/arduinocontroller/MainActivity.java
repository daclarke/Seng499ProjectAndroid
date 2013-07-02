package com.example.arduinocontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    
	Button Connect;
    ToggleButton OnOff;
    TextView Result;
    
    private static final String TAG = "Main";
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
	public String address = "00:07:80:50:78:BE";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private InputStream inStream = null;
	Handler handler = new Handler();
	byte delimiter = 10;
	boolean stopWorker = false;
	int readBufferPosition = 0;
	byte[] readBuffer = new byte[1024];

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
	private static Byte cStopForward = 102;//f
	private static Byte cRight = 82;		//R
	private static Byte cStopRight = 114;	//r
	private static Byte cLeft = 76;		//L
	private static Byte cStopLeft = 108;	//l
	private static Byte cReverse = 86;		//V
	private static Byte cStopReverse = 118;//v
	private static Byte cUp = 85;			//U
	private static Byte cStopUp = 117;		//u
	private static Byte cDown = 68;		//D
	private static Byte cStopDown = 100;	//d
	
	Map<Byte,Boolean> outputCommands = new HashMap<Byte,Boolean>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Connect = (Button) findViewById(R.id.connect);
        Result = (TextView) findViewById(R.id.msgJonduino);

        Connect.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//Connect();		
				SetUpPlayerOptionMenu();
			}
		});

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
		
		beginSendCommandsThread();
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
    

    public void SetUpPlayerOptionMenu(){
    	setContentView(R.layout.player_menu);
    	
    	mOnePlayerButton = (Button) findViewById(R.id.button_num_players_one);
    	mOnePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpOnePlayer();
			}
    	});

    	mTwoPlayerButton = (Button) findViewById(R.id.button_num_players_one);
    	mTwoPlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpTwoPlayer();
			}
    	});

    	mThreePlayerButton = (Button) findViewById(R.id.button_num_players_one);
    	mThreePlayerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SetUpThreePlayer();
			}
    	});
    }
    
    public void SetUpTwoPlayer(){
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
		setContentView(R.layout.one_player);

		mUpButton = (Button) findViewById(R.id.button_vertical_up);
		mUpButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	outputCommands.put(cUp, true);
		        	outputCommands.put(cStopUp, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	outputCommands.put(cStopUp, true);
		        	outputCommands.put(cUp, false);
		        }
				return false;
		     }
		});
		
		mDownButton = (Button) findViewById(R.id.button_vertical_down);
		mDownButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	outputCommands.put(cDown, true);
		        	outputCommands.put(cStopDown, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	outputCommands.put(cStopDown, true);
		        	outputCommands.put(cDown, false);
		        }
				return false;
		     }
		});
		
		mForwardButton = (Button) findViewById(R.id.button_up);
		mForwardButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) { 
		        	outputCommands.put(cForward, true);
		        	outputCommands.put(cStopForward, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) { 
		        	outputCommands.put(cForward, true);
		        	outputCommands.put(cStopForward, false);
		        }
				return false;
		     }
		});

		mReverseButton = (Button) findViewById(R.id.button_down);
		mReverseButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	outputCommands.put(cReverse, true);
		        	outputCommands.put(cStopReverse, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	outputCommands.put(cStopReverse, true);
		        	outputCommands.put(cReverse, false);
		        }
				return false;
		     }
		});
		
		mLeftButton = (Button) findViewById(R.id.button_left);
		mLeftButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	outputCommands.put(cLeft, true);
		        	outputCommands.put(cStopLeft, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	outputCommands.put(cStopLeft, true);
		        	outputCommands.put(cLeft, false);
		        }
				return false;
		     }
		});
		
		mRightButton = (Button) findViewById(R.id.button_right);
		mRightButton.setOnTouchListener(new OnTouchListener() {
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	outputCommands.put(cRight, true);
		        	outputCommands.put(cStopRight, false);
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	outputCommands.put(cStopRight, true);
		        	outputCommands.put(cRight, false);
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
		// TODO Auto-generated method stub

		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpTwoPlayer();
			}
		});
	}

	protected void SetUpTwoPlayer_PlayerOne() {
		// TODO Auto-generated method stub

		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpTwoPlayer();
			}
		});
	}

    
	protected void SetUpThreePlayer_PlayerThree() {
		// TODO Auto-generated method stub

		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}

	protected void SetUpThreePlayer_PlayerTwo() {
		// TODO Auto-generated method stub

		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}

	protected void SetUpThreePlayer_PlayerOne() {
		// TODO Auto-generated method stub

		mBackMenuButton = (Button) findViewById(R.id.button_back_player_menu);
		mBackMenuButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				SetUpThreePlayer();
			}
		});
	}

	
	//This thread will loop ~10 times a second. Will output 6 commands per loop, 1 for each direction.
	
	//TODO: remove keys from outputCommands depending on the player. Not all players need to send all commands.
	public void beginSendCommandsThread(){
		
		outputCommands.put(cForward,false);
		outputCommands.put(cStopForward,true);
		outputCommands.put(cRight,false);
		outputCommands.put(cStopRight,true);
		outputCommands.put(cLeft,false);
		outputCommands.put(cStopLeft,true);
		outputCommands.put(cReverse,false);
		outputCommands.put(cStopReverse,true);
		outputCommands.put(cUp,false);
		outputCommands.put(cStopUp,true);
		outputCommands.put(cDown,false);
		outputCommands.put(cStopDown,true);
		
		Thread commandThread = new Thread(new Runnable(){
			@Override
			public void run() {
				if(outputCommands.get(cForward)){
					writeData(cForward);
				}
				if(outputCommands.get(cStopForward)){
					writeData(cStopForward);
				}
				if(outputCommands.get(cRight)){
					writeData(cRight);
				}
				if(outputCommands.get(cStopRight)){
					writeData(cStopRight);
				}
				if(outputCommands.get(cLeft)){
					writeData(cLeft);
				}
				if(outputCommands.get(cStopLeft)){
					writeData(cStopLeft);
				}
				if(outputCommands.get(cReverse)){
					writeData(cReverse);
				}
				if(outputCommands.get(cStopReverse)){
					writeData(cStopReverse);
				}
				if(outputCommands.get(cUp)){
					writeData(cUp);
				}
				if(outputCommands.get(cStopUp)){
					writeData(cStopUp);
				}
				if(outputCommands.get(cDown)){
					writeData(cDown);
				}
				if(outputCommands.get(cStopDown)){
					writeData(cStopDown);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		});
		commandThread.start();
	}
}

