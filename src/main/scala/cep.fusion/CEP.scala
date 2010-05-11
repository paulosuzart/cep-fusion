package cep.fusion;

import org.drools.runtime.KnowledgeSessionConfiguration
import org.drools.runtime.rule.WorkingMemoryEntryPoint
import org.drools.KnowledgeBaseFactory
import org.drools.KnowledgeBaseConfiguration
import org.drools.builder._
import org.drools.runtime.conf.ClockTypeOption
import org.drools.conf.EventProcessingOption
import org.drools.time._
import org.drools.io._
import org.drools.runtime.StatefulKnowledgeSession
import java.util.concurrent.TimeUnit
import org.drools.logger._
import java.util.Date

/**
 * My CEP engine stuff
 */
object CEP {
		
    // I want to use pseudoclock, but event time should walk together
	object MyTimer {
      var currentTime = java.util.Calendar.getInstance.getTime
      
      //advance time and return the current time changed
      def advanceSeconds(a : Int)(implicit clock : SessionPseudoClock) = {
    	  clock.advanceTime(a, TimeUnit.SECONDS)
          currentTime.setSeconds(currentTime.getSeconds + 1); 
          currentTime
      }
      
    }
  
	
    private def kbConfig = {
	    val kbc = KnowledgeBaseFactory.newKnowledgeBaseConfiguration
		kbc.setOption(EventProcessingOption.STREAM)
		kbc
    }
	
	private def createKB(configuration : KnowledgeBaseConfiguration) = {
		val kb  = KnowledgeBaseFactory.newKnowledgeBase(configuration)
	
	    val kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	
	    kbuilder.add( ResourceFactory.newClassPathResource( "rules.drl", getClass() ),
	              ResourceType.DRL );
	 
	    if (kbuilder.hasErrors() ) 
	    	System.out.println( kbuilder.getErrors() );
     
     	kb.addKnowledgePackages( kbuilder.getKnowledgePackages())
	    kb
	}
    
	private def sessionConfig = {
	    val conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration
	    conf.setOption(ClockTypeOption.get("pseudo")); 
	    conf
	}

    //Actually instantiate a new Knowledge Base, a Stateful Session, Clock and Stream 
    def start : (StatefulKnowledgeSession, WorkingMemoryEntryPoint, SessionPseudoClock) = {
	  val kb = createKB(kbConfig)
      val session = kb.newStatefulKnowledgeSession(sessionConfig, null)
	  val clock = session.getSessionClock.asInstanceOf[SessionPseudoClock]
	  val stream =  session.getWorkingMemoryEntryPoint("temp-change")     		

	  (session, stream, clock)
	 }
 
  
  import scala.reflect.BeanProperty
  /**
   * My Event. A fact declared as an event inside the DRL
   */
  case class TemperatureChange(@BeanProperty temperature : Double, @BeanProperty time : Date)

}


