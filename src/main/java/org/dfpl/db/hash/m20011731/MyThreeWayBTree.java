package org.dfpl.db.hash.m20011731;

import java.util.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
//import java.util.NavigableSet;
import java.util.SortedSet;

import static java.lang.Math.*;

public class MyThreeWayBTree implements NavigableSet<Integer> {     //BTree 클래스

	final int m = 3;    //3way BTree
	final int max_children = m; //최대 자식 수
	final int max_keys = m - 1;     //최대 키 수
	final int min_keys = (int) (ceil((double) m / 2)) - 1; //최소 키 수

	private MyThreeWayBTreeNode root;      //BTree의 root노드

	int curIdx;
	int size;                   //비트리 노드 개수

	MyThreeWayBTree(){      //bTree 초기화
		root = null;
		size = 0;

	}

	public void printTree(MyThreeWayBTreeNode node , int level) {
		if (node == null) {
			System.out.println("Empty");
		} else {
			System.out.printf("Level %d ", level);
			for (int i = 0; i < node.getKeyList().size(); i++) {
				System.out.printf("|%d|", node.getKeyList().get(i));
			}
			System.out.printf("\n");
			level++;
			for (int i = 0; i < node.getChildren().size(); i++) {
				printTree(node.getChildren().get(i), level);
			}
		}
	}

	public boolean isExist(int val){    //특정 숫자가 트리에 존재하는지
		MyThreeWayBTreeNode node = searchNode(val);     //searchNode()를 호출하여 특정 숫자가 있는 노드 받기
		if(node != null){
			for(int i = 0; i < node.getChildren().size(); i++){  //특정 숫자가 있다면 true반환
				if(val == node.getKeyList().get(i)){
					return true;
				}
			}
		}
		return false;   //찾는 숫자가 없다면 false 반환
	}

	public MyThreeWayBTreeNode searchNode(int val){     //특정 숫자를 찾는 메소드

		MyThreeWayBTreeNode cur = root;     //root부터 내려오면서 찾음
		while(true){
			int i;
			for(i =0; i < cur.getKeyList().size(); i++){
				if(val == cur.getKeyList().get(i)){     //현재 노드에서 val을 찾으면 현재 노드 반환
					return cur;
				}
				else if(val < cur.getKeyList().get(i)){     //현재 노드에서 val보다 큰 수가 나올때 멈춤 -> 자식으로 내려가기 위해
					break;
				}
			}
			if(cur.isLeaf){     //현재 노드가 리프 노드면 찾는 숫자가 없는거
				break;
			}
			else{       //리프가 아니라면 자식으로 내려가기
				cur = cur.getChildren().get(i);
			}
		}
		return null;
	}

	public MyThreeWayBTreeNode createNode(int val){     //새로운 노드를 생성하는 메소드
		MyThreeWayBTreeNode newNode = new MyThreeWayBTreeNode();
		newNode.isLeaf = false;
		newNode.getKeyList().add(val);      //노드에 숫자를 추가한 후 새로 만든 노드 반환
		return newNode;
	}

	//해당 노드의 max_key규칙을 어기면 노드를 분리해서 가운데 숫자 부모로 올리는 메소드
	public MyThreeWayBTreeNode splitNode(int pos, MyThreeWayBTreeNode node, MyThreeWayBTreeNode parent){
		int middle = node.getKeyList().size() / 2;      //가운데 숫자
		MyThreeWayBTreeNode newNode = new MyThreeWayBTreeNode();    //새로운 노드 생성
		newNode.isLeaf = node.isLeaf;

		for(int i = middle + 1;  i < node.getKeyList().size(); i++){    //현재 노드의 오른쪽 절반을 새로운 노드에 추가
			newNode.getKeyList().add(node.getKeyList().get(i));
			node.getKeyList().remove(i);        //현재노드에서 옮긴값들 제거
			i--;
		}

		if(!node.isLeaf){       //노드가 리프가 아니라 중간 노드라면 분리할 노드에 자식 담기
			for(int i = middle + 1; i < node.getChildren().size(); i++){
				newNode.getChildren().add(node.getChildren().get(i));
				node.getChildren().get(i).setParent(newNode);
				node.getChildren().remove(i);
				i--;
			}
		}

		if(node == root){       //노드가 루트노드일때 새로운 부모를 생성, 처리
			MyThreeWayBTreeNode newParent = createNode(node.getKeyList().get(middle));
			node.getKeyList().remove(middle);
			newParent.getChildren().add(node);
			node.setParent(newParent);
			newParent.getChildren().add(newNode);
			newNode.setParent(newParent);
			return newParent;
		}
		else{               //노드가 root가 아니라면 기존 부모를 이용하여 split
			int size = parent.getKeyList().size();
			for(int i = size;  i > pos; i--){      //부모노드에 값추가를 위해 오른쪽으로 한칸씩 밀기
				parent.getKeyList().add(i, parent.getKeyList().get(i -1));
				parent.getChildren().add(i + 1, parent.getChildren().get(i));
				parent.getChildren().remove(i);
				parent.getKeyList().remove(i -1);
			}
			parent.getKeyList().add(pos, node.getKeyList().get(middle));        //부모노드에 값 추가
			node.getKeyList().remove(middle);
			parent.getChildren().add(pos + 1, newNode);
			newNode.setParent(parent);
		}
		return node;
	}

	//bTree 노드에 값을 추가하는 메소드
	public MyThreeWayBTreeNode insertNode(int parent_pos, int val, MyThreeWayBTreeNode node, MyThreeWayBTreeNode parent){
		int pos;
		for(pos = 0; pos < node.getKeyList().size(); pos++){        //해당 키 리스트에서 val보다 높은 값 위치 찾기
			if(val == node.getKeyList().get(pos)){          //중복된 값이 추가되었을때
				size--;
				return node;
			}
			else if(val < node.getKeyList().get(pos)){      //val보다 높은 값을 만났을때 정지
				break;
			}
		} //for문이 정상 종료시 해당 노드에서 val이 가장 큼

		if(!node.isLeaf){           //리프가 아니라면 오른쪽 자식으로 이동
			node.getChildren().set(pos, insertNode(pos, val, node.getChildren().get(pos), node));
			if(node.getKeyList().size() == max_keys +1){    //최대 키 규칙 위반시 split하기
				node = splitNode(parent_pos, node, parent);
			}
		}
		else{       //리프라면 값 추가
			node.getKeyList().add(pos, val);
			if(node.getKeyList().size() == max_keys + 1){       //최대 키 규칙 위반시 split
				node = splitNode(parent_pos, node, parent);
			}
		}
		return node;        //재귀를 위해
	}


	public Integer getPLV(MyThreeWayBTreeNode node){        //PLV를 찾는 메소드 -> merge에서 사용
		int pIdx = node.getParent().getChildren().indexOf(node) -1;
		if(pIdx < 0){
			return null;
		}
		return node.getParent().getKeyList().get(pIdx);
	}

	public MyThreeWayBTreeNode getLS(MyThreeWayBTreeNode node){       //LS(왼쪽 형제)를 찾는 메소드
		if(getPLV(node) == null){
			return null;
		}
		int pIdx = node.getParent().getChildren().indexOf(node) -1;
		return node.getParent().getChildren().get(pIdx);
	}

	public Integer getLV(MyThreeWayBTreeNode node){     //LV(왼쪽 형제에서 가장 큰 값) 을 찾는 메소드
		if(getLS(node) == null){
			return null;
		}
		int pIdx = node.getParent().getChildren().indexOf(node) -1;
		int size = node.getParent().getChildren().get(pIdx).getKeyList().size();
		return node.getParent().getChildren().get(pIdx).getKeyList().get(size -1);
	}

	public Integer getPRV(MyThreeWayBTreeNode node){        //PRV를 찾는 메소드 -> merge에서 사용
		int pIdx = node.getParent().getChildren().indexOf(node);
		if(pIdx == node.getParent().getKeyList().size()){
			return null;
		}
		return node.getParent().getKeyList().get(pIdx);
	}

	public MyThreeWayBTreeNode getRS(MyThreeWayBTreeNode node){      //RS(오른쪽 형제)를 찾는 메소드
		if(getPRV(node) == null){
			return null;
		}
		int pIdx = node.getParent().getChildren().indexOf(node) + 1;
		return node.getParent().getChildren().get(pIdx);
	}

	public Integer getRV(MyThreeWayBTreeNode node){         //RV(오른쪽 형제에서 가장 작은 값)을 찾는 메소드
		if(getRS(node) == null){
			return null;
		}
		int pIdx = node.getParent().getChildren().indexOf(node) + 1;
		return node.getParent().getChildren().get(pIdx).getKeyList().get(0);
	}

	public MyThreeWayBTreeNode getLC(MyThreeWayBTreeNode node, int idx){    //현재 노드의 특정 인덱스에서 오른쪽 자식 노드를 찾는 메소드
		MyThreeWayBTreeNode t = node.getChildren().get(idx);
		while(!t.isLeaf){
			t = t.getChildren().get(t.getChildren().size() -1);
		}
		return t;
	}

	public MyThreeWayBTreeNode getRC(MyThreeWayBTreeNode node, int idx){        //현재 노드의 특정 인덱스에서 왼쪽 자식 노드를 찾는 메소드
		MyThreeWayBTreeNode t = node.getChildren().get(idx + 1);
		while(!t.isLeaf){
			t = t.getChildren().get(0);
		}
		return t;
	}



	public MyThreeWayBTreeNode getRoot(){       //루트 반환
		return root;
	}

	@Override
	public Comparator<? super Integer> comparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer first() {        //BTree에서 가장 작은 값을 찾는 메소드
		MyThreeWayBTreeNode t = root;
		while(!t.isLeaf){
			t = t.getChildren().get(0);
		}
		return t.getKeyList().get(0);
	}

	@Override
	public Integer last() {         //BTree에서 가장 큰 값을 찾는 메소드
		MyThreeWayBTreeNode t = root;
		while(!t.isLeaf){
			t = t.getChildren().get(t.getChildren().size() -1);
		}
		return t.getKeyList().get(t.getKeyList().size() -1);
	}

	@Override
	public int size() {     //현재 Btree의 전체 숫자 반환
		return size;
	}

	@Override
	public boolean isEmpty() {      //BTree가 비었는지 아닌지
		if(size == 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean contains(Object o) {     //특정 숫자가 BTree에 있는지 없는지 검색
		MyThreeWayBTreeNode node = searchNode((int) o);     //searchNode를 호출하여 값이 있는 노드 받기
		if(node != null){
			for(int i=0; i < node.getKeyList().size(); i++){
				if((int) o == node.getKeyList().get(i)){
					return true ;
				}
			}
		}
		return false;
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
	public boolean add(Integer e) {         //Btree에 값을 넣는 메소드
		size++;
		if(root == null){               //처음으로 추가된 수일때 root노드 생성
			root = createNode(e);
			root.isLeaf = true;
		}
		else{                       //InsertNode를 호출해서 root부터 추가할 위치를 검색해서 추가
			root = insertNode(0, e, root, root);
		}
		return true;
	}

	public void mergeNode(MyThreeWayBTreeNode node, int rpos, int lpos){        //T가 최소 키 규칙을 어겼을때 부모와 오른쪽자식을 왼쪽 자식으로 합침
		node.getChildren().get(lpos).getKeyList().add(node.getKeyList().get(lpos));     //현재 숫자를 왼쪽자식 노드 키에 추가
		node.getChildren().get(lpos).getKeyList().addAll(node.getChildren().get(rpos).getKeyList());    //오르쪽 자식 노드의 키값을 전부 왼쪽 자식 노드에 추가
		node.getChildren().get(lpos).getChildren().addAll(node.getChildren().get(rpos).getChildren());  //오른쪽 자식 노드의 자식 리스트를 왼쪽 자식 노드의 자식으로 모두 추가

		for(int i =0; i < node.getChildren().get(rpos).getChildren().size(); i++){              //부모가 누군지 바꾸기
			node.getChildren().get(rpos).getChildren().get(i).setParent(node.getChildren().get(lpos));
		}
		node.getChildren().remove(rpos);            //오른쪽 자식 노드 삭제
		node.getKeyList().remove(lpos);
	}

	public void borrowFromLeft(MyThreeWayBTreeNode node, int pos){          //T가 최소 키 규칙을 어겼을 때 부로한테 값을 받고 왼쪽 형제 LV 값을 부모로 올리기
		node.getChildren().get(pos).getKeyList().add(0, node.getKeyList().get(pos-1));  //T의 부모한테 값 받기
		int size = node.getChildren().get(pos-1).getKeyList().size();
		node.getKeyList().set(pos-1, node.getChildren().get(pos-1).getKeyList().get(size-1));   //왼쪽 자식 노드의 LV값을 node poe-1에 추가
		node.getChildren().get(pos-1).getKeyList().remove(size-1);

		if(node.getChildren().get(pos-1).getChildren().size() > 0){     //왼쪽 형제의 자식을 T의 자식에 추가
			size = node.getChildren().get(pos -1).getChildren().size();
			node.getChildren().get(pos).getChildren().add(0, node.getChildren().get(pos-1).getChildren().get(size-1));
			node.getChildren().get(pos-1).getChildren().get(size-1).setParent(node.getChildren().get(pos));
			node.getChildren().get(pos-1).getChildren().remove(size-1);
		}
	}

	public void borrowFromRight(MyThreeWayBTreeNode node, int pos){      //T가 최소 키 규칙을 어겼을 때 부로한테 값을 받고 오른쪽 형제 RV 값을 부모로 올리기
		node.getChildren().get(pos).getKeyList().add(node.getKeyList().get(pos));  //T의 부모한테 값 받기

		node.getKeyList().set(pos, node.getChildren().get(pos+1).getKeyList().get(0));      //T의 오른쪽 형제의 RV값을 부모한테 올리기
		node.getChildren().get(pos+1).getKeyList().remove(0);

		if(node.getChildren().get(pos+1).getChildren().size() > 0){     //오른쪽 형제의 RV값 자식을 T의 자식으로 추가
			node.getChildren().get(pos).getChildren().add(node.getChildren().get(pos+1).getChildren().get(0));
			node.getChildren().get(pos+1).getChildren().get(0).setParent(node.getChildren().get(pos));
			node.getChildren().get(pos+1).getChildren().remove(0);
		}

	}
	//삭제시 최소 키 규칙을 어길때 형제로부터 빌리거나 부모, 형제와 합치는 등 최소키 규칙 준수를 위해 조정
	public void balancing(MyThreeWayBTreeNode node, int pos){
		if(pos == 0){       //T가 가장 왼쪽 노드라면 오른쪽 형제한테 빌려오기
			if(node.getChildren().get(pos +1).getKeyList().size() > min_keys){
				borrowFromRight(node, pos);   //오른쪽 형제한테 빌려오기
			}
			else{     //오른쪽 형제가 최소키만 가지고 있어 빌릴 수 없다면
				mergeNode(node, pos + 1, pos);  //부모와 오른쪽 형제를 합치기
			}
			return;
		}
		else if(pos == node.getKeyList().size()){         //T가 가장 오른쪽 노드라면 왼쪽 형제한테 빌려오기
			if(node.getChildren().get(pos-1).getKeyList().size() > min_keys){  //왼쪽 형제한테 빌릴 수 있음
				borrowFromLeft(node, pos);
			}else{
				mergeNode(node, pos, pos-1);        //왼쪽 형제한테 빌릴 수 없으면 부모, 왼쪽 형제와 합치기
			}
			return;
		}
		else{       //T가 양 끝의 인덱스가 아닐때
			if(node.getChildren().get(pos -1).getKeyList().size() > min_keys){      //왼쪽 형제한테 빌리기
				borrowFromLeft(node, pos);
			}
			else if(node.getChildren().get(pos + 1).getKeyList().size() > min_keys){        //오른쪽 형제한테 빌리기
				borrowFromRight(node, pos);
			}
			else{
				mergeNode(node, pos, pos -1);       //형제한테 빌릴 수 없을때 합치기
			}
			return;
		}
	}

	//리프가 아닌 노드에서 삭제가 일어날때 리프의 LV로부터 값 받기
	public int findLV(MyThreeWayBTreeNode node){  //T의 자식들 중 가장 큰 값을 찾기 위한 메소드
		if(node.isLeaf){
			return node.getKeyList().get(node.getChildren().size() -1);
		}
		return findLV(node.getChildren().get(node.getChildren().size() -1));
	}

	//리프가 아닌 노드에서 삭제가 일어날때 리프의 RV로부터 값 받기
	public int findRV(MyThreeWayBTreeNode node){    //T의 자식들 중 가장 직은 값을 찾기 위한 메소드
		if(node.isLeaf){
			return node.getKeyList().get(0);
		}
		return findRV(node.getChildren().get(0));
	}

	//리프가 아닌 노드에서 삭제가 일어날때 왼쪽과 오른쪽 자식을 왼쪽 자식으로 합치는 메소드
	public void mergeChildNode(MyThreeWayBTreeNode node, int pos){
		int val = node.getKeyList().get(pos);    //바로 자식끼리 합치면 RV의 오른쪽 자식과 LV의 왼쪽 자식 중 하나가 사라짐
		node.getChildren().get(pos).getKeyList().add(val);      //일단 PV값을 복사해서 두 자식을 살린채 합친다
		node.getChildren().get(pos).getKeyList().addAll(node.getChildren().get(pos +1).getKeyList());
		node.getChildren().get(pos).getChildren().addAll(node.getChildren().get(pos + 1).getChildren());

		for(int i =0; i < node.getChildren().get(pos +1).getChildren().size(); i++){
			node.getChildren().get(pos+1).getChildren().get(i).setParent(node.getChildren().get(pos));
		}
		node.getChildren().remove((Object)node.getChildren().get(pos +1));  //오른쪽 자식 노드 삭제
		node.getKeyList().remove((Object) val);
		delVal(node.getChildren().get(pos), val);    //부모노드에서 내렸던 값을 지우기
	}

	//리프노드가 아닌 중간 노드에서 값 지우기
	public void delNotLeaf(MyThreeWayBTreeNode node, int pos){
		if(node.getChildren().get(pos).getKeyList().size() > min_keys){     //LV를 찾고 지운값을 LV값을 대체하기
			int LV = findLV(node.getChildren().get(pos));
			node.getKeyList().set(pos, LV);
			delVal(node.getChildren().get(pos), LV);        //LV 삭제
		}
		else if(node.getChildren().get(pos +1).getKeyList().size() > min_keys){  //RV를 찾고 지운값을 RV값으로 대체하기
			int RV = findRV(node.getChildren().get(pos+1));
			node.getKeyList().set(pos, RV);
			delVal(node.getChildren().get(pos +1), RV);   //RV 삭제
		}
		else{        //자식들 합치기
			mergeChildNode(node, pos);
		}
	}

	public boolean delVal(MyThreeWayBTreeNode node, int val){
		boolean flag = false;       //Val 값 존재 유무
		int pos;

		for(pos = 0; pos < node.getKeyList().size(); pos++){        //Val값 위치 탐색
			if(val == node.getKeyList().get(pos)){
				flag = true;        //Val 값 찾음
				break;
			}
			else if(val < node.getKeyList().get(pos)){      //자식으로 이동하기 위해 멈춤
				break;
			}
		}

		if(flag){       //val값 찾았으면
			if(node.isLeaf){        //리프노드면 val값 제거
				node.getKeyList().remove((Object) val);
				size--;
			}
			else{       //중간노드일때 제거
				delNotLeaf(node, pos);
			}
			return flag;
		}
		else{           //val값 못찾았으면
			if(node.isLeaf){        //val값 못찾음
				return flag;
			}
			else{           //현재 level에서 val값 못찾음 -> 자식으로 이동
				flag = delVal(node.getChildren().get(pos), val);
			}
		}
		if(node.getChildren().get(pos).getKeyList().size() < min_keys){     //최소 키 규칙 위반
			balancing(node, pos);       //최소 키 규칙 준수를 위한 조정
		}
		return flag;
	}

	public boolean delete(MyThreeWayBTreeNode node, int val){       //val값을 찾아 없애기
		if(node == null){
			return false;
		}
		if(delVal(node, val) == false){         //자식까지 내려갔는데 val값이 없으면
			return false;
		}
		if(node.getKeyList().size() == 0){      //노드가 비었다면
			node = node.getChildren().get(0);   //노드가 가진 왼쪽 자식을 대입
		}
		root = node;        //root 재설정
		return true;
	}

	@Override
	public boolean remove(Object o) {       //값 삭제
		return delete(root, (int) o);
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

	@Override
	public Integer lower(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer floor(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer ceiling(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer higher(Integer e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer pollFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer pollLast() {
		// TODO Auto-generated method stub
		return null;
	}

	class BtreeIterator implements Iterator<Integer>{           //BTree Iterator 클래스

		MyThreeWayBTreeNode curNode;        //현재 노드
		ArrayList<Integer> BTreeList;

		int curIdx;         //현재 iterate 중인 값 인덱스

		public BtreeIterator(){     //시작점
			curIdx  = 0;

			BTreeList = root.InOrderTraversal();
		}

		@Override
		public boolean hasNext() {      //다음 값 존재 유무

			try{
				int trial = BTreeList.get(curIdx);
				return true;
			}
			catch(IndexOutOfBoundsException e){
				return false;
			}
		}

//        public void movePointer(){      //Iterate 중인 노드 포인터
//            if(!curNode.isLeaf && curNode.getChildren().size() > curIdx) {      //자식으로 이동
//                curNode = curNode.getChildren().get(curIdx);
//                curIdx = 0;
//                if (!curNode.isLeaf) {      //리프가 아니면 계속 자식으로 이동
//                    movePointer();
//                }
//            }
//            else if(curNode.getKeyList().size() <= curIdx){     //현재 키 리스트의 마지막이므로 부모로 이동
//                if(curNode == root){        //현재 노드가 루트일때
//                    curNode = curNode.getParent();
//                    return;
//                }
//                else{           //부모로 이동
//                    curIdx = curNode.getParent().getChildren().indexOf((Object) curNode);  //현재 자식의 위치
//                    curNode = curNode.getParent();
//                    if(curNode.getKeyList().size() <= curIdx) { //다음 key로 이동
//                        curIdx++;
//                        movePointer();
//                    }
//                }
//            }
//        }

		@Override
		public Integer next() {     //다음 값으로 이동

			return BTreeList.get(curIdx++);
		}

		public void remove(){       //arrayList에서 값 삭제

			BTreeList.remove(curIdx);
		}
	}

	@Override
	public Iterator<Integer> iterator() {

		return new BtreeIterator();
	}

	@Override
	public NavigableSet<Integer> descendingSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Integer> descendingIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NavigableSet<Integer> subSet(Integer fromElement, boolean fromInclusive, Integer toElement,
										boolean toInclusive) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NavigableSet<Integer> headSet(Integer toElement, boolean inclusive) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NavigableSet<Integer> tailSet(Integer fromElement, boolean inclusive) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Integer> subSet(Integer fromElement, Integer toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Integer> headSet(Integer toElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedSet<Integer> tailSet(Integer fromElement) {
		// TODO Auto-generated method stub
		return null;
	}

}

