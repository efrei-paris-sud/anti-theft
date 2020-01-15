package com.socket;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
 
 
 
 
import java.util.ArrayList;
 
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View; 
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
 
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
 

public class ActivityServer extends Activity 
{
	    /** Called when the activity is first created. */
		private Button CreateButton;

		private EditText editMsgText, editMsgTextCilent;
		private TextView recvText;
		private TextView WenduText;
		private TextView WenduText1;
	
		private Button Button1;	
		private Button Button2;
		private Button Button3;

		private TextView View1;
		private TextView View2;
		
		private EditText EditText1;
		
		private Context mContext;
		private Thread mThreadServer = null;
		private Socket mSocketServer = null;
		private Socket mSocketClient = null;
		static BufferedReader mBufferedReaderServer	= null;
		static PrintWriter mPrintWriterServer = null;
		private  String recvMessageServer = "";
		private  String WenDuStr = " ";
		private  String ShiDuStr = " ";
		int cnt=0;
		private EditText username, password;
		private SQLiteDatabase DB;
		private ListView values;		
 
		String str1="";
		String str2="";		
		
		
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        mContext = this;
	        
	        
	        
	        Button1= (Button)findViewById(R.id.Button1Xml);
	        Button1.setOnClickListener(Button1ClickListener);

	        
	        CreateButton= (Button)findViewById(R.id.CreateConnect);
	        CreateButton.setOnClickListener(CreateClickListener);
 		
	        
	        recvText= (TextView)findViewById(R.id.RecvText);       
	        recvText.setMovementMethod(ScrollingMovementMethod.getInstance()); 
	        
			
	        View1 = (TextView)findViewById(R.id.View1Xml);

	        
//			values = (ListView) findViewById(R.id.values_list);
			// 获取SQLiteDatabase以操作SQL语句
			DB = SQLiteDatabase.openOrCreateDatabase(getFilesDir() + "/WSD.db",null);	        
	        
	        
	               
	    }
		
		

		
		private OnClickListener Button1ClickListener = new OnClickListener()
		{
				@Override
				public void onClick(View arg0)
				{
						// TODO Auto-generated method stub				
						if ( serverRuning && mSocketServer!=null ) 
						{
								String msgText = "FD00";
								mPrintWriterServer.print(msgText);//发送给服务器
								mPrintWriterServer.flush();														
						}
						else
						{
								Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
						}
										
				}
		};
			
		
		
		Handler mHandler = new Handler()
		{										
				public void handleMessage(Message msg)										
				{											
						super.handleMessage(msg);			
						if(msg.what == 0)
						{
								recvText.append("Server: "+recvMessageServer);	// 刷新
											  			
						}
						else if(msg.what == 1)
						{
								recvText.setText("Server: "+recvMessageServer);	// 刷新
								if(recvMessageServer.substring(0,1).equals("w"))
								{
									
										if(recvMessageServer.substring(1,2).equals("1"))
										{

													View1.setText("  当前状态:  ---报警---");

		

										}
										else
										{
											View1.setText("  当前状态:            ");
										}
										
											  												
										//save();

										
	
								}	
						}
				}									
		 };
		 
		//创建服务端ServerSocket对象
	     private ServerSocket serverSocket = null;
	     private boolean serverRuning = false;
		 private OnClickListener CreateClickListener = new OnClickListener() 
		 {
				@Override
				public void onClick(View arg0)
				{
						// TODO Auto-generated method stub				
						if (serverRuning) 
						{
								serverRuning = false;
															
								try
								{
										if(serverSocket!=null)
										{
												serverSocket.close();
												serverSocket = null;
										}
										if(mSocketServer!=null)
										{
												mSocketServer.close();
												mSocketServer = null;
										}
								}
								catch (IOException e)
								{
										// TODO Auto-generated catch block
										e.printStackTrace();
								}
								mThreadServer.interrupt();
								CreateButton.setText("创建服务");
								recvText.setText("信息:\n");
						}
						else
						{
								serverRuning = true;
								mThreadServer = new Thread(mcreateRunnable);
								mThreadServer.start();
								CreateButton.setText("停止服务");
						}
				}
		};	
			
		//线程:监听服务器发来的消息
		private Runnable	mcreateRunnable	= new Runnable() 
		{
				public void run()
				{				
						try
						{
								serverSocket = new ServerSocket(8888);
															
								SocketAddress address = null;	
								if(!serverSocket.isBound())	
								{
										serverSocket.bind(address, 0);
								}
															
															
								getLocalIpAddress();
									
								//方法用于等待客服连接 
								mSocketServer = serverSocket.accept();	                	               
											                
								//接受客服端数据BufferedReader对象
								mBufferedReaderServer = new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
								//给客服端发送数据
								mPrintWriterServer = new PrintWriter(mSocketServer.getOutputStream(),true);
								//mPrintWriter.println("服务端已经收到数据！");
									
								Message msg = new Message();
								msg.what = 0;
								recvMessageServer = "client已经连接上！\n";
								mHandler.sendMessage(msg);
								                
						}
						catch (IOException e)
						{
								// TODO Auto-generated catch block
								//e.printStackTrace();
								Message msg = new Message();
								msg.what = 0;
								recvMessageServer = "创建异常:" + e.getMessage() + e.toString() + "\n";//消息换行
								mHandler.sendMessage(msg);
								return;
						}
						char[] buffer = new char[256];
						int count = 0;
						while(serverRuning)
						{
								try
								{
										//if( (recvMessageServer = mBufferedReaderServer.readLine()) != null )//获取客服端数据
										if((count = mBufferedReaderServer.read(buffer))>0);
										{						
												recvMessageServer = getInfoBuff(buffer, count) + "\n";//消息换行
																							
												Message msg = new Message();
												msg.what = 1;
												mHandler.sendMessage(msg);
																							
										}
								}
								catch (Exception e)
								{
										recvMessageServer = "接收异常:" + e.getMessage() + "\n";//消息换行
										Message msg = new Message();
										msg.what = 1;
										mHandler.sendMessage(msg);
										return;
								}
						}
				}
		};
					
			public String getLocalIpAddress() 
			{
					try 
					{
							for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
							{
									NetworkInterface intf = en.nextElement();
									for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();	enumIpAddr.hasMoreElements();) 
									{
											InetAddress inetAddress = enumIpAddr.nextElement();
											if(inetAddress.isSiteLocalAddress())
											{
												recvMessageServer = "请连接IP："+inetAddress.getHostAddress()+":"+ serverSocket.getLocalPort()+ "\n";
											}
									}
							}
					} 
					catch (SocketException ex) 
					{
							recvMessageServer = "获取IP地址异常:" + ex.getMessage() + "\n";//消息换行
							Message msg = new Message();
							msg.what = 0;
							mHandler.sendMessage(msg);
					}
					Message msg = new Message();
				    msg.what = 0;
					mHandler.sendMessage(msg);
					return null;
			}
			
			private String getInfoBuff(char[] buff, int count)
			{
					char[] temp = new char[count];
					for(int i=0; i<count; i++)
					{
							temp[i] = buff[i];
					}
					return new String(temp);
			}
			
			
			
			public void onDestroy()
			{
					super.onDestroy();
					DB.close();
					if (serverRuning) 
					{
							serverRuning = false;				
							try
							{
									if(serverSocket!=null)
									{
											serverSocket.close();
											serverSocket = null;
									}
									if(mSocketServer!=null)
									{
											mSocketServer.close();
											mSocketServer = null;
									}
							} 
							catch (IOException e) 
							{
									// TODO Auto-generated catch block
									e.printStackTrace();
							}
							mThreadServer.interrupt();
					}
			}
			
			
			/**
			 * 保存按钮点击事件，首次插入由于没有表必然报错，简化程序利用try-catch在catch中创建表
			 */
			public void save() 
			{
 
					try
					{
							insert(WenDuStr, ShiDuStr);
					}
					catch (Exception e)
					{
							create();
							insert(WenDuStr, ShiDuStr);
					}
					Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();
			}
			
			/**
			 * 读取按钮点击事件，以列表的形式显示所有内容
			 */
			public void read()
			{
					replaceList();
			}
		 
			/**
			 * ListView的适配器
			 */
			public void replaceList()  
			{
					Cursor cursor = select();
				//	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.values_item, cursor, new String[] { "_id", "username","password" }, new int[] { R.id.tv_id, R.id.tv_username,R.id.tv_password },SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				//	values.setAdapter(adapter);
			}
		 
			/**
			 * 建表
			 */
			public void create()
			{
					String createSql = "create table user(_id integer primary key autoincrement,username,password)";
					DB.execSQL(createSql);
			}
		 
			/**
			 * 插入
			 */
			public void insert(String username, String password)
			{
					String insertSql = "insert into user(username,password) values(?,?)";
					DB.execSQL(insertSql, new String[] { username, password });
			}
		 
			/**
			 * 查询
			 */
			public Cursor select()
			{
					String selectSql = "select _id,username,password from user";
					Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
					return cursor;
			}
		 
			/**
			 * 删除
			 */
			public void delete(String id)
			{
					String deleteSql = "delete from user where _id=?";
					DB.execSQL(deleteSql, new String[] { id });
			}
		 
			/**
			 * 更新
			 */
			public void updata(String username, String password, String id)
			{
					String updataSql = "update user set username=?,password=? where _id=?";
					DB.execSQL(updataSql, new String[] { username, password, id });
			}
			
			
}









