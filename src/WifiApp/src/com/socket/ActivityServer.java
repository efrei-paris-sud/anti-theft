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
			// ��ȡSQLiteDatabase�Բ���SQL���
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
								mPrintWriterServer.print(msgText);//���͸�������
								mPrintWriterServer.flush();														
						}
						else
						{
								Toast.makeText(mContext, "û������", Toast.LENGTH_SHORT).show();
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
								recvText.append("Server: "+recvMessageServer);	// ˢ��
											  			
						}
						else if(msg.what == 1)
						{
								recvText.setText("Server: "+recvMessageServer);	// ˢ��
								if(recvMessageServer.substring(0,1).equals("w"))
								{
									
										if(recvMessageServer.substring(1,2).equals("1"))
										{

													View1.setText("  ��ǰ״̬:  ---����---");

		

										}
										else
										{
											View1.setText("  ��ǰ״̬:            ");
										}
										
											  												
										//save();

										
	
								}	
						}
				}									
		 };
		 
		//���������ServerSocket����
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
								CreateButton.setText("��������");
								recvText.setText("��Ϣ:\n");
						}
						else
						{
								serverRuning = true;
								mThreadServer = new Thread(mcreateRunnable);
								mThreadServer.start();
								CreateButton.setText("ֹͣ����");
						}
				}
		};	
			
		//�߳�:������������������Ϣ
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
									
								//�������ڵȴ��ͷ����� 
								mSocketServer = serverSocket.accept();	                	               
											                
								//���ܿͷ�������BufferedReader����
								mBufferedReaderServer = new BufferedReader(new InputStreamReader(mSocketServer.getInputStream()));
								//���ͷ��˷�������
								mPrintWriterServer = new PrintWriter(mSocketServer.getOutputStream(),true);
								//mPrintWriter.println("������Ѿ��յ����ݣ�");
									
								Message msg = new Message();
								msg.what = 0;
								recvMessageServer = "client�Ѿ������ϣ�\n";
								mHandler.sendMessage(msg);
								                
						}
						catch (IOException e)
						{
								// TODO Auto-generated catch block
								//e.printStackTrace();
								Message msg = new Message();
								msg.what = 0;
								recvMessageServer = "�����쳣:" + e.getMessage() + e.toString() + "\n";//��Ϣ����
								mHandler.sendMessage(msg);
								return;
						}
						char[] buffer = new char[256];
						int count = 0;
						while(serverRuning)
						{
								try
								{
										//if( (recvMessageServer = mBufferedReaderServer.readLine()) != null )//��ȡ�ͷ�������
										if((count = mBufferedReaderServer.read(buffer))>0);
										{						
												recvMessageServer = getInfoBuff(buffer, count) + "\n";//��Ϣ����
																							
												Message msg = new Message();
												msg.what = 1;
												mHandler.sendMessage(msg);
																							
										}
								}
								catch (Exception e)
								{
										recvMessageServer = "�����쳣:" + e.getMessage() + "\n";//��Ϣ����
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
												recvMessageServer = "������IP��"+inetAddress.getHostAddress()+":"+ serverSocket.getLocalPort()+ "\n";
											}
									}
							}
					} 
					catch (SocketException ex) 
					{
							recvMessageServer = "��ȡIP��ַ�쳣:" + ex.getMessage() + "\n";//��Ϣ����
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
			 * ���水ť����¼����״β�������û�б��Ȼ�����򻯳�������try-catch��catch�д�����
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
			 * ��ȡ��ť����¼������б����ʽ��ʾ��������
			 */
			public void read()
			{
					replaceList();
			}
		 
			/**
			 * ListView��������
			 */
			public void replaceList()  
			{
					Cursor cursor = select();
				//	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,R.layout.values_item, cursor, new String[] { "_id", "username","password" }, new int[] { R.id.tv_id, R.id.tv_username,R.id.tv_password },SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
				//	values.setAdapter(adapter);
			}
		 
			/**
			 * ����
			 */
			public void create()
			{
					String createSql = "create table user(_id integer primary key autoincrement,username,password)";
					DB.execSQL(createSql);
			}
		 
			/**
			 * ����
			 */
			public void insert(String username, String password)
			{
					String insertSql = "insert into user(username,password) values(?,?)";
					DB.execSQL(insertSql, new String[] { username, password });
			}
		 
			/**
			 * ��ѯ
			 */
			public Cursor select()
			{
					String selectSql = "select _id,username,password from user";
					Cursor cursor = DB.rawQuery(selectSql, null);// ������Ҫ�鴦������ʲ���Ҫ��ѯ����
					return cursor;
			}
		 
			/**
			 * ɾ��
			 */
			public void delete(String id)
			{
					String deleteSql = "delete from user where _id=?";
					DB.execSQL(deleteSql, new String[] { id });
			}
		 
			/**
			 * ����
			 */
			public void updata(String username, String password, String id)
			{
					String updataSql = "update user set username=?,password=? where _id=?";
					DB.execSQL(updataSql, new String[] { username, password, id });
			}
			
			
}









