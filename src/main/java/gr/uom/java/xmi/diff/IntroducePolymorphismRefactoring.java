package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.MethodCall;
import gr.uom.java.xmi.UMLOperation;

public class IntroducePolymorphismRefactoring implements Refactoring {
	private static final long serialVersionUID = 7104345607192110031L;
	private String clientClass;
	private String supplierClass;
	private MethodCall methodCall;
	private UMLOperation invokingOperation;

	public IntroducePolymorphismRefactoring(String clientClass,
			String supplierClass, MethodCall methodCall, UMLOperation invokingOperation) {
		this.clientClass = clientClass;
		this.supplierClass = supplierClass;
		this.methodCall = methodCall;
		this.invokingOperation = invokingOperation;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append("class ").append(clientClass);
		sb.append(" employs the ").append(supplierClass).append(" hierarchy through method call ");
		sb.append(methodCall);
		sb.append(" from operation ").append(invokingOperation);
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.INTRODUCE_POLYMORPHISM;
	}

	public String getClientClass() {
		return clientClass;
	}

	public String getSupplierClass() {
		return supplierClass;
	}

	public MethodCall getMethodCall() {
		return methodCall;
	}

	public UMLOperation getInvokingOperation() {
		return invokingOperation;
	}
	
	
}
