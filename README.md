# RepoDriller

[![Build Status](https://travis-ci.org/mauricioaniche/repodriller.svg?branch=master)](https://travis-ci.org/mauricioaniche/repodriller)

RepoDriller is a Java framework that helps developers on mining software repositories. With it, you can easily extract information from any Git repository, such as commits, developers, modifications, diffs, and source codes, and quickly export CSV files.

Take a look at our [manual folder](https://www.github.com/mauricioaniche/repodriller/tree/master/manual) and [our many examples](https://github.com/mauricioaniche/repodriller-tutorial). Or [talk to us in our mailing list](https://groups.google.com/forum/#!forum/repodriller).

# Advice to researchers

## Difficulties in mining git

You should read this paper:
- Bird, Christian, et al. "The promises and perils of mining git." Mining Software Repositories, 2009. MSR'09. 6th IEEE International Working Conference on. IEEE, 2009. [Link](http://cs.queensu.ca/~ahmed/home/teaching/CISC880/F10/papers/MiningGit_MSR2009.pdf).

# FAQs

## Why use an MSR framework?

There's no question that Mining Software Repositories (MSR) studies benefit from automation.
The datasets are too large to analyze manually.

So the choice is whether to use an MSR framework or to write your own scripts.
An MSR framework offers two benefits:
- The researcher can focus on their questions and not on the infrastructure.
- Coding against a framework improves standardization and therefore reproducibility (see Robles, Gregorio. "Replicating MSR: A study of the potential replicability of papers published in the Mining Software Repositories proceedings." Mining Software Repositories (MSR), 2010 7th IEEE Working Conference on. IEEE, 2010.).

## How is RepoDriller different from other MSR frameworks?

RepoDriller is a minimalist's MSR framework, a lightweight tool for flexible analysis.
- RepoDriller is *lightweight*:
	1. It's a straightforward Java framework with the APIs you need -- no more, no less.
	2. You pay for storage and computation when you need to. No significant pre-processing stage, no giant database.
- RepoDriller is *flexible*:
	1. Write arbitrary analyses in the popular Java programming language.
	2. RepoDriller has the right knobs -- tune which commits you visit, how much concurrency you want, etc.

Here's how it compares to some other MSR frameworks and tools:
- [GHTorrent](http://ghtorrent.org/) lets you query GitHub events.
	1. You are restricted to querying projects on GitHub.
	2. You are restricted to the information exposed in a GitHub API.
- [Boa](http://boa.cs.iastate.edu/) lets you query ASTs on a pre-defined set of repositories.
	1. You are restricted to the repositories tracked by Boa.
	2. You must write queries in the Boa language, largely against ASTs.
	3. If you roll your own Boa cluster, you are restricted to repositories with languages that Boa can import (i.e. parse into ASTs).
- [Alitheia Core](https://github.com/istlab/Alitheia-Core) is a scalable platform for MSR.
	1. Alitheia-Core is a heavyweight approach. You pay a lot of up-front costs (configuration, pre-processing, etc.) in exchange for a scalable analysis. If you're doing exploratory research, the overhead may not be worth it.
	2. Alitheia Core is no longer being maintained.

## How do I cite RepoDriller?

For now, cite the repository.

## Is there a discussion forum?

You can subscribe to our mailing list: https://groups.google.com/forum/#!forum/repodriller.

## How do I contribute?

Required: Git, Maven.

```
git clone https://github.com/mauricioaniche/repodriller.git
cd repodriller/test-repos
unzip \*.zip
```

Then, you can:

* compile : `mvn clean compile`
* test    : `mvn test`
* eclipse : `mvn eclipse:eclipse`
* build   : `mvn clean compile assembly:single`

# License

This software is licensed under the Apache 2.0 License.
