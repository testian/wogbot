package utils;
import java.util.*;
public class RingList<T> implements Iterable<T> {

	Object[] array;
int position;
int covered;
long putCount;
	public RingList(int size)
{
array = (T[])new Object[size];
position=0;
covered=0;
putCount=0;
}
public int size()
{
return covered;
}

public long putCount()
{
return putCount;
}

public void put(T put)
{
array[position]=put;

position++;
if (position>covered){covered=position;}
position=position%array.length;
putCount++;
}
public Iterator<T> iterator()
{
return new RingIterator<T>();
}
private class RingIterator<E> implements Iterator<E>
{

int start;
int len;
int count;
RingIterator()
{
count=0;
if (covered<array.length)
{
start=0;

}
else {
	start=position;
}
len=covered;
}
public boolean hasNext()
{
return (count<covered);
}
public E next()
{
return (E)array[(start+(count++))%array.length];

}
public void remove()
{}
}
	
}
