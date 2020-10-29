package one.microstream.demo.bookstore.util.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import com.google.common.util.concurrent.Striped;


public class ReadWriteLockedStriped
{
	private transient volatile Striped<ReadWriteLock> stripes;

	public ReadWriteLockedStriped()
	{
		super();
	}
	
	private Striped<ReadWriteLock> stripes()
	{
		/*
		 * Double-checked locking to reduce the overhead of acquiring a lock
		 * by testing the locking criterion.
		 * The field (this.stripes) has to be volatile.
		 */
		Striped<ReadWriteLock> stripes = this.stripes;
		if(stripes == null)
		{
			synchronized(this)
			{
				if((stripes = this.stripes) == null)
				{
					stripes = this.stripes = Striped.lazyWeakReadWriteLock(4);
				}
			}
		}
		return stripes;
	}

	public <T> T read(
		final Object key,
		final ValueOperation<T> operation
		)
	{
		final Lock readLock = this.stripes().get(key).readLock();
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

	public void read(
		final Object key,
		final VoidOperation operation
	)
	{
		final Lock readLock = this.stripes().get(key).readLock();
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

	public <T> T write(
		final Object key,
		final ValueOperation<T> operation
	)
	{
		final Lock writeLock = this.stripes().get(key).writeLock();
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

	public void write(
		final Object key,
		final VoidOperation operation
	)
	{
		final Lock writeLock = this.stripes().get(key).writeLock();
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
