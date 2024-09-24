// For week 5
// raup@itu.dk * 05/10/2022
// jst@itu.dk * 23/9/2023
// raup@itu.dk * 2024-09-18
package exercises05;

// Warning: Non-thread safe class
// To be used only sequentially
class Histogram1 implements Histogram {
    private int[] counts;

    public Histogram1(int span) {
        this.counts = new int[span];
    }

    public void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }

    public int getCount(int bin) {
        return counts[bin];
    }

    public int getSpan() {
        return counts.length;
    }

    public int getAndClear(int bin) {
        int returnValue = getCount(bin);
        counts[bin] = 0;
        return returnValue;
    }
}
