## Kokoro Infrastructure

The files in this directory serve as plumbing for running tests under Kokoro,
our internal CI tool. If there are any changes required to these config files,
please file an issue.

The [build script](./gcp_ubuntu/kokoro_build.sh) runs each time a PR is opened,
or when a commit is pushed to an open PR, or when an a PR is merged into the
master branch. The script runs on a Google Compute Engine machine hosted in our
own Google Cloud project, with public access to the logs.

The script downloads the dependencies it needs, compiles, builds, and unit tests
the code, and then uses Firebase Test Lab to run instrumentation tests. Code
coverage reports are then uploaded to Codecov, where they are displayed on the
GitHub Pull Request/Repo Homepage.

**WARNING**:Do NOT run the script on your local machine.
