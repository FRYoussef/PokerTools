package com.example.youss.pokertools.model.processor;

import com.example.youss.pokertools.model.ObserverPatron.HandlerObserver;
import com.example.youss.pokertools.model.processor.concurrency.Shared;

import java.util.TimerTask;

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
