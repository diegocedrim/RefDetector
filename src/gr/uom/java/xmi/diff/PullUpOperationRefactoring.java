package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLOperation;

public class PullUpOperationRefactoring extends MoveOperationRefactoring {

	private static final long serialVersionUID = 6153900252842878452L;

	public PullUpOperationRefactoring(UMLOperation originalOperation, UMLOperation movedOperation) {
		super(originalOperation, movedOperation);
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.PULL_UP_OPERATION;
	}
	
}
