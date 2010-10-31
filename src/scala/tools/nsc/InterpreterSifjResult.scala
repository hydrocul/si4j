package scala.tools.nsc;

case class InterpreterSifjResult (
  result: InterpreterResults.Result,
  message: String,
  value: Option[ResultValueInfo],
  exception: Option[Throwable],
  assignments: List[ResultValueInfo]){

  def append(message: String, value: Option[ResultValueInfo],
    assignments: List[ResultValueInfo]): InterpreterSifjResult =
    InterpreterSifjResult(result,
      ( if(this.message.isEmpty) ""
        else this.message + "\n" ) + message.trim,
      value, this.exception, this.assignments ::: assignments);

  def append(message: String): InterpreterSifjResult =
    InterpreterSifjResult(result,
      ( if(this.message.isEmpty) ""
        else this.message + "\n" ) + message.trim,
      None, this.exception, this.assignments);

}

object InterpreterSifjResult {

  def apply(result: InterpreterResults.Result): InterpreterSifjResult = {
    if(result==InterpreterResults.Success){
      throw new AssertionError;
    }
    InterpreterSifjResult(result, "", None, None, Nil);
  }

}
