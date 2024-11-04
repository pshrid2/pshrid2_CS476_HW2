Here is an extended README file incorporating the new features you’ve implemented:

---

# Welcome to Breakspeare
## To be, to not be, to maybe?
### By Pranav Shridhar

Breakspeare is a domain-specific language (DSL) designed to facilitate the 
creation, manipulation, and evaluation of fuzzy sets within the context of 
fuzzy logic. Users can work with sets that support varying degrees of membership,
moving beyond binary inclusion.

### **Key Features**

1. **Fuzzy Set Creation:** Define fuzzy sets using flexible membership functions to 
2. determine how much an element belongs to a set. These sets support generic types, 
3. allowing membership functions over various data types. Custom functions are abstract 
4. by default, and Breakspeare simplifies their usage.

2. **Scope Management:** Supports nested scoping to manage variables in isolated contexts, 
3. preventing variable conflicts across operations.

3. **Class and Inheritance Support**: Implemented as part of the DSL, Breakspeare supports:
    - **Classes**: Define variables and methods within a class scope.
    - **Inheritance**: Extend classes to reuse methods and variables.
    - **Dynamic Dispatch**: Methods are invoked based on class inheritance hierarchy, 
    - ensuring the correct method is called when multiple definitions exist.

4. **Nested Classes**: Breakspeare allows defining and accessing nested classes, 
5. enabling layered class definitions within an instance.

5. **Fuzzy Set Operations**: An extensive set of operations enables manipulating fuzzy sets:
    - **Union**: Combines two fuzzy sets, taking the maximum membership degree for each element.
    - **Intersection**: Computes the minimum membership degree for elements in two sets.
    - **Complement**: Reverses membership values, essentially negating the fuzzy set.
    - **Addition, Multiplication, and Difference**: For advanced numerical operations on sets.
    - **Alpha Cut**: Creates a crisp set with elements above a specific membership threshold.

### **Example Usage**

The language enables users to define fuzzy sets, perform set operations, and test logical expressions. 
Below are examples illustrating the main features.

```scala
// Create two fuzzy sets
val setA = createFuzzySet((x: Double) => if (x > 0.5) 1.0 else 0.0)
val setB = createFuzzySet((x: Double) => if (x < 0.5) 1.0 else 0.0)

// Perform union operation
val unionSet = combine(setA, FuzzySetOperation.Union, Some(setB))

// Test De Morgan's law
val complementUnion = combine(unionSet, FuzzySetOperation.Complement, None)
val complementA = combine(setA, FuzzySetOperation.Complement, None)
val complementB = combine(setB, FuzzySetOperation.Complement, None)
val intersectionOfComplements = combine(complementA, FuzzySetOperation.Intersection, Some(complementB))

// Verification
assert(complementUnion.membership(0.3) == intersectionOfComplements.membership(0.3))
```

### **How to Use Breakspeare**

#### **1. Creating Fuzzy Sets**

Define fuzzy sets with custom membership functions to assign a degree of membership to each element.

```scala
val setA = createFuzzySet((x: Double) => if (x > 0.5) 1.0 else 0.0)
val setB = createFuzzySet((x: Double) => if (x < 0.5) 1.0 else 0.0)
```

#### **2. Class and Inheritance Features**

Breakspeare supports defining classes with variables, methods, inheritance, and nested classes.

```scala
// Define a class with a variable and a method
val baseClass = Class(
  name = "BaseClass",
  variables = List(ClassVar("x", DataType("Double"))),
  methods = List(Method("increaseX", List(Parameter("inc", DataType("Double"))), List(
    Variable("x"), FuzzyOperationExpr(FuzzySetOperation.Addition, Variable("x"), Some(Constant(1.0)))
  )))
)

// Define a derived class extending the base class
val derivedClass = Class(
  name = "DerivedClass",
  parent = Some(baseClass),
  methods = List(Method("doubleX", List(), List(
    Variable("x"), FuzzyOperationExpr(FuzzySetOperation.Multiplication, Variable("x"), Some(Constant(2.0)))
  )))
)
```

#### **3. Nested Classes**

Define and access nested classes in Breakspeare:

```scala
// Define a class with a nested class
val outerClass = Class(
  name = "OuterClass",
  nestedClasses = List(
    Class(
      name = "InnerClass",
      variables = List(ClassVar("innerVar", DataType("Double"))),
      methods = List(Method("setInnerVar", List(Parameter("value", DataType("Double"))), List(
        Assign("innerVar", Constant(2.0))
      )))
    )
  )
)

// Create an instance of the outer class and access the nested instance
val outerInstance = createInstance(outerClass)
val innerInstance = createNestedInstance(outerInstance, "InnerClass")
```

#### **4. Fuzzy Set Operations**

```scala
val unionSet = combine(setA, FuzzySetOperation.Union, Some(setB))
val complementSet = combine(setA, FuzzySetOperation.Complement, None)
```

#### **5. Dynamic Dispatch and Method Invocation**

Breakspeare’s method invocation follows dynamic dispatch in an inheritance hierarchy, ensuring the 
correct method is invoked based on the instance’s class.

```scala
val baseInstance = createInstance(baseClass)
invokeMethod(baseInstance, "increaseX", Map("inc" -> 1.0))

val derivedInstance = createInstance(derivedClass)
invokeMethod(derivedInstance, "doubleX", Map())
```

#### **6. Variable Management and Scoping**

Breakspeare supports variable assignment, retrieval, and scoping.

```scala
enterScope()
assignGate(Assign("temperature", setA))
val retrievedSet = get("temperature").asInstanceOf[FuzzySet[Double]]
exitScope()
```

#### **7. Evaluating Expressions**

Evaluate the degree of membership for specific elements using `evaluate`.

```scala
val testGate = TestGate(retrievedSet, 0.6)
val result = evaluateMembership(testGate)
```

---
