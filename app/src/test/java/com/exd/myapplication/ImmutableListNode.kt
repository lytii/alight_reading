package com.exd.myapplication

/**
 * // This is the ImmutableListNode's API interface.
 * // You should not implement it, or speculate about its implementation.
 * class ImmutableListNode {
 *     fun getNext(): ImmutableListNode? {} // return the next node.
 *     fun printValue() {} // print the value of this node.
 * };
 */


interface ImmutableListNode {
    fun getNext(): ImmutableListNode? // return the next node.
    fun printValue() // print the value of this node.
}