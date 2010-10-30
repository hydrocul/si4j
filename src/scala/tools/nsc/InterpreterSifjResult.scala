package scala.tools.nsc;

case class InterpreterSifjResult (
  result: InterpreterResults.Result,
  message: String,
  value: Option[InterpreterSifjResult],
  exception: Option[Throwable],
  assignments: List[InterpreterSifjResult]){

  def append(message: String, value: Option[InterpreterSifjResult],
    assignments: List[InterpreterSifjResult]): InterpreterSifjResult =
    InterpreterSifjResult(result,
      ( if(this.message.isEmpty) ""
        else this.message + "\n" ) + message.trim,
      value, this.exception, this.assignments ::: assignments);

}

object InterpreterSifjResult {

  def apply(result: InterpreterResults.Result): InterpreterSifjResult = {
    if(result==InterpreterResults.Success){
      throw new AssertionError;
    }
    InterpreterSifjResult(result, "", None, None, Nil);
  }

}
