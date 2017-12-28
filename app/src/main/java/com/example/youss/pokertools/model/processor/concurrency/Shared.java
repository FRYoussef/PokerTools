package model.processor.concurrency;

public class Shared {
	private volatile double playerStats [];
	private volatile boolean run;
	private volatile int sims; //mostrar
	
	
	public Shared(int nPlayers) {
		playerStats = new double [nPlayers];
		run = true;
		sims = 0;
	}
	
	public synchronized void increasePlayer (int player, double value) {
		playerStats[player] += value;
	}
	
	public synchronized void increaseSim () {
		sims++;
	}
	
	public synchronized double getStat (int player) {
		return playerStats[player];
	}
	
	public synchronized boolean run() {
		return run;
	}
	
	public synchronized void stop() {
		run = false;
	}
	
	public synchronized int getSims () {
		return sims;
	}
	
	//It returns player's equities. 
	public synchronized double[] getPlayersStats(){
		double[] stats = new double[this.playerStats.length];
		for(int i = 0; i < this.playerStats.length; i++)
			stats[i] = this.playerStats[i] / this.sims;
		return stats;
	}
}
