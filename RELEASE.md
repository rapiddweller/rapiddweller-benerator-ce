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

## Release to Homebrew

1. Fork the [Homebrew/homebrew-core](https://github.com/Homebrew/homebrew-core)
to your personal Github account.

2. Tap (download a local clone of) the repository of core Homebrew formulae:

  ```shell
  brew tap --force homebrew/core
  ```

3. Change to the directory containing Homebrew formulae:

  ```shell
  cd "$(brew --repository homebrew/core)"
  ```

4. Add your pushable forked repository as a new remote:

  ```shell
  git remote add <YOUR_USERNAME> https://github.com/<YOUR_USERNAME>/homebrew-core.git
  ```

  `<YOUR_USERNAME>` is your GitHub username, not your local machine username.

5. Update brew formula with the automation command [`bump-formula-pr`](https://docs.brew.sh/Manpage#bump-formula-pr-options-formula):

  ```shell
  brew bump-formula-pr \
    --url="https://github.com/rapiddweller/rapiddweller-benerator-ce/releases/download/5.6.7/rapiddweller-benerator-ce-5.6.7-jdk-11-dist.tar.gz" \
    --sha256="PASTE THE SHA256 CHECKSUM HERE" \
    benerator
  ```

Note the `url` need to point to correct asset, version from our github release.

This will create a pull request into the Homebrew core repository.
Once the Homebrew maintainers team approve it will be merged.

See [Homebrew's Documentation](https://docs.brew.sh/How-To-Open-a-Homebrew-Pull-Request.html)
for more detail.
