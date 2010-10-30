//import scala.tools.nsc.InterpreterExtResults;
//import scala.tools.nsc.InterpreterResults;

object Test {

  def main(args: Array[String]){

    println("scala-interpreter test start");

/*
    val classPath = System.getProperty("java.class.path");
    val settings = new scala.tools.nsc.Settings;
    settings.classpath.value = classPath;
    val sout = new java.io.StringWriter;
    def clearSout = { val s = sout.getBuffer; s.delete(0, s.length); }
    val interpreter = new scala.tools.nsc.Interpreter(settings,
      new java.io.PrintWriter(sout));

    def assert(expected: AnyRef, result: AnyRef): List[String] = {
      if(result!=expected){
        ("expedted: " + expected + ", but result: " + result) :: Nil
      } else {
        Nil
      }
    }

    def testInterpreter(source: String,
      expectedResult: InterpreterResults.Result,
      expectedMessage: String,
      expectedObject: (String, Any, String),
      expectedException: Option[Throwable],
      expectedAssignments: List[(String, Any, String)]): List[String] = {

      val result: InterpreterExtResults = interpreter.interpret(source, false, None);
      val errors = assert(expectedResult, result.interpreterResult) :::
        assert(expectedMessage, result.resultMessages) :::
        assert(expectedObject, result.resultObject) :::
        assert(expectedException, result.exception) :::
        assert(expectedAssignments, result.assignments) ::: Nil;

      if(errors.isEmpty){
        Nil
      } else {
        errors.mkString("source: " + source + "\n\t", "\n\t", "") : Nil
      }

    }

    val msgs =
      testInterpreter("1 + 2", InterpreterResults.Success,
        "res0: Int = 3", ("res0", 3, "Int"), None,
        ("res0", 3, "Int") :: Nil) :: Nil;

    msgs.foreach(println(_));
*/

    println("scala-interpreter test end");

  }

}
