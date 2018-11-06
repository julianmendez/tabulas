package de.tudresden.inf.lat.tabulas.ext.parser

import org.scalatest.FunSuite

/** Test class of MyStack[A]
  */
class MyStackTest extends FunSuite {

  test("it should push and pop values in the right order") {
    val calPar = CalendarParser()
    val myStack = new calPar.MyStack[String]

    assert(myStack.size === 0)

    myStack.push("1")
    myStack.push("2")
    assert(myStack.size === 2)

    myStack.push("2")
    assert(myStack.size === 3)

    myStack.push("3")
    assert(myStack.size === 4)

    val elem3: String = myStack.pop()
    assert(elem3 === "3")
    assert(myStack.size === 3)

    val elem2: String = myStack.pop()
    assert(elem2 === "2")
    assert(myStack.size === 2)

    val elem1: String = myStack.pop()
    assert(elem1 === "2")
    assert(myStack.size === 1)

    val elem0: String = myStack.pop()
    assert(elem0 === "1")
    assert(myStack.size === 0)
  }

  test("it should throw a NoSuchElementException after popping from an empty stack") {
    val calPar = CalendarParser()
    val myStack = new calPar.MyStack[String]
    intercept[NoSuchElementException] {
      myStack.pop()
    }
  }

}

object MyStackTest {

  def apply(): MyStackTest = new MyStackTest

}
