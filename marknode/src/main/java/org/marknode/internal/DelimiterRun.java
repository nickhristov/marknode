package org.marknode.internal;

class DelimiterRun {

    final int count;
    final boolean canClose;
    final boolean canOpen;

    DelimiterRun(int count, boolean canOpen, boolean canClose) {
        this.count = count;
        this.canOpen = canOpen;
        this.canClose = canClose;
    }

}
