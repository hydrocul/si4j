package scala.tools.nsc;

case class InterpreterSifjResult (
  result: InterpreterResults.Result,
  messages: List[String],
  value: Option[ResultValueInfo],
  exception: Option[Throwable],
  assignments: List[ResultValueInfo]){

  def message: String = messages.mkString("\n");

  def append(message: String, value: Option[ResultValueInfo],
    assignments: List[ResultValueInfo]): InterpreterSifjResult = {
    val m: List[String] = {
      val m2 = message.trim;
      val i = messages.indexOf(m2);
      if(i < 0) messages ::: (m2 :: Nil)
      else messages.take(i) ::: messages.drop(i + 1) ::: (m2 :: Nil)
    }
    val a: List[ResultValueInfo] = {
      var a = this.assignments;
      assignments.foreach { as =>
        val i = a.indexOf(as);
        a = if(i < 0) a ::: (as :: Nil)
          else a.take(i) ::: a.drop(i + 1) ::: (as :: Nil)
      }
      a
    }
    InterpreterSifjResult(result, m,
      value, this.exception, a);
  }

  def append(message: String): InterpreterSifjResult =
    append(message, None, Nil);

}

object InterpreterSifjResult {

  def apply(result: InterpreterResults.Result,
    message: String,
    value: Option[ResultValueInfo],
    exception: Option[Throwable],
    assignments: List[ResultValueInfo]): InterpreterSifjResult = {
    InterpreterSifjResult(result, (if(message.isEmpty) Nil else message :: Nil),
      value, exception, assignments)
  }

  def apply(result: InterpreterResults.Result): InterpreterSifjResult = {
    if(result==InterpreterResults.Success){
      throw new AssertionError;
    }
    InterpreterSifjResult(result, "", None, None, Nil);
  }

}
