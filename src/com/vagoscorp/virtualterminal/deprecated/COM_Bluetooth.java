package com.vagoscorp.virtualterminal.deprecated;

import android.os.AsyncTask;

public class COM_Bluetooth extends AsyncTask<Void, byte[], Integer> {

	@Override
	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*final	byte	CONECTADOs = 1;
	final	byte	IO_HABILITADOs = 2;
	final	byte	IO_EXCEPTIONs = 3;
	final	byte	DATO_RECIBIDOs = 7;
	final	byte[]	CONECTADO = {1};
	final	byte[]	IO_HABILITADO = {2};
	final	byte[]	IO_EXCEPTION = {3};
	final	byte[]	DATO_RECIBIDO = {7};
	
	int CS;
	boolean Bcon = false;
	BluetoothSocket socket = null;
	BluetoothSocket tempS = null;
	BluetoothServerSocket serverSocket = null;
	BluetoothServerSocket tempSS = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	public COM_Bluetooth(int CoS) {
		// TODO Auto-generated constructor stub
		CS = CoS;
		Bcon = false;
		if(CS == C.SERVER)
			Conect.setEnabled(true);
		else if(CS == C.CLIENT)
			Conect.setEnabled(false);
	}
	
	public void enviar(int dato) {
		if(comunic) {
			try {
				dos.writeByte(dato);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void enviar(String dato) {
		if(comunic) {
			try {
				dos.writeBytes(dato);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		socket = null;
		serverSocket = null;
		comunic = false;
		Chan_Ser.setEnabled(false);
		Conect.setText("Conectando...");
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Void... params) {
		// TODO Auto-generated method stub
		try {
			if(CS == C.CLIENT) {
				tempS = null;
				tempS = mDevice.createRfcommSocketToServiceRecord(myUUID);
				if (tempS != null) {
					socket = tempS;
					socket.connect();
					Bcon = true;
				} else
					Bcon = false;
			}else if(CS == C.SERVER) {
				tempSS = null;
				tempSS = BTAdapter.listenUsingRfcommWithServiceRecord(
						"sas", myUUID);
				if (tempSS != null) {
					serverSocket = tempSS;
					socket = serverSocket.accept();
					serverSocket.close();
					Bcon = true;
				} else
					Bcon = false;
			}
			if(Bcon) {
				publishProgress(CONECTADO);
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				comunic = true;
				publishProgress(IO_HABILITADO);
				byte[] buffer;
				int len;
				while(comunic && socket.isConnected()) {
					buffer = new byte[1024];
					len = dis.read(buffer);
					if(len != -1) {
						publishProgress(DATO_RECIBIDO,(len+"").getBytes(),buffer);
					}/*else if(socket.isClosed())
						break;*/
				/*}
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
			RX.append(EncodingUtils.getAsciiString(values[2],0,lon));
			break;
		}
		case CONECTADOs: {
			
			break;
		}
		case IO_HABILITADOs: {
			Chan_Ser.setEnabled(false);
			Conect.setText("Desconectar");
			Conect.setEnabled(true);
			Send.setEnabled(true);
			break;
		}
		case IO_EXCEPTIONs: {
			
			break;
		}
		}
		super.onProgressUpdate(values);
	}

	public void chao() {
		try {
			if (comunic) {
				comunic = false;
				dis.close();
				dos.close();
			}
			if (socket != null)
				socket.close();
			if(CS == C.SERVER && serverSocket != null)
				serverSocket.close();
		} catch (IOException e) {

		}
		Conect.setText("Conectar");
		Conect.setEnabled(true);
		Chan_Ser.setEnabled(true);
		Send.setEnabled(false);
		con = false;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		Toast.makeText(PrincipalBT.this, "post execute", 
				Toast.LENGTH_SHORT).show();
		chao();
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}*/
}
