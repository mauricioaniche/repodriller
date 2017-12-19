# RepoDriller 1.0.0


## Getting Started

You simply have to start a Java Project in Eclipse. RepoDriller is on Maven, so you can download all its dependencies by only adding this to your pom.xml. Or, if you want, you can see [an example](github.com/mauricioaniche/change-metrics):

```
<dependency>
	<groupid>org.repodriller</groupid>
	<artifactid>repodriller</artifactid>
	<version>1.0.0</version>
</dependency> 
```

Always use the latest version in Maven. You can see them here: [http://www.mvnrepository.com/artifact/org.repodriller/repodriller](http://www.mvnrepository.com/artifact/org.repodriller/repodriller) . You can also see a [fully function pom.xml example](https://gist.github.com/mauricioaniche/3eba747930aea97e4adb).

RepoDriller needs a _Study_. The interface is quite simple: a single _execute()_ method:

```java
import org.repodriller.RepoDriller;
import org.repodriller.Study;

public class MyStudy implements Study {

	public static void main(String[] args) {
		new RepoDriller().start(new MyStudy());
	}

	@Override
	public void execute() {
		// do the magic here! ;)
	}
}
```

All the magic goes inside this method. In there, you will have to configure your study, projects to analyze, metrics to be executed, and output files. Take a look in the example. That's what we have to configure:

```java
public void execute() {
	new RepositoryMining()
		.in(<LIST OF PROJECTS>)
		.through(<COMMITS>)
		.process(<PROCESSOR>, <OUTPUT>)
		.mine();
}
```

Let's start with something simple: we will print the name of the developers for each commit. For now, you should not care about all possible configurations. We will analyze all commits in the project at "/Users/mauricioaniche/workspace/repodriller", outputing _DevelopersVisitor_ to "/Users/mauricioaniche/Desktop/devs.csv".

*   in(): We use to configure the project (or projects) that will be analyzed.
*   through(): The list of commits to analyze. We want all of them.
*   withCommits(): Possible filters to commits, e.g., only commits in a certain branch 
*   reverseOrder(): Commits will be analysed in reverse order. Default starts from the first commit to the latest one.
*   process(): Visitors that will pass in each commit.
*   mine(): The magic starts!

```java
public void execute() {
	new RepositoryMining()
		.in(GitRepository.singleProject("/Users/mauricioaniche/workspace/repodriller"))
		.through(Commits.all())
		.process(new DevelopersVisitor(), new CSVFile("/Users/mauricioaniche/Desktop/devs.csv"))
		.mine();
}
```


In practice, RepoDriller will open the Git repository and will extract all information that is inside. Then, the framework will pass each commit to all processors. Let's write our first _DevelopersProcessor_. It is fairly simple. All we will do is to implement _CommitVisitor_. And, inside of _process()_, we print the commit hash and the name of the developer. RepoDriller gives us nice objects to play with all the data:

```java
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class DevelopersVisitor implements CommitVisitor {

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		writer.write(
			commit.getHash(),
			commit.getCommitter().getName()
		);

	}

	@Override
	public String name() {
		return "developers";
	}

}
```

That's it, we are ready to go! If we execute it, we will have the CSV printed into "/Users/mauricioaniche/Desktop/devs.csv". [Take a look](https://gist.github.com/mauricioaniche/e0d2f8d4c09ef15d17fad25ec582c706).

**I bet you never found a framework simple as this one!**

## Configuring the project

The first thing you configure in RepoDriller is the project you want to analyze. RepoDriller currently suports Subversion and Git repositories. The _SubversionRepository_ and _GitRepository_ classes contains two factory methods to that:

*   _singleProject(path)_: When you want to analyze a single repository.
*   _allProjectsIn(path)_: When you want to analyze many repositories. In this case, you should pass a path to which all projects are sub-directories of it. Each directory will be considered as a project to RepoDriller.

You can also initialize git repositories with their remote HTTP URLs. In this case, RepoDriller will clone the remote repository in order to manipulate the repository history. The _GitRemoteRepository_ class contains the same factory methods of _GitRepository_, but you can also configure it, using a sintaxe such as:


```
	GitRemoteRepository
		.hostedOn(gitUrl)							// URL like: https://github.com/mauricioaniche/repodriller.git
		.inTempDir(tempDir)							// <Optional>
		.asBareRepos()								// <Optional> (1)
		.buildAsSCMRepository())
```

(1) You can clone as bare repository, if your study will work only with repository metadata (commit history info, modifications, etc.) and won't need to checkout/reset files.

## Logging

RepoDriller uses log4j to print useful information about its execution. We recommend tou to have a log4.xml:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

    <appender name="main" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{HH:mm:ss} %5p %m%n"/>
        </layout>
    </appender>

    <category name="org.repodriller">
        <priority value="INFO"/>
        <appender-ref ref="main"/>
    </category>

    <category name="/">
        <priority value="INFO"/>
        <appender-ref ref="main"/>
    </category>

</log4j:configuration>
```

## Selecting the Commit Range

RepoDriller allows you to select the range of commits to be processed. The class _Commits_ contains different methods to that:

*   _all()_: All commits. From the first to the last.
*   _onlyInHead()_: It only analyzes the most recent commit.
*   _single(hash)_: It only analyzes a single commit with the provided hash.
*   _monthly(months)_: It selects one commit per month, from the beginning to the end of the repo.
*   _list(commits...)_: The list of commits to be processed.
*   _range(start,end)_: The range of commits, starting at "start" hash, ending at "end" hash.
*   _betweenDates(from,to)_: The range of commits, starting at "from" timestamp, ending at "to" timestamp.
*   _since(date)_: All commits that appear after a certain timestamp.

One interesting thing about RepoDriller is that is avoids huge commits. When a commit contains too many files (> 50), it will be ignored.

## Filtering commits

RepoDriller comes with a set of common filters that you can apply. As an example, the `OnlyInBranches` filter makes sure
that your Study will only visit commits which exist in specific branches.

* _OnlyInBranches_: Only visits commits that belong to certain branches. 
* _OnlyInMainBranch_: Only visits commits that belong to the main branch of the repository.
* _OnlyNoMerge_: Only visits commits that are not merge commits.
* _OnlyModificationsWithFileTypes_: Only visits commits in which at least one modification was done in that file type, e.g., 
if you pass ".java", then, the study will visit only commits in which at least one Java file was modified; clearly, it will skip
other commits.

You can choose more than one filter as you can be decorated. A working example is:

```
.withCommits(
	new OnlyModificationsWithFileTypes(Arrays.asList(".java", ".xml"),
	new OnlyInBranches(Arrays.asList("master"), 
	new OnlyNoMerge(), 
	new OnlyInMainBranch()))
);
```

Also, you can create your own filter. All you have to do is to extend `CommitFilter` and implement
the `shouldAccept()` method. This method should return **false** if the commit _should not_ be visted.  

## Getting Modifications

You can get the list of modified files, as well as their diffs and current source code. To that, all you have to do is to get the list of _Modification_s that exists inside _Commit_. A _Commit_ contains a hash, a committer (name and email), an author (name, and email) a message, the date, its parent hash, and the list of modification.

```java
@Override
public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
	
	for(Modification m : commit.getModifications()) {
		writer.write(
				commit.getHash(),
				commit.getAuthor().getName(),
				commit.getCommitter().getName(),
				m.getFileName(),
				m.getType()
		);
		
	}
}
```

A _Modification_ contains a type (ADD, COPY, RENAME, DELETE, MODIFY), a diff (with the exact format Git delivers) and the current source code. Remember that it is up to you to handle deleted or renamed files in your study.

## Branches

RepoDriller puts all commits from all branches in a single sentence. It means that different commits from different branches will appear. It is your responsibility to filter the branches in your _CommitVisitor_.

The _Commit_ class contains the _getBranches()_ method. It returns the list of branches in which a commit belongs to. If you want to use only commits in the master branch, you can simply check whether 'master' in inside this set.

Note about the implementation: This is not supported by JGit, so it makes use of Git directly.

## Blame

The _SCM_ class contains a blame() method, which allows you to blame a file in a specific commit:

`List<BlamedLine> blame(String file, String commitToBeBlamed, boolean priorCommit)`

You should pass the file name (relative path), the commit which the file should be blamed, and a boolean
informing whether you want the file to be blamed _before_ (priorCommit=true) or _after_ (priorCommit=false)
the changes of that particular commit.

## Managing State in the Visitor

If you need to, you can store state in your visitors. As an example, if you do not want to process a huge CSV, you can pre-process something before. As an example, if you want to count the total number of modified files per developer, you can either output all developers and the quantity of modifications, and then sum it later using your favorite database, or do the math in the visitor. If you decide to do it, it will be your responsibility to save the results afterwards.

```java
import java.util.HashMap;
import java.util.Map;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class ModificationsVisitor implements CommitVisitor {

	private Map<String, Integer> devs;
	
	public ModificationsVisitor() {
		this.devs = new HashMap<String, Integer>();
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		String dev = commit.getCommitter().getName();
		if(!devs.containsKey(dev)) devs.put(dev, 0);
		
		int currentFiles = devs.get(dev);
		devs.put(dev, currentFiles + commit.getModifications().size());
		
	}

	@Override
	public String name() {
		return "files-per-dev";
	}

}
```

## Parsing Code

You have entire source code of the repository. You may want to analyze it. RepoDriller comes with JDT and ANTLR bundled. JDT is the Eclipse internal parser, so you will not regret to use it.

Let's say we decide to count the quantity of methods in each modified file. All we have to do is to create a _CommitVisitor_, the way we are used to. This visitor will use our _JDTRunner_ to invoke a JDT visitor (yes, JDT uses visitors as well). Notice that we executed the JDT visitor, and then wrote the result.

```java
import java.io.ByteArrayInputStream;

import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.parser.jdt.JDTRunner;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;


public class JavaParserVisitor implements CommitVisitor {

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		for(Modification m : commit.getModifications()) {
		
			if(m.wasDeleted()) continue;
			
			NumberOfMethodsVisitor visitor = new NumberOfMethodsVisitor();
			new JDTRunner().visit(visitor, new ByteArrayInputStream(m.getSourceCode().getBytes()));
			
			int methods = visitor.getQty();
			
			writer.write(
				commit.getHash(),
				m.getFileName(),
				methods
			);
			
		}
		
	}

	@Override
	public String name() {
		return "java-parser";
	}

}
```

The visitor is quite simple. It has methods for all different nodes in the file. All you have to do is to visit the right node. As an example, we will visit _MethodDeclaration_ and count the number of times it is invoked (one per each method in this file). 

```java
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class NumberOfMethodsVisitor extends ASTVisitor {

	private int qty = 0;
	
	public boolean visit(MethodDeclaration node) {
		
		qty++;
		return super.visit(node);
	}
	
	public int getQty() {
		return qty;
	}
}
```


If you want to see all methods available, check the documentation for [ASTVisitor](http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjdt%2Fcore%2Fdom%2FASTVisitor.html).

## Getting the Current Revision

If you need more than just the metadata from the commit, you may also check out to the revision to access all files. This may be useful when you need to parse all files inside that revision.

To that, we will _checkout()_ the revision, and get all _files()_. It returns the list of all files in the project at that moment. Then, it is up to you to do whatever you want. In here, we will use our _NumberOfMethodsVisitor_ to count the number of files in all Java files. Please, remember to _reset()_ as soon as you finish playing with the files.

```java
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mystudy.example4.NumberOfMethodsVisitor;

import org.repodriller.domain.Commit;
import org.repodriller.parser.jdt.JDTRunner;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.RepositoryFile;
import org.repodriller.scm.SCMRepository;

public class JavaParserVisitor implements CommitVisitor {

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		try {
			repo.getScm().checkout(commit.getHash());
		
			List<RepositoryFile> files = repo.getScm().files();
			
			for(RepositoryFile file : files) {
				if(!file.fileNameEndsWith("java")) continue;
				
				File soFile = file.getFile();
				
				NumberOfMethodsVisitor visitor = new NumberOfMethodsVisitor();
				new JDTRunner().visit(visitor, new ByteArrayInputStream(readFile(soFile).getBytes()));
				
				int methods = visitor.getQty();
				
				writer.write(
						commit.getHash(),
						file.getFullName(),
						methods
				);
				
			}
			
		} finally {
			repo.getScm().reset();
		}
	}
	

	private String readFile(File f) {
		try {
			FileInputStream input = new FileInputStream(f);
			String text = IOUtils.toString(input);
			input.close();
			return text;
		} catch (Exception e) {
			throw new RuntimeException("error reading file " + f.getAbsolutePath(), e);
		}
	}
	

	@Override
	public String name() {
		return "java-parser";
	}

}
```

## Dealing with Threads

How good is your machine? RepoDriller can execute the visitor over many threads. This is just another configuration you set in _RepositoryMining_. The _withThreads()_ lets you configure the number of threads the framework will use to process everything.

We suggest you to use threads unless your project _checkout_ revisions. The checkout operation in Git changes the disk, so you can't actually parallelize the work.

```java
@Override
public void execute() {
	new RepositoryMining()
		.in(GitRepository.singleProject("/Users/mauricioaniche/workspace/repodriller"))
		.through(Commits.all())
		.withThreads(3)
		.process(new JavaParserVisitor(), new CSVFile("/Users/mauricioaniche/Desktop/devs.csv"))
		.mine();
}
```

## Configuring Git options

RepoDriller takes a few decisions on the Git configuration. You can change them by passing
a system property. Example: `java -Dgit.maxfiles=2000 -jar ...`.

Existing variables:

- *git.maxfiles*: The max quantity of files in a single commit. Commits with more files than this constant
are ignored. Default is 200.
  
- *git.maxdiff*: The max number of lines in a diff. Diffs higher than that are ignored. Default is 100000.

- *git.diffcontext*: The size of the content that is used by the diff algorithm. Default is git default.

## Creating your own CommitRange

(not written yet)

## Creating your own PersistenceMechanism

(not written yet)


# How do I cite RepoDriller?

For now, cite the repository. 

# How can I discuss about it?

You can subscribe to our mailing list: https://groups.google.com/forum/#!forum/repodriller.

# How to Contribute

Required: Git, Maven.

```
git clone https://github.com/mauricioaniche/repodriller.git
cd repodriller/test-repos
unzip *.zip
```

Then, you can:

* compile : `mvn clean compile`
* test    : `mvn test`
* eclipse : `mvn eclipse:eclipse`
* build   : `mvn clean compile assembly:single`
