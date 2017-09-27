# Changelog

## 1.3.0

- No need for `name()` in CommitVisitor anymore (the fallback implementation will return the name of the class).
- Update JGit to 4.8.0.
- New commit range: `BetweenTags(from, to)`.
- Optional initialize and finalize callbacks for CommitVisitors.