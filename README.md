# RefDetector
RefDetector is a library/API written in Java that can detect refactorings applied in the history of a Java project.

Currently, it supports the detection of the following refactorings:

1. Extract Method
2. Inline Method
3. Move Method/Attribute
4. Pull Up Method/Attribute
5. Push Down Method/Attribute
6. Extract Superclass/Interface
7. Move Class

In order to build the project, import it to Eclipse IDE as a Java project, and install the Apache IvyDE plug-in.

## Contributors ##
The code in package **gr.uom.java.xmi** has been developed by **Nikolaos Tsantalis**.

The code in package **ca.ualberta.cs.data** has been developed by **Nikolaos Tsantalis** and **Fabio Rocha**.

The code in package **br.ufmg.dcc.labsoft.refactoringanalyzer** has been developed by **Danilo Ferreira e Silva**.

## API usage guidelines ##

RefDetector can automatically detect refactorings in the entire history of 
git repositories, or at specified commits.
In the code snippet below we demonstrate how to print all refactorings performed
in the toy project https://github.com/danilofes/refactoring-toy-example.git.

```java
GitService gitService = new GitServiceImpl();
GitHistoryRefactoringDetector detector = new GitHistoryRefactoringDetectorImpl();

Repository repo = gitService.cloneIfNotExists(
    "refactoring-toy-example", 
    "https://github.com/danilofes/refactoring-toy-example.git");
detector.detectAll(repo, "master", new RefactoringHandler() {
  @Override
  public void handle(RevCommit commitData, List<Refactoring> refactorings) {
    System.out.println("Refactorings at " + commitData.getId().getName());
    for (Refactoring ref : refactorings) {
      System.out.println(ref.toString());
    }
  }
});
```


There is also a lower level API that works comparing the source code from two
folders that contain the code before and after the code changes:  

```java
UMLModel model1 = new ASTReader(new File("/path/to/version1/")).getUmlModel();
UMLModel model2 = new ASTReader(new File("/path/to/version2/")).getUmlModel();
UMLModelDiff modelDiff = model1.diff(model2);
List<Refactoring> refactorings = modelDiff.getRefactorings();
```

Note that by doing this you may get different results from the git history analysis, because
it uses information from git to better identify moves and renames.
