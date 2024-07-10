# Docs

Developer Documentation for both users of and contributors to this project is maintained
alongside its code in [the `docs/` folder its git repo](https://github.com/google/android-fhir/tree/master/docs).

The <https://google.github.io/android-fhir/> website is automatically generated from this with Continous Integration.

## Contribute

Contributions to the documentation are very welcome, and should typically be made together with code contributions.

You can edit the `docs/**.md` files with any editor you like and include such changes with your regular Pull Requests.

New MD files needs to be added to `nav:` in [`mkdocs.yaml`](https://github.com/google/android-fhir/blob/master/mkdocs.yaml).

To generate the website, for simplicity we recommend using [GitHub Codespaces](codespaces.md); in a Terminal of such a Codespace:

1. `pipenv shell`: This enters a [Python "virtual environment" (`venv`)](https://docs.python.org/3/library/venv.html), using [`pipenv`](https://pipenv.pypa.io/)

1. `mkdocs serve`

1. Confirm Codespace's _"Port Forward"_ prompt to open `https://...app.github.dev`, et voilà!

## Implementation Details

The website is built using [Material](https://squidfunk.github.io/mkdocs-material/) for [MkDocs](https://www.mkdocs.org/).

The GitHub Action [`Build`](https://github.com/google/android-fhir/actions/workflows/build.yml) (defined in [`build.yml`](https://github.com/google/android-fhir/blob/master/.github/workflows/build.yml)) runs the [`build-docs.bash`](https://github.com/google/android-fhir/blob/master/build-docs.bash) script to generate the HTML in the `site/` directory.
That `site/` directory is created dynamically during the build process but never commited into the Git repository since it is included in the `.gitignore` file.

The docs related build steps run for every PR, to help detect broken doc before merge. However, these runs do not update
the live website, which only happens for builds of the `master` branch (notice `on: push: branches: [master]` and `if: ${{ github.event_name == 'push' }}` in [`build.yml`](https://github.com/google/android-fhir/blob/master/.github/workflows/build.yml).)

The publication works by using the [`upload-pages-artifact`](https://github.com/actions/upload-pages-artifact) action to package the generated HTML from the `site/` directory,
and uploading this artifact to a staging area on GitHub, and the [`deploy-pages`](https://github.com/actions/deploy-pages) action to actually publish that artifact to the live website.
