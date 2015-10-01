package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLOperation;

public class PushDownOperationRefactoring extends MoveOperationRefactoring {

	private static final long serialVersionUID = -3297564303025175107L;

	public PushDownOperationRefactoring(UMLOperation originalOperation, UMLOperation movedOperation) {
		super(originalOperation, movedOperation);
		// TODO Auto-generated constructor stub
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.PUSH_DOWN_OPERATION;
	}
}
