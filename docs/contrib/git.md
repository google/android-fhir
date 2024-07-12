# Git Tips

This page documents [Git](https://git-scm.com) tips for contributors to this project.

This project generally follows [these guidelines](https://github.com/google/fhir-data-pipes/blob/master/doc/review_process.md) from our `fhir-data-pipes` sister project.

## Usage

### Edit on GitHub Web UI

To make simple single file changes, notably e.g. to `docs/*.md` Markdown files,
it can be convenient to simply click the _"Pencil" button_ on GitHub's Web UI
to _Edit in place._ For a more fully fledged IDE for docs, see [Docs](docs.md).

### Locally with Git CLI

Using the standard `git` CLI client, you would typically do the following:

1. Initially, one time only, click Fork on https://github.com/google/android-fhir/tree/master and:

       git clone git@github.com:google/android-fhir.git
       cd android-fhir
       git remote add YOUR-UID git@github.com:YOUR-UID/android-fhir.git

2. Now every time you want to propose a change, you do:

       git checkout -b NEW-BRANCH-NAME
       ... do some work ...
       git commit -a -m "MESSAGE ABOUT WHAT YOU CHANGED"
       git push YOUR-UID

   This will print something with a URL you can click on to create a Pull Request.

3. To _"rebase"_ your local branch so that it has the latest upstream work:

       git checkout master
       git pull
       git checkout THAT-BRANCH-NAME
       git rebase master

4. When you get code review feedback and would like to make some changes:

       git checkout THAT-BRANCH-NAME
       ... do some work ...
       git commit -a -m "Incorporated review feedback."
       git push --force-with-lease YOUR-UID

Note that, as per [these guidelines](https://github.com/google/fhir-data-pipes/blob/master/doc/review_process.md) we do not `commit --amend`. (Maintainers will _"squash"_ when merging PRs.)

### Locally with GitHub's CLI

https://cli.github.com is a GitHub specific tool which allows you to do certain
operations from the CLI instead of having to click on the GitHub Web UI, e.g. :

    gh repo fork google/android-fhir --clone --remote

    gh pr create

    gh pr checkout 2306

https://github.com/topics/gh-extension has a lot of fancy extensions for `gh`.

`gh` is also useful to manage GitHub Secrets.

## Processes

## Further Resources

* https://git-scm.com/doc
* https://en.wikipedia.org/wiki/Git
* https://developer.mozilla.org/en-US/docs/Learn/Tools_and_testing/GitHub
* https://docs.github.com/en/get-started/quickstart/hello-world
* https://www.w3schools.com/git/git_intro.asp
