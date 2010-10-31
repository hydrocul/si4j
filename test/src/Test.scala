import scala.tools.nsc.InterpreterSifjResult;
import scala.tools.nsc.InterpreterResults;
import scala.tools.nsc.ResultValueInfo;

object Test {

  def main(args: Array[String]){

    println("BEGIN OF scala-interpreter test");

    val classPath = System.getProperty("java.class.path");
    val settings = new scala.tools.nsc.Settings;
    settings.classpath.value = classPath;
    val sout = new java.io.StringWriter;
    def clearSout = { val s = sout.getBuffer; s.delete(0, s.length); }
    val interpreter = new scala.tools.nsc.InterpreterSifj(settings,
      new java.io.PrintWriter(sout));

    case class StringPattern(pattern: String){
      def matches(str: String): Boolean = {
        val p = java.util.regex.Pattern.compile(pattern);
        val m = p.matcher(str);
        m.matches
      }
      override def toString: String = pattern;
    }

    var existsError: Boolean = false;

    def assert(expected: Any, result: Any): List[String] = {
      (expected, result) match {
        case (Some(ex), Some(re)) => assert(ex, re)
        case (ex: String, re: Throwable) => assert(ex, re.getClass.getName)
        case (ex: StringPattern, re: Throwable) => assert(ex, re.getClass.getName)
        case (ex1 :: ex2, re1 :: re2) => assert(ex1, re1) ::: assert(ex2, re2)
        case (ex: Product, re: Product) => {
          if(ex.productArity != re.productArity){
            existsError = true;
            ("expected: " + expected + ", but result: " + result) :: Nil
          } else if(ex.productArity == 0){
            if(expected==result) Nil
            else { existsError = true;
              ("expected: " + expected + ", but result: " + result) :: Nil }
          } else {
            List((0 until ex.productArity):_*).flatMap(i =>
              assert(ex.productElement(i), re.productElement(i)))
          }
        }
        case (ex: StringPattern, re: String) => if(ex.matches(re)) Nil
          else { existsError = true;
            ("expected pattern: " + ex + ", but result: " + re) :: Nil }
        case _ => if(expected==result) Nil
          else { existsError = true;
            ("expected: " + expected + ", but result: " + result) :: Nil }
      }
    }

    def testInterpreter(source: String,
      expectedResult: InterpreterResults.Result,
      expectedMessage: AnyRef,
      expectedValue: Option[(AnyRef, Any, String)],
      expectedException: Option[String],
      expectedAssignments: List[(AnyRef, Any, String)]): List[String] = {

      val result: InterpreterSifjResult = interpreter.interpretSifj(source, false);
      val errors = assert(expectedResult, result.result) :::
        assert(expectedMessage, result.message) :::
        assert(expectedValue, result.value) :::
        assert(expectedException, result.exception) :::
        assert(expectedAssignments, result.assignments) ::: Nil;

      if(errors.isEmpty){
        "OK --- source: " + source :: Nil
      } else {
        errors.mkString("FAILED source: " + source + "\n\t", "\n\t", "") :: Nil
      }

    }

    val msgs: List[String] =
      testInterpreter("\"abc\"", InterpreterResults.Success,
        StringPattern("res\\d+: java.lang.String = abc"),
        Some((StringPattern("res\\d+"), "abc", "java.lang.String")), None,
        (StringPattern("res\\d+"), "abc", "java.lang.String") :: Nil) :::
      testInterpreter("1 + 2", InterpreterResults.Success,
        StringPattern("res\\d+: Int = 3"),
        Some((StringPattern("res\\d"), 3, "Int")), None,
        (StringPattern("res\\d+"), 3, "Int") :: Nil) :::
      testInterpreter("val f = 1 + 2", InterpreterResults.Success,
        "f: Int = 3",
        Some(("f", 3, "Int")), None,
        ("f", 3, "Int") :: Nil) :::
      testInterpreter("def f = 1 + 2", InterpreterResults.Success,
        "f: Int",
        None, None,
        Nil) :::
      testInterpreter("def f(x: Int, y: Int) = x / y", InterpreterResults.Success,
        "f: (x: Int,y: Int)Int",
        None, None,
        Nil) :::
      testInterpreter("val f = 1 + 2\ndef g(x: Int) = x * 2", InterpreterResults.Success,
        "f: Int = 3\ng: (x: Int)Int",
        None, None,
        ("f", 3, "Int") :: Nil) :::
      testInterpreter("val f = 1 + 2\ndef g(x: Int) = ", InterpreterResults.Incomplete,
        "", None, None,
        Nil) :::
      testInterpreter("var x = 5 % 3", InterpreterResults.Success,
        "x: Int = 2",
        Some(("x", 2, "Int")), None,
        ("x", 2, "Int") :: Nil) :::
      //
      // test for AssignmentHandler
      testInterpreter("var x = 5 % 3; x = 3", InterpreterResults.Success,
        "x: Int = 3",
        Some(("x", 3, "Int")), None,
        ("x", 3, "Int") :: Nil) :::
      //
      // test for AssignmentHandler
      testInterpreter("var x = 3", InterpreterResults.Success,
        "x: Int = 3",
        Some(("x", 3, "Int")), None,
        ("x", 3, "Int") :: Nil) :::
      testInterpreter("x = 4", InterpreterResults.Success,
        "x: Int = 4",
        Some(("x", 4, "Int")), None,
        ("x", 4, "Int") :: Nil) :::
      // test for re-assignment to val
      testInterpreter("val x = 5", InterpreterResults.Success,
        "x: Int = 5",
        Some(("x", 5, "Int")), None,
        ("x", 5, "Int") :: Nil) :::
      testInterpreter("x = 4", InterpreterResults.Error,
        "", None, None,
        Nil) :::
      //
      // test for ModuleHandler
      testInterpreter("object testObj", InterpreterResults.Success,
        "defined module testObj", None, None,
        Nil) :::
      testInterpreter("object testObj { override def toString = \"testObj\" }",
        InterpreterResults.Success,
        "defined module testObj", None, None,
        Nil) :::
      //
      testInterpreter("var x = 5 % 3; var x = 3", InterpreterResults.Error,
        "", None, None,
        Nil) :::
      testInterpreter("var x = 5 % 3; val x = 3", InterpreterResults.Error,
        "", None, None,
        Nil) :::
      testInterpreter("1 / 0", InterpreterResults.Error,
        "", None, Some("java.lang.ArithmeticException"),
        Nil) :::
      testInterpreter("(1", InterpreterResults.Incomplete,
        "", None, None,
        Nil) :::
      testInterpreter("(1 +", InterpreterResults.Incomplete,
        "", None, None,
        Nil) :::
      testInterpreter("1 +", InterpreterResults.Error,
        "", None, None,
        Nil) ::: Nil;

    msgs.foreach(println(_));

    println("END OF scala-interpreter test");

    if(existsError){
      println("ERROR exists");
      System.exit(1);
    }

  }

}
