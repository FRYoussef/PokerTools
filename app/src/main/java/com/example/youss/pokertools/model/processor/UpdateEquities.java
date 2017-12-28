package model.processor;

import java.util.TimerTask;

import control.ObserverPatron.HandlerObserver;
import model.processor.concurrency.Shared;

public class UpdateEquities extends TimerTask{
	
	private Shared sharedData; 
	public UpdateEquities(Shared sharedData){
		this.sharedData = sharedData;
	}
	@Override
	public void run() {
		if(HandlerObserver.getoSolution() != null){
			HandlerObserver.getoSolution().notifyEquity(this.sharedData.getPlayersStats());
		}
	}

}
