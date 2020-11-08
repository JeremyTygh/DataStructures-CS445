public class QuickSort<T extends Comparable<? super T>>
implements Sorter<T>
{
      private Partitionable<T> partAlgo;
      private int MIN_SIZE;  // min size to recurse, use InsertionSort
                             // for smaller sizes to complete sort
      public QuickSort(Partitionable<T> part)
      {
            partAlgo = part;
            MIN_SIZE = 3;
      }
      // remaining code in QuickSort class not shown
      // You must complete this class – in particular the methods
      // sort() and setMin()  You will use partAlgo for partition
      // within the sort() method.

      public void sort(T[] a, int size)
      {
            quickSort(a, 0, size-1);
      }

      public void setMin(int minSize)
      {
            MIN_SIZE = minSize;     // sets the min size
      }

      public void quickSort(T[] a, int first, int last)
      {
        if (last - first + 1 < MIN_SIZE)
        {
          insertionSort(a, first, last);
        }
        else
        {
          // create the partition: Smaller | Pivot | Larger
          int pivotIndex = partAlgo.partition(a, first, last);
          
          // sort subarrays Smaller and Larger
          quickSort(a, first, pivotIndex - 1);
          quickSort(a, pivotIndex + 1, last);
        } // end if
      } // end quickSort

      public static <T extends Comparable<? super T>> 
             void insertionSort(T[] a, int first, int last)
      {
            int unsorted, index;
            
            for (unsorted = first + 1; unsorted <= last; unsorted++)
            {   // Assertion: a[first] <= a[first + 1] <= ... <= a[unsorted - 1]
            
                  T firstUnsorted = a[unsorted];
                  
                  insertInOrder(firstUnsorted, a, first, unsorted - 1);
            } // end for
      } // end insertionSort

      private static <T extends Comparable<? super T>> 
              void insertInOrder(T element, T[] a, int begin, int end)
      {
            int index;
            
            for (index = end; (index >= begin) && (element.compareTo(a[index]) < 0); index--)
            {
                  a[index + 1] = a[index]; // make room
            } // end for
            
            // Assertion: a[index + 1] is available
            a[index + 1] = element;  // insert
      } // end insertInOrder


}