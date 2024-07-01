import scala.util._
import java.util.Scanner
import java.io._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await,ExecutionContext,Future,Promise}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.io._

case class Flow(f: Int)
case class Debug(debug: Boolean)
case class Control(control:ActorRef)
case class Source(n: Int)
case class SourceChanged(e: Int)
case class SinkChanged(e: Int)
case class CanPush(fromHeight: Int, a: Edge)
case class Push(a: Edge)

case object Print
case object Start
case object Excess
case object Maxflow
case object Sink
case object Hello
case object Discharge
case object DonePush

class Edge(var u: ActorRef, var v: ActorRef, var c: Int) {
	var	f = 0
}

class Node(val index: Int) extends Actor {
	var	e = 0;				/* excess preflow. 						*/
	var	h = 0;				/* height. 							*/
	var k = 0
	var pushed = false
	var	control:ActorRef = null		/* controller to report to when e is zero. 			*/
	var	source:Boolean	= false		/* true if we are the source.					*/
	var	sink:Boolean	= false		/* true if we are the sink.					*/
	var	edge: List[Edge] = Nil		/* adjacency list with edge objects shared with other nodes.	*/
	var	debug = false			/* to enable printing.						*/
	
	def min(a:Int, b:Int) : Int = { if (a < b) a else b }

	def id: String = "@" + index;

	def other(a:Edge, u:ActorRef) : ActorRef = { if (u == a.u) a.v else a.u }

	def status: Unit = { if (debug) println(id + " e = " + e + ", h = " + h) }

	def enter(func: String): Unit = { if (debug) { println(id + " enters " + func); status } }
	def exit(func: String): Unit = { if (debug) { println(id + " exits " + func); status } }

	def relabel : Unit = {

		enter("relabel")

		h += 1

		exit("relabel")
	}

	def flow(f: Int) : Unit = {
		enter("flow")

		e += f

		if (source) {
			control ! SourceChanged(e)
		}
		else if (sink) {
			control ! SinkChanged(e)
		}

		// e = f means node had zero excess before and can now push
		if (e == f && !source && !sink)
			self ! Discharge

		exit("flow")
	}

	def start : Unit = {
		enter("start")

		for (outEdge<-edge) {
			val neighbor = other(outEdge, self)
			val capacity = outEdge.c
	
			neighbor ! Flow(capacity)
	
			e -= capacity
			control ! SourceChanged(e)
			outEdge.f = capacity
		}

		exit("start")
	}

	def discharge : Unit = {
		enter("discharge")

		var p: List[Edge] = Nil
		var a: Edge = null

		k = edge.length
		pushed = false
		p = edge
		while (p != Nil) {
			a = p.head
			p = p.tail

			other(a, self) ! CanPush(h, a)
		}

		exit("discharge")
	}

	def canPush(height: Int, a: Edge) = {
		enter("can push")

		val from: ActorRef = other(a, self)
		var b = 1

		if (a.v == from)
			b = -1

		if (height > h && b * a.f < a.c)
			from ! Push(a)
		else
			from ! DonePush
		
		exit("can push")
	}

	def push(a: Edge) : Unit = {
		enter("push")

		if (e <= 0)
			return

		val to = other(a, self)
		var d = 0

		if (self == a.u) {
			d = min(e, a.c - a.f)
			a.f += d
		} else {
			d = min(e, a.c + a.f)
			a.f -= d
		}

		e -= d
		to ! Flow(d)

		if (source) {
			control ! SourceChanged(e)
		}
		else if (sink) {
			control ! SinkChanged(e)
		}

		assert(d >= 0)
		assert(e >= 0)
		assert(a.f.abs <= a.c)

		pushed = true
		self ! DonePush

		exit("push")
	}

	def donePush = {
		enter("done push")

		k -= 1

		if (k == 0) {
			if (!pushed)
				relabel
			if (e > 0)
				self ! Discharge
		}

		exit("done push")
	}

	def receive = {

	case Debug(debug: Boolean)	=> this.debug = debug

	case Print => status

	case Excess => { sender ! Flow(e) /* send our current excess preflow to actor that asked for it. */ }

	case edge:Edge => { this.edge = edge :: this.edge /* put this edge first in the adjacency-list. */ }

	case Control(control:ActorRef)	=> this.control = control

	case Sink	=> { sink = true }

	case Flow(f: Int) => flow(f)

	case Source(n:Int)	=> { h = n; source = true }

	case Start => start

	case Discharge => discharge

	case CanPush(height: Int, a: Edge) => canPush(height, a)

	case Push(a: Edge) => push(a)

	case DonePush => donePush

	case _		=> {
		println("" + index + " received an unknown message" + _) }

		assert(false)
	}

}


class Preflow extends Actor
{
	var	s	= 0;			/* index of source node.					*/
	var	t	= 0;			/* index of sink node.					*/
	var	n	= 0;			/* number of vertices in the graph.				*/
	var	edge:Array[Edge]	= null	/* edges in the graph.						*/
	var	node:Array[ActorRef]	= null	/* vertices in the graph.					*/
	var	ret:ActorRef 		= null	/* Actor to send result to.					*/
	var sourceE = 0
	var sinkE = 0

	def receive = {
		case node:Array[ActorRef]	=> {
			this.node = node
			n = node.size
			s = 0
			t = n-1
			for (u <- node)
				u ! Control(self)
		}

		case edge:Array[Edge] => this.edge = edge

		case Flow(f:Int) => {
			ret ! f			/* somebody (hopefully the sink) told us its current excess preflow. */
		}

		case SourceChanged(e: Int) => {
			sourceE = e

			if (sinkE.abs == sourceE.abs)
				self ! Flow(e.abs)
		}

		case SinkChanged(e: Int) => {
			sinkE = e

			if (sinkE.abs == sourceE.abs)
				self ! Flow(e.abs)
		}

		case Maxflow => {
			ret = sender		// Save sender for a future reply

			node(s) ! Source(n)	// Tell s it is source with height h=n
			node(t) ! Sink		// Tell t it is sink
			node(s) ! Start		// Tell s to do initial pushes
		}
	}
}

object main extends App {
	implicit val t = Timeout(500 seconds);

	val	begin = System.currentTimeMillis()
	val system = ActorSystem("Main")
	val control = system.actorOf(Props[Preflow], name = "control")

	var	n = 0;
	var	m = 0;
	var	edge: Array[Edge] = null
	var	node: Array[ActorRef] = null

	val	s = new Scanner(System.in);

	n = s.nextInt
	m = s.nextInt

	/* next ignore c and p from 6railwayplanning */
	s.nextInt
	s.nextInt

	node = new Array[ActorRef](n)

	for (i <- 0 to n-1)
		node(i) = system.actorOf(Props(new Node(i)), name = "v" + i)

	edge = new Array[Edge](m)

	for (i <- 0 to m-1) {

		val u = s.nextInt
		val v = s.nextInt
		val c = s.nextInt

		edge(i) = new Edge(node(u), node(v), c)

		node(u) ! edge(i)
		node(v) ! edge(i)
	}

	control ! node
	control ! edge

	val flow = control ? Maxflow
	val f = Await.result(flow, t.duration)

	println("f = " + f)

	system.stop(control);
	system.terminate()

	val	end = System.currentTimeMillis()

	println("t = " + (end - begin) / 1000.0 + " s")
}
