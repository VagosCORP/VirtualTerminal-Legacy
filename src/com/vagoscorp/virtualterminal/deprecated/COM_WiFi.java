package com.vagoscorp.virtualterminal.deprecated;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.util.EncodingUtils;

import android.os.AsyncTask;

public class COM_WiFi extends AsyncTask<Void, byte[], Integer> {

	final 	int	CLIENT = 1;
	final 	int	SERVER = 2;
	final	byte	CONECTADOs = 1;
	final	byte	IO_HABILITADOs = 2;
	final	byte	IO_EXCEPTIONs = 3;
	final	byte	DATO_RECIBIDOs = 4;
	final	byte[]	CONECTADO = {1};
	final	byte[]	IO_HABILITADO = {2};
	final	byte[]	IO_EXCEPTION = {3};
	final	byte[]	DATO_RECIBIDO = {7};
	
	int CS;
	int serverport = 2000;
	InetSocketAddress isa;
	Socket socket = null;
	ServerSocket serverSocket = null;
	boolean comunic = false;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	public COM_WiFi(int port) {
		// TODO Auto-generated constructor stub
		CS = SERVER;
		serverport = port;
	}
	
	public COM_WiFi(String ip, int port) {
		// TODO Auto-generated constructor stub
		CS = CLIENT;
		isa = new InetSocketAddress(ip, port);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		socket = null;
		serverSocket = null;
		comunic = false;
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			if(CS == CLIENT) {
				socket = new Socket();
				socket.connect(isa, 7000);
			}else if(CS == SERVER) {
				serverSocket = new ServerSocket(serverport);
				socket = serverSocket.accept();
				serverSocket.close();
			}
			if(socket.isConnected()) {
				publishProgress(CONECTADO);
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				comunic = true;
				publishProgress(IO_HABILITADO);
				while(comunic && socket.isConnected()) {
					byte[] buffer = new byte[1024];
					int len = dis.read(buffer);
					if(len != -1) {
						publishProgress(DATO_RECIBIDO,(len+"").getBytes(),buffer);
					}else if(!socket.isClosed())
						break;
				}
				comunic = false;
				dis.close();
				dos.close();
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			publishProgress(IO_EXCEPTION);
			e.printStackTrace();
		}
		return 1;
	}

	@Override
	protected void onProgressUpdate(byte[]... values) {
		// TODO Auto-generated method stub
		byte[] action = values[0];
		switch(action[0]) {
		case DATO_RECIBIDOs: {
			int lon = Integer.parseInt(new String(values[1]));
			EncodingUtils.getAsciiString(values[2],0,lon);
			break;
		}
		case CONECTADOs: {
			
			break;
		}
		case IO_HABILITADOs: {
			
			break;
		}
		case IO_EXCEPTIONs: {
			
			break;
		}
		}
		super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
}
