package de.fhbocholt.campuswoche.ioio;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;


public class IOIOControl extends BaseIOIOLooper implements ZapfControl{
	
	private DigitalOutput outputZapfen;
	private DigitalOutput outputDrehen;
	
	private DigitalOutput statusLED;
	
	private int ZAPF_PORT = 11;
	private int DREH_PORT = 10;
	private static final int mode_Zapfen = 1;
	private static final int mode_Pause = 0;
	private static final int mode_Drehen = 2;
	
	private boolean zapft;
	private boolean dreht;
	private boolean status;
	private int statusMode;
	
	private int pulseCounter = 0;
	
	@Override
	public void setup() throws ConnectionLostException{
		this.zapft = false;
		this.dreht = false;
		outputZapfen = ioio_.openDigitalOutput(ZAPF_PORT, false);
		outputDrehen = ioio_.openDigitalOutput(DREH_PORT, false);
		
		statusLED = ioio_.openDigitalOutput(0,false);
	}
	
	/**
	 * Wird immer wieder vom IOIO gerufen.
	 * Hierin die Anschlüsse der Zapfanlage ansteuern
	 */
	@Override
	public void loop() throws ConnectionLostException {
		outputDrehen.write(dreht);
		outputZapfen.write(zapft);
		statusLED.write(getStatusLED());
	}
	
	private boolean getStatusLED(){
		switch(statusMode){
		case mode_Zapfen: status = true;
			break;
		case mode_Pause: status = false;
			break;
		case mode_Drehen: 
			pulseCounter++;
			if(pulseCounter%15 == 0){
				status = !status;
			}
		}
		return false;
	}
	
	public void starteZapfen(){
		this.dreht = false;
		this.zapft = true;
		this.statusMode = mode_Zapfen;
	}
	
	public void starteDrehen(){
		this.dreht = true;
		this.zapft = false;
		this.statusMode = mode_Drehen;
	}
	
	public void stoppeZapfen(){
		this.zapft = false;
		this.statusMode = mode_Pause;
	}
	
	public void stoppeDrehen(){
		this.dreht = false;
		this.statusMode = mode_Pause;
	}
}
