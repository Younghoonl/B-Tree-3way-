package org.dfpl.db.hash.m20011731;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<Integer> implements Set<Integer> {
	private MyThreeWayBTree[] hashTable;   //hashTable 3개

	public MyHashSet() {   //hashTable 초기화
		hashTable = new MyThreeWayBTree[3];
		hashTable[0] = new MyThreeWayBTree();
		hashTable[1] = new MyThreeWayBTree();
		hashTable[2] = new MyThreeWayBTree();
	}

	public void print(int n){
		hashTable[n].printTree(hashTable[n].getRoot(), 0);
	}

	@Override
	public int size() {     //해시테이블 전체 사이즈 반환 메소드
		int size = 0;
		for (MyThreeWayBTree t : hashTable) {
			size += t.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {      //헤시테이블이 비었는지 아닌지 반환
		if(this.size() == 0) return true;

		return false;
	}

	@Override
	public boolean contains(Object o) {     //특정 숫자가 있는지 알려주는 메소드

		return hashTable[(int) o % 3].contains(o);  //특정 숫자를 3으로 나눈 나머지의 인덱스의 해시테이블에 특정 숫자가 있는지 검사
	}

	class HashIterator<Integer> implements Iterator<Integer>{   //해시 테이블 3개를 Iterate하는 클래스

		Iterator<Integer> curIt;        //현재 노드 Iterator
		int  curInd;            //현재 Iterate 중인 HashTable 배열 인덱스
		Integer val;
		public HashIterator(){  //Iterator 초기화
			curInd = 0;
			curIt = (Iterator<Integer>) hashTable[curInd].iterator();
		}

		@Override
		public boolean hasNext() {  //다음 가르킬 숫자가 있는지
			while(curInd < 2 && !curIt.hasNext()){      //0, 1번째 hashTable에서 가장 큰 숫자를 가르킬때
				curIt = (Iterator<Integer>) hashTable[++curInd].iterator(); //다음 hashTable의 가장 작은 숫자를 Iterate
			}
			return curIt.hasNext();     //다음 Iterate 할 숫자가 있는지 확인
		}

		@Override
		public Integer next() {     //curIt의 Next() 호출
			val = (Integer) curIt.next();
			return val;
		}

		public void remove(){
			curIt.remove();
		}
	}

	@Override
	public Iterator<Integer> iterator() {       //HashIterator 클래스 생성 후 반환
		return new HashIterator();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Integer e) {     //특정 숫자를 3으로 나눈 나머지의 인덱스 hashTable에서 add 호출

		hashTable[(int)e % 3].add((int)e);
		return true;
	}

	@Override
	public boolean remove(Object o) {      //특정 숫자를 3으로 나눈 나머지의 인덱스 hashTable에서 remove 호출
		hashTable[(int) o % 3].remove((int) o);
		return true;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}


