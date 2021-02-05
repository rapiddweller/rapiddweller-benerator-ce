# Release

This guide describes the creation of a rapiddweller-benerator-ce release.

## The Process

Note this uses `5.6.7` as an example release number.

1. Create a draft release

* Use format `5.6.7` for both the tag and title

2. Update [CHANGELOG.md](CHANGELOG.md)

* Write the release highlights
* Copy in headings ready for the next release

3. Create release commit

  ```
  git checkout -b release-5.6.7
  ```

4. Create merge request getting other maintainers to review
5. Copy the release notes in to the draft release, adding a link to [CHANGELOG.md](CHANGELOG.md)
6. Update you local master branch

  ```
  git checkout master
  git pull
  ```

7. Create & push the tag

  ```
  git tag 5.6.7
  git push --tags
  ```

8. The GitHub pipeline creates the release artifacts