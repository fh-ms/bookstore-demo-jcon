package one.microstream.demo.bookstore.util.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ReadWriteLocked
{
	private transient volatile ReentrantReadWriteLock mutex;

	public ReadWriteLocked()
	{
		super();
	}
	
	private ReentrantReadWriteLock mutex()
	{
		/*
		 * Double-checked locking to reduce the overhead of acquiring a lock
		 * by testing the locking criterion.
		 * The field (this.mutex) has to be volatile.
		 */
		ReentrantReadWriteLock mutex = this.mutex;
		if(mutex == null)
		{
			synchronized(this)
			{
				if((mutex = this.mutex) == null)
				{
					mutex = this.mutex = new ReentrantReadWriteLock();
				}
			}
		}
		return mutex;
	}

	public <T> T read(final ValueOperation<T> operation)
	{
		final ReadLock readLock = this.mutex().readLock();
		readLock.lock();

		try
		{
			return operation.execute();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public void read(final VoidOperation operation)
	{
		final ReadLock readLock = this.mutex().readLock();
		readLock.lock();

		try
		{
			operation.execute();
		}
		finally
		{
			readLock.unlock();
		}
	}

	public <T> T write(final ValueOperation<T> operation)
	{
		final WriteLock writeLock = this.mutex().writeLock();
		writeLock.lock();

		try
		{
			return operation.execute();
		}
		finally
		{
			writeLock.unlock();
		}
	}

	public void write(final VoidOperation operation)
	{
		final WriteLock writeLock = this.mutex().writeLock();
		writeLock.lock();

		try
		{
			operation.execute();
		}
		finally
		{
			writeLock.unlock();
		}
	}

}
