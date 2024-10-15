# Unit Testing (JUnit)

We use unit tests to ensure all foundational code of our repository works as intended with the click of a button. This prevents small changes from unknowingly adding bugs to the code.

## What Should Be Tested
Any method that is even slightly complex should be tested. Extremely simple methods do not *need* to be tested, but it can't hurt.

Op modes and hardware-reliant methods do not need to be tested.

## Testing File Locations

Each tested class will have an extra file with the suffex "Test" added. 
This will be located in the same package as the regular class, but in TeamCode/src/test instead of TeamCode/src/main.

### Example:
```
TeamCode
└── src
	├── main
	│   └── java
	│		└── t10
	│			└── utils
	│				└── MathUtils.java
	└── test
		└── java
			└── t10
				└── utils
					└── MathUtilsTest.java
```

## Writing Tests

### Test Declaration
A test is written as a method following this template:
```
1. @Test  
2. @DisplayName("<method signature>")  
3. void <methodName>() {  

}
```

Line 1 is an annotation marking this method as a test.

Line 2 is an annotation setting the name of the test. Our convention is the  signature of the method being tested in the following format:
**returnType methodName(parameters)**

Line 3 declares the test method with a void return type. The name of the testing method should be the same as the method being tested.

### Testing the Method
Now that the testing method has been declared, the real testing can be written. To do this, we use JUnit to ensure the results of the method being tested are correct. 

Here are some of the most common JUnit methods used for this:

1. assertEquals(expected, actual) - confirm that an actual value returned from the method being tested is equal to the expected value
2. assertNotEquals(unexpected, actual) -  the opposite of assertEquals
3. assertArrayEquals(expectedArray, actualArray) - assertEquals, but for all values in two arrays
4. assertNull(actual) - confirm that an object is null
5. assertThrows(errorType, executable) - confirm that an error is thrown when a given [executable](https://junit.org/junit5/docs/5.0.3/api/org/junit/jupiter/api/function/Executable.html) is executed

Use these methods to test all parts of the output of the method. If a method runs differently depending on the input (uses switch/if statements), all paths should be tested using different test methods.

## Extra Tips
- After running a method on an object, confirm that the object has not been modified in any unintended way. This is easy with lists using the *assertArrayEquals* method and *toArray* method.

