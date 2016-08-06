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
		// ������ ��� �ڸ����� � ��θ� ���� ����Ǵ���
		// Root���� ������ ���߿�, Final�� ���� �����
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

		// ������ ��� �ڸ��� newNode�� ����
		// �׸��� newNode�� ��� ������ FRTF�� ����
		// FRTF == From Root To Final
		// Root�� ����, Final�� ���߿� �����.
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

			// �����ִ� ��� �ٷ� ���� ���
			BT switchNode = FRTF.get(index);

			// �����ִ� ����� parent�� �������� ���ʿ� �ִ���
			boolean isLeftOfIndex = isLeft.get(FRTF.size() - index - 1);

			// priority�� parent�� �װͺ��� ũ�� �ٲ�
			if (switchNode.getPriority() < priority) {

				// parent�� root�� �ƴϸ�
				// parent�� parent�� ���� ��(*)���� �ٽ� ���δ�.
				if (index > 0) {
					BT parentOfSwitch = FRTF.get(index - 1);
					boolean isLeftOfIndexm1 = isLeft.get(FRTF.size() - index);

					if (isLeftOfIndexm1) {
						parentOfSwitch.setLeftChild(null);
					} else {
						parentOfSwitch.setRightChild(null);
					}
				}

				// �����ִ� ��尡 parent�� �������� ���ʿ� ������
				if (isLeftOfIndex) {
					BT rightOfSwitch = switchNode.getRightChild();
					BT targetNode = switchNode.getLeftChild();

					switchNode.setLeftChild(targetNode.getLeftChild());
					switchNode.setRightChild(targetNode.getRightChild());

					targetNode.setLeftChild(switchNode);
					targetNode.setRightChild(rightOfSwitch);

					// parent�� parent�� �ٽ� ���̴� ����(*)
					if (index > 0) {
						BT parentOfSwitch = FRTF.get(index - 1);
						boolean isLeftOfIndexm1 = isLeft.get(FRTF.size()
								- index);

						if (isLeftOfIndexm1) {
							parentOfSwitch.setLeftChild(targetNode);
						} else {
							parentOfSwitch.setRightChild(targetNode);
						}
						// parent�� root���ٸ� target�� root�� ����
					} else {
						root = targetNode;
					}

					// �����ִ� ��尡 parent�� �������� �����ʿ� ������
				} else {
					BT leftOfSwitch = switchNode.getLeftChild();
					BT targetNode = switchNode.getRightChild();

					switchNode.setLeftChild(targetNode.getLeftChild());
					switchNode.setRightChild(targetNode.getRightChild());

					targetNode.setLeftChild(leftOfSwitch);
					targetNode.setRightChild(switchNode);

					// parent�� parent�� �ٽ� ���̴� ����(*)
					if (index > 0) {
						BT parentOfSwitch = FRTF.get(index - 1);
						boolean isLeftOfIndexm1 = isLeft.get(FRTF.size()
								- index);

						if (isLeftOfIndexm1) {
							parentOfSwitch.setLeftChild(targetNode);
						} else {
							parentOfSwitch.setRightChild(targetNode);
						}
						// parent�� root���ٸ� target�� root�� ����
					} else {
						root = targetNode;
					}
				}

				// priority�� parent�� �װͺ��� ������ break
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
		// root���� finalNode������ ��θ� ã�´�
		// Root���� ������ ���߿�, Final�� ���� �����
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

		// finalNode�� ã�´�
		// finalNode�� �ڸ��� null�� �����
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

		// finalNode�� root�� ����
		root.setValue(finalNode.getValue());
		root.setPriority(finalNode.getPriority());

		/* downheap */
		BT targetNode = root;
		BT leftOfTarget = targetNode.getLeftChild();
		BT rightOfTarget = targetNode.getRightChild();
		BT parentOfTarget = null;

		int targetPriority = targetNode.getPriority();

		// left ������ no action(left ������ right�� ����)
		// case1. left�� ������ left�� ��
		// case2. left, right �� �� ������ max(left, right)�� ��
		while (leftOfTarget != null) {

			// target�� parent�� left���� right���� �����Ѵ�
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

					// parent�� �翬���Ų��
					// parent�� ���ٸ� root�� �缳���Ѵ�.
					if (hasParent) {
						if (isLeftParent) {
							parentOfTarget.setLeftChild(leftOfTarget);
						} else {
							parentOfTarget.setRightChild(leftOfTarget);
						}
					} else {
						root = leftOfTarget;
					}

					// parent�� �缳���Ѵ�
					parentOfTarget = leftOfTarget;

				} else {
					return rootValue;
				}

				// case2
			} else {
				int leftPriority = leftOfTarget.getPriority();
				int rightPriority = rightOfTarget.getPriority();

				// left�� right�� ���� ���ؼ�
				// ū priority�� ���� node�� ���Ѵ�
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

					// parent�� �翬���Ų��
					// parent�� ���ٸ� root�� �缳���Ѵ�.
					if (hasParent) {
						if (isLeftParent) {
							parentOfTarget.setLeftChild(switchNode);
						} else {
							parentOfTarget.setRightChild(switchNode);
						}
					} else {
						root = switchNode;
					}

					// parent�� �缳���Ѵ�
					parentOfTarget = switchNode;

				} else {
					return rootValue;
				}
			}
			// �缳���Ѵ�
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
