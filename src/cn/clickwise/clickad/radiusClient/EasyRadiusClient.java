package cn.clickwise.clickad.radiusClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class EasyRadiusClient extends RadiusClient {

	private InputStream sockIn;

	private OutputStreamWriter sockOut;

	@Override
	public State connect(RadiusCenter rc) {

		State state = new State();

		try {
			sock = new Socket(rc.getIp(), rc.getPort());
			sockIn = sock.getInputStream();

			OutputStream outputStream = sock.getOutputStream();
			sockOut = new OutputStreamWriter(outputStream);

			state.setStatValue(StateValue.Normal);
		} catch (Exception e) {
			state.setStatValue(StateValue.Error);
			e.printStackTrace();
		}

		return state;
	}

	@Override
	public State sendHeartbeat() {

		State state = new State();

		try {
			sockOut.write(Heartbeat.heartbeat);
			state.setStatValue(StateValue.Normal);
		} catch (IOException e) {
			state.setStatValue(StateValue.Error);
		}

		
		
		
		return state;
	}

	@Override
	public RadiusPacket readPacket() {
		// TODO Auto-generated method stub
		byte[] head = new byte[16];
		
		PacketHead ph=new PacketHead();

		try {
			sockIn.read(head);
            ph.setHead(head);
			ph.parseBytes2Info();
			
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
