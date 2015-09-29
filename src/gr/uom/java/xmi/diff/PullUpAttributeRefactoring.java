package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;

public class PullUpAttributeRefactoring extends MoveAttributeRefactoring {

	private static final long serialVersionUID = 6111925278233063975L;

	public PullUpAttributeRefactoring(UMLAttribute movedAttribute,
			String sourceClassName, String targetClassName) {
		super(movedAttribute, sourceClassName, targetClassName);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(movedAttribute);
		sb.append(" from class ");
		sb.append(sourceClassName);
		sb.append(" to class ");
		sb.append(targetClassName);
		return sb.toString();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.PULL_UP_ATTRIBUTE;
	}
}
