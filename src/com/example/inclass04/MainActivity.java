//Group9A_InClass04
//Ram Prasad Narayanaswamy
//Aaron Maisto
//Anusha Srivastava

package com.example.inclass04;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	SeekBar seekbar;
	Button generateByAsync;
	Button generateByHandlers;
	TextView textViewResult;
	TextView textViewNoOfTimes;
	int noOfTimes = 1;
	ProgressDialog progressDialog;
	ExecutorService threadPool;
	Handler handler;
	Message message;
	Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewResult = (TextView) findViewById(R.id.textViewResult);
		textViewNoOfTimes = (TextView) findViewById(R.id.textViewCount);
		textViewNoOfTimes.setText(1+" times");
		
		seekbar = (SeekBar) findViewById(R.id.seekBar1);
		seekbar.setMax(10);
		seekbar.setProgress(1);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				noOfTimes = progress;
				if(progress == 0){
					seekBar.setProgress(1);
					Toast.makeText(MainActivity.this,"Cannot set to 0...", Toast.LENGTH_SHORT).show();
				}				
				textViewNoOfTimes.setText(noOfTimes+" times");
			}
		});
		
		threadPool = Executors.newFixedThreadPool(2);
		handler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				switch (msg.what) {
					case GetNumberByHandlers.STATUS_START:
						progressDialog = new ProgressDialog(MainActivity.this);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.setCancelable(false);
						progressDialog.setProgress(0);
						progressDialog.setMessage("Retreiving the number...");
						progressDialog.setMax(noOfTimes);
						progressDialog.show();
						break;
					case GetNumberByHandlers.STATUS_STEP:
						progressDialog.setProgress((Integer) msg.obj);
						break;
					case GetNumberByHandlers.STATUS_DONE:
						progressDialog.dismiss();
						textViewResult.setText(((Double)msg.obj)+"");
						break;
				default:
					break;
				}
				return false;
			}
		});
		
		
		generateByAsync = (Button) findViewById(R.id.button2);
		generateByAsync.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setCancelable(false);
				progressDialog.setProgress(0);
				progressDialog.setMessage("Retreiving the number...");
				progressDialog.setMax(noOfTimes);
				progressDialog.show();
				new GetNumberByAsync().execute(noOfTimes);				
			}
		});
		
		
		generateByHandlers = (Button) findViewById(R.id.button1);
		generateByHandlers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				threadPool.execute(new GetNumberByHandlers());				
			}
		});	
	}

	public class GetNumberByAsync extends AsyncTask<Integer, Integer, Double>{
		@Override
		protected Double doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			int count = params[0];
			double result = 0.0;
			for(int i=0;i<count;i++){	
				result = result + HeavyWork.getNumber();
				publishProgress(i+1);
			}
			
			result = result/count;
			return result;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}

		@Override
		protected void onPostExecute(Double result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressDialog.dismiss();
			textViewResult.setText(result+"");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progressDialog.setProgress(values[0]);
		}	
	}
	
	public class GetNumberByHandlers implements Runnable{
		static final int STATUS_START = 0X00;
		static final int STATUS_STEP = 0X01;
		static final int STATUS_DONE = 0X02;	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			message = new Message();
			message.what = STATUS_START;
			handler.sendMessage(message);
			
			double result = 0.0;
			for(int i=0;i<noOfTimes;i++){	
				result = result + HeavyWork.getNumber();
				message = new Message();
				message.what = STATUS_STEP;
				message.obj = i+1;
				handler.sendMessage(message);	
			}			
			result = result/noOfTimes;		
			message = new Message();
			message.what = STATUS_DONE;
			message.obj = result;
			handler.sendMessage(message);
		}	
	}

}
