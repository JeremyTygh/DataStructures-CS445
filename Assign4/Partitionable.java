// CS 0445 Spring 2020
// Code for various versions of QuickSort (much taken from the Carrano text)
// Note that this code is designed for readability and modularity.  It is
// not necessarily the most efficient way of implementing these algorithms.

public interface Partitionable<T extends Comparable<? super T>> 
{
	public int partition(T[] a, int first, int last);
}