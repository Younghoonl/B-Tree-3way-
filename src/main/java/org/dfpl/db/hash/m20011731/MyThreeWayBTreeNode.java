package org.dfpl.db.hash.m20011731;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class MyThreeWayBTreeNode {      //hashTable을 구성하는 노드 클래스

	final int m = 3;        //3way BTree
	final int max_children = m; //최대 자식 수
	final int max_keys = m - 1; //최대 키 수
	final int min_keys = (int) (ceil(m/2) -1); //최소 키 수
	private MyThreeWayBTreeNode parent;     //해당 노드의 부모 노드
	private List<Integer> keyList;      //키 리스트
	private List<MyThreeWayBTreeNode> children;     //해당 노드의 자식 노드 리스트
	boolean isLeaf;     //해당 노드가 리프노드인지

	public MyThreeWayBTreeNode(){   //노드 초기화
		keyList = new ArrayList<Integer>();
		children = new ArrayList<MyThreeWayBTreeNode>();
		isLeaf = true;
		parent = null;
	}

	public MyThreeWayBTreeNode(int val){        //노드 초기화
		keyList = new ArrayList<Integer>();
		children = new ArrayList<MyThreeWayBTreeNode>();
		isLeaf = false;
		parent = null;
		keyList.add(val);
	}

	public MyThreeWayBTreeNode getParent(){     //현재 노드의 부모 노드 반환
		return parent;
	}

	public List<MyThreeWayBTreeNode> getChildren(){     //현재 노드의 자식노드 리스트 반환
		return children;
	}

	public List<Integer> getKeyList(){      //현재 노드이 키 리스트 반환
		return keyList;
	}

	public void setParent(MyThreeWayBTreeNode parent){      //부모 저장
		this.parent = parent;
	}

	public void setKeyList(List<Integer> keyList){
		this.keyList = keyList;
	}   //현재 노드의 키 리스트 저장

	public void setChildren(List<MyThreeWayBTreeNode> children){
		this.children = children;
	}   //현재 노드의 자식 리스트 저장

	public ArrayList<Integer> InOrderTraversal()  //재귀를 사용해서 btree에 있는 모든 값들을 리스트에 넣고 반화
	{
		if (isLeaf)             //리프노드일 경우 노드의 키리스트 반환
		{
			return (ArrayList<Integer>) keyList;
		}
		ArrayList<Integer> inorderList = new ArrayList<>();
		for (int i = 0; i < keyList.size(); i++){           //자기노드의 값과 자식 노드의 키리스트 값 번갈아가며 순서대로 넣기
			inorderList.addAll(children.get(i).InOrderTraversal());
			inorderList.add(keyList.get(i));
		}
		inorderList.addAll(children.get(keyList.size()).InOrderTraversal());  //재귀 사용
		return inorderList;
	}

}



