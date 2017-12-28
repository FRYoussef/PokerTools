package model.processor;

import java.util.TimerTask;

import control.ObserverPatron.HandlerObserver;
import model.processor.concurrency.Shared;

public class UpdateSims extends TimerTask{

	private Shared sharedData; 
	
	public UpdateSims(Shared sharedData){
		this.sharedData = sharedData;
	}
	@Override
	public void run() {
		if(HandlerObserver.getoSolution() != null)
			HandlerObserver.getoSolution().notifySimulations(this.sharedData.getSims());
	}

}
