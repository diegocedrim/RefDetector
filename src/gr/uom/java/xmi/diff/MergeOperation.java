package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.UMLOperationBodyMapper;

public class MergeOperation implements Refactoring {
	private static final long serialVersionUID = -8253229601851439314L;
	private UMLOperationBodyMapper mapper;
	
	public MergeOperation(UMLOperationBodyMapper mapper) {
		this.mapper = mapper;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(mapper.getOperation1());
		sb.append(" merged to ");
		sb.append(mapper.getOperation2());
		sb.append(" in class ");
		sb.append(mapper.getOperation2().getClassName());
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.MERGE_OPERATION;
	}
	
	
}
