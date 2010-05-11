package cep.fusion;

import junit.framework._;
import Assert._;
import cep.fusion.CEP._
import CEP.MyTimer._
import java.util.concurrent.TimeUnit

object CEPTest {
    def suite: Test = {
        val suite = new TestSuite(classOf[CEPTest]);
        suite
    }

    def main(args : Array[String]) {
        junit.textui.TestRunner.run(suite);
    }
}

/**
 * Unit test for CEP.
 */
class CEPTest extends TestCase("CEP") {

  implicit val (session, stream, clock) = start   
  
    /**
     * Case 1 must fire exactly fire two rules
     */
  def testCase1 = {
      

   stream.insert(TemperatureChange(135, advanceSeconds(1)))
   stream.insert(TemperatureChange(140, advanceSeconds(3)))
   stream.insert(TemperatureChange(143, advanceSeconds(4)))
   assertEquals(1, session.fireAllRules) 
    
  }

    /**
     * Case 1 must fire exactly fire one rule
     */
  def testCase2 = {
     
     stream.insert(TemperatureChange(120, advanceSeconds(2)))
     stream.insert(TemperatureChange(131, advanceSeconds(1)))
     assertEquals(1, session.fireAllRules)
   }

}
