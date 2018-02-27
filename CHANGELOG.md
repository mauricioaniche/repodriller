# Changelog

## 2.0.1 (HEAD)

- Bug fix: when the diff filter was activated, diff was not being collected.
See #126 (by @ayaankazerouni) 

## 2.0.0

- Bug fix: Our scheduler was not dividing commits
properly when using threads, causing the study to throw an exception (@davisjam, #119).

- New feature: whitelist/blacklist of which diffs to actually
load. You can use it through `CollectConfiguration` API (@ayaankazerouni, #121)

- Users can now refer to the home directory, e.g., `~/Desktop/file.csv` (@mauricioaniche, #125).

- _IMPORTANT BUG FIX AND BREAKING BEHAVIOR_: RepoDriller always returned a "more complete"
list of commits than `git log`. This can be misleading. From now on, RepoDriller returns the same
list as `git log` would. This might affect previous studies (@mauricioaniche, #124). 

## 1.5.0

(You should not use this version. 1.4.0 introduced a breaking change, and we wrongly generated this 1.5.0. Go to the 2.0.0 version directly)

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
