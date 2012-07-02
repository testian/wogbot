package utils;

import java.util.*;

public class LinkedListDecorator <T> implements List<T> {

LinkedList<T> decorate;
public LinkedListDecorator(LinkedList<T> decorate)
{
this.decorate=decorate;
}
public void add(int arg0, T arg1) {
	decorate.add(arg0, arg1);
}
public boolean add(T arg0) {
	return decorate.add(arg0);
}
public boolean addAll(Collection<? extends T> arg0) {
	return decorate.addAll(arg0);
}
public boolean addAll(int arg0, Collection<? extends T> arg1) {
	return decorate.addAll(arg0, arg1);
}
public void clear() {
	decorate.clear();
}
public boolean contains(Object arg0) {
	return decorate.contains(arg0);
}
public boolean containsAll(Collection<?> arg0) {
	return decorate.containsAll(arg0);
}
public boolean equals(Object arg0) {
	return decorate.equals(arg0);
}
public T get(int arg0) {
	return decorate.get(arg0);
}
public int hashCode() {
	return decorate.hashCode();
}
public int indexOf(Object arg0) {
	return decorate.indexOf(arg0);
}
public boolean isEmpty() {
	return decorate.isEmpty();
}
public Iterator<T> iterator() {
	return decorate.iterator();
}
public int lastIndexOf(Object arg0) {
	return decorate.lastIndexOf(arg0);
}
public ListIterator<T> listIterator() {
	return decorate.listIterator();
}
public ListIterator<T> listIterator(int arg0) {
	return decorate.listIterator(arg0);
}
public T remove(int arg0) {
	return decorate.remove(arg0);
}
public boolean remove(Object arg0) {
	return decorate.remove(arg0);
}
public boolean removeAll(Collection<?> arg0) {
	return decorate.removeAll(arg0);
}
public boolean retainAll(Collection<?> arg0) {
	return decorate.retainAll(arg0);
}
public T set(int arg0, T arg1) {
	return decorate.set(arg0, arg1);
}
public int size() {
	return decorate.size();
}
public List<T> subList(int arg0, int arg1) {
	return decorate.subList(arg0, arg1);
}
public Object[] toArray() {
	return decorate.toArray();
}
public <T> T[] toArray(T[] arg0) {
	return decorate.toArray(arg0);
}
public List<T> clone()
{
	List<T> copy = new LinkedList<T>(); 

	for (Iterator<T> i = decorate.iterator() ;i.hasNext();)
	{
		copy.add(i.next());
	}
	return copy;
}




}
