package com.michielvanderlee.tools.tunnels;

public interface CacheUpdateListener<T>
{
	public void addedToCache( T object );
	
	public void removedFromCache( T object  );
}
