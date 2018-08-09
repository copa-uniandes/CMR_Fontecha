package maintenancer;

public class Tres {
	private int eventsSitesID; //id1
	private int eventsOperationsID; //id2
	private double eventTimes; //s_i
	
	public Tres(int id1, int id2, double s_i){
		setEventsSitesID(id1);
		setEventsOperationsID(id2);
		setEventTimes(s_i);
	}
	
	public int getEventsSitesID() {
		return eventsSitesID;
	}
	public void setEventsSitesID(int eventsSitesID) {
		this.eventsSitesID = eventsSitesID;
	}
	public int getEventsOperationsID() {
		return eventsOperationsID;
	}
	public void setEventsOperationsID(int eventsOperationsID) {
		this.eventsOperationsID = eventsOperationsID;
	}
	public double getEventTimes() {
		return eventTimes;
	}
	public void setEventTimes(double eventTimes) {
		this.eventTimes = eventTimes;
	}
	
	
}
