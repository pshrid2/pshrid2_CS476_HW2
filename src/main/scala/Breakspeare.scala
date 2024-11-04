import scala.collection.mutable

object Breakspeare {
  private val environment: mutable.Map[String, FuzzySet[Any]] = mutable.Map()
  type EnvironmentTable = mutable.Map[String, FuzzySet[Any]]

  private val environmentStack: List[EnvironmentTable] = List(mutable.Map[String, FuzzySet[Any]]())
  private var currentStack: List[EnvironmentTable] = environmentStack

  // Scope management
  def enterScope(): Unit = {
    currentStack = mutable.Map[String, FuzzySet[Any]]() :: currentStack
  }

  def exitScope(): Unit = {
    currentStack = currentStack.tail
  }

  // Instance-specific scope management
  def enterInstanceScope(instanceEnv: mutable.Map[String, Any]): Unit = {
    currentStack = instanceEnv.asInstanceOf[EnvironmentTable] :: currentStack
  }

  def exitInstanceScope(): Unit = {
    currentStack = currentStack.tail
  }

  // Variable assignment within the current scope
  def assign[T](name: String, fuzzySet: FuzzySet[T]): Unit = {
    currentStack.head(name) = fuzzySet.asInstanceOf[FuzzySet[Any]]
  }

  // Variable retrieval within the current scope
  def get(name: String): FuzzySet[Any] = {
    currentStack.head.get(name) match {
      case Some(fuzzySet) => fuzzySet
      case None => throw new IllegalArgumentException(s"Variable '$name' not found in the current scope.")
    }
  }

  // Fuzzy set operations
  def createFuzzySet[T](membershipFunction: T => Double): FuzzySet[T] = UserDefinedFuzzySet(membershipFunction)

  def combine[T](setA: FuzzySet[T], operation: FuzzySetOperation, setB: Option[FuzzySet[T]], alpha: Option[Double] = None): FuzzySet[T] = {
    CombinedFuzzySet(setA, setB, operation, alpha)
  }

  case class TestGate[T](fuzzySet: FuzzySet[T], value: T)

  def evaluateMembership[T](testGate: TestGate[T]): Double = testGate.fuzzySet.membership(testGate.value)

  // Expression evaluation within an instance's environment
  def evaluate(expr: Expression, instance: Instance): Any = expr match {
    case Constant(value) => value
    case Variable(name) => instance.getVariable(name)
    case FuzzyOperationExpr(operation, setA, setB, alpha) => combine(setA, operation, setB, alpha)
  }

  // Create instance and enter its scope
  def createInstance(classDefinition: Class): Instance = {
    val instance = Instance(classDefinition)
    enterInstanceScope(instance.instanceEnvironment)
    instance
  }

  // Create nested instance within a given parent instance
  def createNestedInstance(outerInstance: Instance, nestedClassName: String): Instance = {
    outerInstance.getNestedInstance(nestedClassName)
      .getOrElse(throw new IllegalArgumentException(s"Nested class '$nestedClassName' not found in ${outerInstance.classDefinition.name}"))
  }

  // Method invocation within an instance
  def invokeMethod(instance: Instance, methodName: String, args: Map[String, Any], inNestedClass: Boolean = false): Any = {
    enterInstanceScope(instance.instanceEnvironment)
    val methodOpt = instance.findMethod(methodName)

    methodOpt match {
      case Some(method) =>
        val methodEnvironment = mutable.Map[String, Any]()
        args.foreach { case (param, value) => methodEnvironment(param) = value }
        currentStack = methodEnvironment.asInstanceOf[EnvironmentTable] :: currentStack

        val result = method.body.map(expr => evaluate(expr, instance)).lastOption.getOrElse(None)

        currentStack = currentStack.tail
        exitInstanceScope()
        result

      case None => throw new IllegalArgumentException(s"Method '$methodName' not found in class ${instance.classDefinition.name}")
    }
  }
}
