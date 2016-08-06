import java.util.LinkedList;
import java.util.Random;

public class PriorityQueue {

	BT root;

	public PriorityQueue() {
		root = null;
	}

	public int size() {
		// Use 'getTreeSize()' to count nodes in the 'root'.
		return PriorityQueue.getTreeSize(this.root);
	}

	// Custom static method for counting nodes under the BT instance including
	// itself.
	private static int getTreeSize(BT root) {

		// Recursively call itself.

		if (root == null) {
			return 0;
		}

		int tempSize = 0;

		if (root.getLeftChild() != null) {
			tempSize += getTreeSize(root.getLeftChild());
		}
		if (root.getRightChild() != null) {
			tempSize += getTreeSize(root.getRightChild());
		}
		tempSize++;
		return tempSize;

	}

	public void add(int priority, String name) {

		BT newNode = new BT(priority, name);
		BT tempNode = root;
		int size = this.size();

		if (size == 0) {
			root = newNode;
			return;
		}
		// retroanalysis
		// 마지막 노드 자리까지 어떤 경로를 통해 연결되는지
		// Root와의 연결이 나중에, Final이 먼저 저장됨
		LinkedList<Boolean> isLeft = new LinkedList();
		while (size != 0) {
			if (size % 2 == 0) {
				isLeft.add(false);
				size = (size - 2) / 2;
			} else {
				isLeft.add(true);
				size = (size - 1) / 2;
			}
		}

		// 마지막 노드 자리에 newNode를 붙임
		// 그리고 newNode의 모든 조상을 FRTF에 저장
		// FRTF == From Root To Final
		// Root가 먼저, Final이 나중에 저장됨.
		LinkedList<BT> FRTF = new LinkedList<BT>();
		for (int index = isLeft.size() - 1; index >= 0; index--) {
			FRTF.add(tempNode);

			if (isLeft.get(index)) {
				if (index == 0) {
					tempNode.setLeftChild(newNode);
				} else {
					tempNode = tempNode.getLeftChild();
				}
			} else {
				if (index == 0) {
					tempNode.setRightChild(newNode);
				} else {
					tempNode = tempNode.getRightChild();
				}
			}
		}

		/* Upheap */
		for (int index = FRTF.size() - 1; index >= 0; index--) {

			// 관심있는 노드 바로 위의 노드
			BT switchNode = FRTF.get(index);

			// 관심있는 노드의 parent를 기준으로 왼쪽에 있는지
			boolean isLeftOfIndex = isLeft.get(FRTF.size() - index - 1);

			// priority가 parent의 그것보다 크면 바꿈
			if (switchNode.getPriority() < priority) {

				// parent가 root가 아니면
				// parent의 parent를 끊고 밑(*)에서 다시 붙인다.
				if (index > 0) {
					BT parentOfSwitch = FRTF.get(index - 1);
					boolean isLeftOfIndexm1 = isLeft.get(FRTF.size() - index);

					if (isLeftOfIndexm1) {
						parentOfSwitch.setLeftChild(null);
					} else {
						parentOfSwitch.setRightChild(null);
					}
				}

				// 관심있는 노드가 parent를 기준으로 왼쪽에 있으면
				if (isLeftOfIndex) {
					BT rightOfSwitch = switchNode.getRightChild();
					BT targetNode = switchNode.getLeftChild();

					switchNode.setLeftChild(targetNode.getLeftChild());
					switchNode.setRightChild(targetNode.getRightChild());

					targetNode.setLeftChild(switchNode);
					targetNode.setRightChild(rightOfSwitch);

					// parent의 parent를 다시 붙이는 과정(*)
					if (index > 0) {
						BT parentOfSwitch = FRTF.get(index - 1);
						boolean isLeftOfIndexm1 = isLeft.get(FRTF.size()
								- index);

						if (isLeftOfIndexm1) {
							parentOfSwitch.setLeftChild(targetNode);
						} else {
							parentOfSwitch.setRightChild(targetNode);
						}
						// parent가 root였다면 target을 root로 설정
					} else {
						root = targetNode;
					}

					// 관심있는 노드가 parent를 기준으로 오른쪽에 있으면
				} else {
					BT leftOfSwitch = switchNode.getLeftChild();
					BT targetNode = switchNode.getRightChild();

					switchNode.setLeftChild(targetNode.getLeftChild());
					switchNode.setRightChild(targetNode.getRightChild());

					targetNode.setLeftChild(leftOfSwitch);
					targetNode.setRightChild(switchNode);

					// parent의 parent를 다시 붙이는 과정(*)
					if (index > 0) {
						BT parentOfSwitch = FRTF.get(index - 1);
						boolean isLeftOfIndexm1 = isLeft.get(FRTF.size()
								- index);

						if (isLeftOfIndexm1) {
							parentOfSwitch.setLeftChild(targetNode);
						} else {
							parentOfSwitch.setRightChild(targetNode);
						}
						// parent가 root였다면 target을 root로 설정
					} else {
						root = targetNode;
					}
				}

				// priority가 parent의 그것보다 작으면 break
			} else {
				break;
			}

		}

	}

	public String remove() {
		// remove the root and return its value

		int size = this.size();
		String rootValue = root.getValue();

		// If the root was the only node, simply remove.
		if (size == 1) {
			root = null;
			return rootValue;
		}

		// If not, locate the last node and get it to root position.

		// retroanalysis
		// root에서 finalNode까지의 경로를 찾는다
		// Root와의 연결이 나중에, Final이 먼저 저장됨
		size--; // index = size -1
		LinkedList<Boolean> isLeft = new LinkedList();
		while (size != 0) {
			if (size % 2 == 0) {
				isLeft.add(false);
				size = (size - 2) / 2;
			} else {
				isLeft.add(true);
				size = (size - 1) / 2;
			}
		}

		// finalNode를 찾는다
		// finalNode의 자리를 null로 만든다
		BT finalNode = root;

		for (int index = isLeft.size() - 1; index >= 0; index--) {
			if (isLeft.get(index)) {
				if (index == 0) {
					BT tempNode = finalNode.getLeftChild();
					finalNode.setLeftChild(null);
					finalNode = tempNode;

				} else {
					finalNode = finalNode.getLeftChild();
				}
			} else {
				if (index == 0) {
					BT tempNode = finalNode.getRightChild();
					finalNode.setRightChild(null);
					finalNode = tempNode;
				} else {
					finalNode = finalNode.getRightChild();
				}
			}
		}

		// finalNode를 root에 저장
		root.setValue(finalNode.getValue());
		root.setPriority(finalNode.getPriority());

		/* downheap */
		BT targetNode = root;
		BT leftOfTarget = targetNode.getLeftChild();
		BT rightOfTarget = targetNode.getRightChild();
		BT parentOfTarget = null;

		int targetPriority = targetNode.getPriority();

		// left 없으면 no action(left 없으면 right도 없음)
		// case1. left만 있으면 left와 비교
		// case2. left, right 둘 다 있으면 max(left, right)와 비교
		while (leftOfTarget != null) {

			// target이 parent의 left인지 right인지 저장한다
			boolean isLeftParent = false;
			boolean hasParent = false;
			if (parentOfTarget != null) {
				hasParent = true;
				if (parentOfTarget.getLeftChild().equals(targetNode)) {
					isLeftParent = true;
				} else {
					isLeftParent = false;
				}
			}

			// case1
			if (rightOfTarget == null) {

				int leftPriority = leftOfTarget.getPriority();
				if (leftPriority > targetPriority) {

					// swap(target, left)
					leftOfTarget.setLeftChild(targetNode);
					targetNode.setLeftChild(null);

					// parent를 재연결시킨다
					// parent가 없다면 root를 재설정한다.
					if (hasParent) {
						if (isLeftParent) {
							parentOfTarget.setLeftChild(leftOfTarget);
						} else {
							parentOfTarget.setRightChild(leftOfTarget);
						}
					} else {
						root = leftOfTarget;
					}

					// parent를 재설정한다
					parentOfTarget = leftOfTarget;

				} else {
					return rootValue;
				}

				// case2
			} else {
				int leftPriority = leftOfTarget.getPriority();
				int rightPriority = rightOfTarget.getPriority();

				// left와 right를 서로 비교해서
				// 큰 priority를 가진 node와 비교한다
				int switchPriority = 0;
				boolean isSwitchLeft = false;
				BT switchNode = null;
				BT otherNode = null;

				if (leftPriority > rightPriority) {
					switchPriority = leftPriority;
					switchNode = leftOfTarget;
					otherNode = rightOfTarget;
					isSwitchLeft = true;
				} else {
					switchPriority = rightPriority;
					switchNode = rightOfTarget;
					otherNode = leftOfTarget;
					isSwitchLeft = false;
				}

				if (switchPriority > targetPriority) {

					// swap(switchNode, targetNode)
					targetNode.setLeftChild(switchNode.getLeftChild());
					targetNode.setRightChild(switchNode.getRightChild());
					if (isSwitchLeft) {
						switchNode.setLeftChild(targetNode);
						switchNode.setRightChild(otherNode);
					} else {
						switchNode.setLeftChild(otherNode);
						switchNode.setRightChild(targetNode);
					}

					// parent를 재연결시킨다
					// parent가 없다면 root를 재설정한다.
					if (hasParent) {
						if (isLeftParent) {
							parentOfTarget.setLeftChild(switchNode);
						} else {
							parentOfTarget.setRightChild(switchNode);
						}
					} else {
						root = switchNode;
					}

					// parent를 재설정한다
					parentOfTarget = switchNode;

				} else {
					return rootValue;
				}
			}
			// 재설정한다
			leftOfTarget = targetNode.getLeftChild();
			rightOfTarget = targetNode.getRightChild();
		}

		return rootValue;
	}

	public String getHighestPriorityValue() {

		return root.getValue();
	}

	public static boolean isEmpty(BT root) {
		if (root == null) {
			return true;
		} else {
			return false;
		}

	}

	public static BT merge(BT root1, BT root2) {
		// return the root of new merged queue

		if(root1 == null){
			return root2;
		}
		if(root2 == null){
			return root1;
		}
		
		BT returnBT = null;
		int priorityOfRoot1 = root1.getPriority();
		int priorityOfRoot2 = root2.getPriority();
		
		if(priorityOfRoot1 > priorityOfRoot2){
			returnBT = merge(root2, root1.getRightChild());
			root1.setRightChild(returnBT);
			return root1;
		} else{
			returnBT = merge(root1, root2.getRightChild());
			root2.setRightChild(returnBT);
			return root2;
		}


	}

}
