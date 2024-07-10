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

The [`Build` GitHub Action](https://github.com/google/android-fhir/actions/workflows/build.yml)
(see [`build.yml`](https://github.com/google/android-fhir/blob/master/.github/workflows/build.yml)) has the required steps
which runs the [`build-docs.bash`](https://github.com/google/android-fhir/blob/master/build-docs.bash) script which generates the HTML in the `site/` directory.
That `site/` directory is intentionally on `.gitignore` and only created dynamically during the build process, but not commited into the Git repository.

The basic docs related build steps intentionally runs for every PR, which helps to detect broken doc before merge (AKA "pre-submit"). The PRs do however obviously NOT actually update
the live <https://google.github.io/android-fhir/> website; that of course only happens for builds of the default `master` branch! (The way this works technically is due to the
automagical combination of `on: push: branches: [master]` and `if: ${{ github.event_name == 'push' }}` in `build.yml`.)
