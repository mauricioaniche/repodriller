# Changelog

## 1.3.1

- Fix issue with annotated tags in `BetweenTags(from, to)` (see PR #76, by ishepard)
- Javadoc to some of the main RepoDriller classes (see PR #71, #72, #73, by davisjam)
- Internal refactoring (see PR #69, by LiamClark)

## 1.3.0

- No need for `name()` in CommitVisitor anymore (the fallback implementation will return the name of the class).
- Update JGit to 4.8.0.
- New commit range: `BetweenTags(from, to)`.
- Optional initialize and finalize callbacks for CommitVisitors.