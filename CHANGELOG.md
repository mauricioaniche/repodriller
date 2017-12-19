# Changelog

## 1.4.0

- The `collect()` option so that users can configure what exactly to 
extract from the repo. This can highly increase the performance
of the study (@mauricioaniche)

- Configuring maven to compile using UTF-8, which removes warnings
during the build (@ttben)

- The `Commit` class now contains both parents of commit, in case of 
a merge (@mauricioaniche)

- PRs now receive an automatic comment with the performance of the
changes (@mauricioaniche)

- Using log4j2 instead of log4j 1, which was deprecated (@ttben)

- Auxiliary method in the `SCM` interface to get the diff between
two commits (@ayaankazerouni)

- Internal improvements in the `CSVFile` class (@davisjam)

- Several refactorings and documentation improvements (@davisjam, @ayaankazerouni)

- Manuals for different versions in different files (@mauricioaniche)

## 1.3.1

- Fix issue with annotated tags in `BetweenTags(from, to)` (see PR #76, by ishepard)
- Javadoc to some of the main RepoDriller classes (see PR #71, #72, #73, by davisjam)
- Internal refactoring (see PR #69, by LiamClark)

## 1.3.0

- No need for `name()` in CommitVisitor anymore (the fallback implementation will return the name of the class).
- Update JGit to 4.8.0.
- New commit range: `BetweenTags(from, to)`.
- Optional initialize and finalize callbacks for CommitVisitors.