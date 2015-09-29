package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAnonymousClass;
import gr.uom.java.xmi.UMLClass;

public class ConvertAnonymousClassToTypeRefactoring implements Refactoring {
	private static final long serialVersionUID = 3782426844601994815L;
	private UMLAnonymousClass anonymousClass;
	private UMLClass addedClass;
	
	public ConvertAnonymousClassToTypeRefactoring(UMLAnonymousClass anonymousClass, UMLClass addedClass) {
		this.anonymousClass = anonymousClass;
		this.addedClass = addedClass;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(anonymousClass);
		sb.append(" was converted to ");
		sb.append(addedClass);
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.CONVERT_ANONYMOUS_CLASS_TO_TYPE;
	}

	public UMLAnonymousClass getAnonymousClass() {
		return anonymousClass;
	}

	public void setAnonymousClass(UMLAnonymousClass anonymousClass) {
		this.anonymousClass = anonymousClass;
	}

	public UMLClass getAddedClass() {
		return addedClass;
	}

	public void setAddedClass(UMLClass addedClass) {
		this.addedClass = addedClass;
	}

}
