package tw.freddie.ledcolor.commands;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Command {
    private static AtomicInteger mAtomicInteger = new AtomicInteger();
    private int mCommandID;

    public Command() {
        mCommandID = mAtomicInteger.incrementAndGet();
    }

    public int getId() {
        return mCommandID;
    }

    public abstract byte[] getRawData();
}
