
// As the name indicates this class contains the definition of an event. next is needed to 
// build a linked list which is used by the EventListClass. It would have been just as easy
// to use a priority list which sorts events using eventTime.

class Event{
	public double eventTime;
	public int eventType;
	public Event next;
}
