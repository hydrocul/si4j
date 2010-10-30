import scala.tools.nsc.InterpreterSifjResult;
import scala.tools.nsc.InterpreterResults;
import scala.tools.nsc.ResultValueInfo;

object Test {

  def main(args: Array[String]){

    println("scala-interpreter test start");

    val classPath = System.getProperty("java.class.path");
    val settings = new scala.tools.nsc.Settings;
    settings.classpath.value = classPath;
    val sout = new java.io.StringWriter;
    def clearSout = { val s = sout.getBuffer; s.delete(0, s.length); }
    val interpreter = new scala.tools.nsc.InterpreterSifj(settings,
      new java.io.PrintWriter(sout));

    def assert(expected: AnyRef, result: AnyRef): List[String] = {
      if(result!=expected){
        ("expected: " + expected + ", but result: " + result) :: Nil
      } else {
        Nil
      }
    }

    def testInterpreter(source: String,
      expectedResult: InterpreterResults.Result,
      expectedMessage: String,
      expectedValue: Option[ResultValueInfo],
      expectedException: Option[Throwable],
      expectedAssignments: List[ResultValueInfo]): List[String] = {

      val result: InterpreterSifjResult = interpreter.interpretSifj(source, false);
      val errors = assert(expectedResult, result.result) :::
        assert(expectedMessage, result.message) :::
        assert(expectedValue, result.value) :::
        assert(expectedException, result.exception) :::
        assert(expectedAssignments, result.assignments) ::: Nil;

      if(errors.isEmpty){
        Nil
      } else {
        errors.mkString("source: " + source + "\n\t", "\n\t", "") :: Nil
      }

    }

    val msgs: List[String] =
      testInterpreter("1 / 0", InterpreterResults.Error,
        "", None, None,
        Nil) :::
      testInterpreter("1 + 2", InterpreterResults.Success,
        "res1: Int = 3\n", Some(ResultValueInfo("res0", 3, "Int")), None,
        ResultValueInfo("res1", 3, "Int") :: Nil) ::: Nil;

    msgs.foreach(println(_));

    println("scala-interpreter test end");

  }

}
