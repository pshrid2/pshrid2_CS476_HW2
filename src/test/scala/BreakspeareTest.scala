import org.scalatest.flatspec.AnyFlatSpec

class BreakspeareTests extends AnyFlatSpec {

  "Class inheritance" should "inherit methods and variables from parent class" in {
    val parentClass = Class("ParentClass", variables = List(ClassVar("x", DataType("int"))))
    val childClass = Class("ChildClass", parent = Some(parentClass))
    val childInstance = Breakspeare.createInstance(childClass)

    childInstance.setVariable("x", 42)
    assert(childInstance.getVariable("x") == 42) // Variable 'x' inherited from parent class
  }


  "Nested classes" should "support access to nested instances and their methods" in {
    val innerClass = Class("InnerClass", variables = List(ClassVar("y", DataType("int"))))
    val outerClass = Class("OuterClass", nestedClasses = List(innerClass))
    val outerInstance = Breakspeare.createInstance(outerClass)
    val innerInstance = Breakspeare.createNestedInstance(outerInstance, "InnerClass")

    innerInstance.setVariable("y", 99)
    assert(innerInstance.getVariable("y") == 99) // Accessing variable of nested class instance
  }

  "Fuzzy set operations" should "combine sets according to specified operations" in {
    val setA = Breakspeare.createFuzzySet[Double](x => if (x > 0.5) 1.0 else 0.0)
    val setB = Breakspeare.createFuzzySet[Double](x => if (x < 0.5) 1.0 else 0.0)
    val unionSet = Breakspeare.combine(setA, FuzzySetOperation.Union, Some(setB))

    assert(unionSet.membership(0.4) == 1.0) // Result of union operation
    assert(unionSet.membership(0.6) == 1.0) // Both sets have full membership in their range
  }



  "Dynamic dispatch" should "execute correct method in case of inheritance hierarchy" in {
    val parentMethod = Method("greet", List(), List(Constant("Hello from Parent")))
    val parentClass = Class("ParentClass", methods = List(parentMethod))
    val childMethod = Method("greet", List(), List(Constant("Hello from Child")))
    val childClass = Class("ChildClass", methods = List(childMethod), parent = Some(parentClass))
    val childInstance = Breakspeare.createInstance(childClass)

    val result = Breakspeare.invokeMethod(childInstance, "greet", Map())
    assert(result == "Hello from Child") // Verifies that child's method overrides parent's method
  }
}
