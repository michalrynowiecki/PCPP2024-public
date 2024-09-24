// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;
import java.util.concurrent.locks.ReentrantLock;

public class TestLongCounterExperiments {

    private final ReentrantLock lock = new ReentrantLock();
    LongCounter lc = new LongCounter();
    int counts = 3;

    public TestLongCounterExperiments() {
        Thread t1 = new Thread(() -> {
                for (int i=0; i<counts; i++){
                    lc.increment(lock);
                }
        });
        Thread t2 = new Thread(() -> {
                for (int i=0; i<counts; i++){
                    lc.increment(lock);
                }
        });
        t1.start(); t2.start();
        try { t1.join(); t2.join(); }
        catch (InterruptedException exn) { 
            System.out.println("Some thread was interrupted");
        }
        System.out.println("Count is " + lc.get() + " and should be " + 2*counts);
    }
    
    public static void main(String[] args) {
        new TestLongCounterExperiments();
    }

    class LongCounter {
        private long count = 0;    
     
        public void increment(ReentrantLock lc) {
            //lc.lock();
            //try {
                count = count + 1;
            //}
            //finally {lc.unlock();}
        }

        public void decrement(ReentrantLock lc) {
            lc.lock();
            try {count = count - 1;}
            finally {lc.unlock();}
        }
    
        public long get() {
            return count;
        }
    }
}

/* TEXT ANSWERS
 * 1.1.1: 
 * Q: The main method creates a LongCounter object. Then it creates and starts two threads that run concurrently,
 * and each increments the count field 10 million times by calling method increment.
 * What output values do you get? Do you get the expected output, i.e., 20 million?
 * 
 * A: No, I do not get the expected output. The output I get is between 19 and 20 milion.
 * 
 * 1.1.2:
 * Q: Reduce the counts value from 10 million to 100, recompile, and rerun the code. It is now likely that you
 * get the expected result (200) in every run.
 * Explain how this could be. Is it guaranteed that the output is always 200?
 * 
 * A: This is because the times the threads can fail synchronizing is significantly lower. Since they are started
 * sequentially, initially they are in synch. But due to various events happening in the CPU, they might fall out
 * of this synch, and read a stale value, or write one. This is more likely to happen the more statements each
 * thread executes.
 * 
 * 1.1.3:
 * Q: The increment method in LongCounter uses the assignment
 * count = count + 1;
 * to add one to count. This could be expressed also as count += 1 or as count++.
 * Do you think it would make any difference to use one of these forms instead? Why? Change the code and
 * run it. Do you see any difference in the results for any of these alternatives?
 * 
 * A: I do not think changing the form will make a difference, because these get transformed to the same
 * three statements by the compiler.
 * I see no differences in execution between these three forms of incrementation.
 *
 * 1.1.4:
 * Q: Set the value of counts back to 10 million. Use Java ReentrantLock to ensure that the output of the
 * program equals 20 million. Explain why your solution is correct, and why no other output is possible.
 * 
 * A: My solution is correct because the critical section, which is
 * the incrementation, is now guarded by a lock that is acquired by
 * each thread before the execution and released after, thus eliminating
 * the race condition which was accessing the shared state.
 * 
 * 1.1.5:
 * 
 * Q: By using the ReetrantLock in the exercise above, you have defined a critical section. Does your critical
 * section contain the least number of lines of code? If so, explain why. If not, fix it and explain why your new
 * critical section contains the least number of lines of code.
 * 
 * A: Initially my critical section contained the entire increment method. Now it contains only the increment
 * statement. Therefore, it cannot be further reduced and is minimal.
 * 
 * CHALLENGING
 * 
 * 1.1.6:
 * 
 * Q: Decompile the methods increment from above to see the byte code in the three versions (as is, +=, ++).
 * The basic decompiler is javap. The decompiler takes as input a (target) .class file. In Gradle projects,
 * .class files are located in the directory app/build/classes/—after compiling the .java files.
 * The flag -c decompiles the code of a class. Does the output of javap verify or refuse the explanation you
 * provided in part 3.?
 * 
 * A: It confirms my answer, since the byte encodings are exactly the same
 * 
 * 1.1.7:
 * 
 * Q: Extend the LongCounter class with a decrement() method which subtracts 1 from the count field
 * without using locks. Change the code in main so that t1 calls decrement 10 million times, and t2 calls
 * increment 10 million times, on a LongCounter instance. What should the expected output be after
 * both threads have completed?
 * Use ReentrantLock to ensure that the program outputs 0. Explain why your solution is correct, and
 * why no other output is possible.
 *
 * A: My solution is correct for the same reasons that using the lock on two incrementing methods was correct.
 * The locks eliminate the race conditions. The actual change to the shared state doesn't matter so long as
 * the access to it is limited to only a single thread.
 * 
 * 1.1.8:
 * 
 * Q: Explain, in terms of the happens-before relation, why your solution for part 4 produces the expected output.
 * 
 * A: The only variable being changed by the threads is count during the increment method. This method is
 * only possible to access with the lock. The lock is shared between threads, and only a single thread can
 * have access to the count variable at a time.
 * This means that the incrementing of the value always happens before the other thread can obtain the lock.
 * Therefore, all the values accessed by threads will be their most-recent values, forming a happens-before
 * relationship between writing and reading of count between threads.
 * 
 * 1.1.9:
 * 
 * Q: Again for the code in part 4 (i.e., without decrement()), remove the ReetrantLock you added. Set
 * the variable counts to 3.
 * What is the minimum value that the program prints? Does the minimum value change if we set counts to
 * a larger number (e.g., 20 million)?
 * Provide an interleaving showing how the minimum value output can occur.
 * 
 * A: 
 * 
 * Interleaving of min value: 
 * increment: read -> change -> write
 * 
 * read -> read -> change -> write -> change -> write = 4
 * read -> read -> change -> write -> change -> write = 5
 * read -> read -> change -> write -> change -> write = 6
 * 
 * 
 * The minimum value is equal to S + N/T where S = starting value, N = counts and T = number of threads, therefore in this case
 * the minimum value is 6. Yes, the minimum value changes with a larger number.
 */


