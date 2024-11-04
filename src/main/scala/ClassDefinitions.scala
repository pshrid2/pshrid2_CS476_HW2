import scala.collection.mutable

// Expression-related definitions
trait Expression

// Basic expression types
case class Constant(value: Any) extends Expression
case class Variable(name: String) extends Expression

// Expression for fuzzy operations
case class FuzzyOperationExpr(
                               operation: FuzzySetOperation,
                               setA: FuzzySet[Double],
                               setB: Option[FuzzySet[Double]] = None,
                               alpha: Option[Double] = None
                             ) extends Expression

// Class-related constructs
case class DataType(name: String)
case class Parameter(name: String, paramType: DataType)
case class ClassVar(name: String, varType: DataType)
case class Method(name: String, parameters: List[Parameter], body: List[Expression])

// Class definition with inheritance and nested class support
case class Class(
                  name: String,
                  variables: List[ClassVar] = List(),
                  methods: List[Method] = List(),
                  parent: Option[Class] = None, // Optional parent class for inheritance
                  nestedClasses: List[Class] = List()
                ) {
  // Retrieve all variables, including inherited ones
  def getVariables: List[ClassVar] = {
    val parentVars = parent.map(_.getVariables).getOrElse(List())
    parentVars.filterNot(v => variables.exists(_.name == v.name)) ++ variables
  }

  // Retrieve all methods, including inherited ones
  def getMethods: List[Method] = {
    val parentMethods = parent.map(_.getMethods).getOrElse(List())
    parentMethods.filterNot(m => methods.exists(_.name == m.name)) ++ methods
  }

  // Retrieve all nested classes, including those in nested levels
  def getNestedClasses: List[Class] = nestedClasses ++ nestedClasses.flatMap(_.getNestedClasses)
}

// Instance definition that includes inheritance and nested class handling
case class Instance(classDefinition: Class, parentInstance: Option[Instance] = None) {
  val instanceEnvironment: mutable.Map[String, Any] = mutable.Map()

  // Initialize all variables, including inherited ones
  for (variable <- classDefinition.getVariables) {
    instanceEnvironment(variable.name) = None // Placeholder for uninitialized variables
  }

  // Initialize nested instances for each nested class
  val nestedInstances: Map[String, Instance] = classDefinition.nestedClasses.map { nestedClass =>
    nestedClass.name -> Instance(nestedClass, Some(this))
  }.toMap

  // Set a variable, checking both current and parent instance
  def setVariable(name: String, value: Any): Unit = {
    if (instanceEnvironment.contains(name)) {
      instanceEnvironment(name) = value
    } else if (parentInstance.isDefined) {
      parentInstance.get.setVariable(name, value)
    } else {
      throw new IllegalArgumentException(s"Variable '$name' not found in class ${classDefinition.name}")
    }
  }

  // Get a variable, searching current and parent instance
  def getVariable(name: String): Any = {
    instanceEnvironment.get(name)
      .orElse(parentInstance.flatMap(_.getVariableOption(name)))
      .getOrElse(throw new IllegalArgumentException(s"Variable '$name' not found in class ${classDefinition.name}"))
  }

  // Helper function to get variable as Option for internal use
  private def getVariableOption(name: String): Option[Any] = instanceEnvironment.get(name)

  // Retrieve a nested instance by its class name, checking parent if needed
  def getNestedInstance(nestedClassName: String): Option[Instance] = {
    nestedInstances.get(nestedClassName)
      .orElse(parentInstance.flatMap(_.getNestedInstance(nestedClassName)))
  }

  // Find a method, including inherited ones
  def findMethod(methodName: String): Option[Method] = {
    classDefinition.getMethods.find(_.name == methodName)
  }
}
