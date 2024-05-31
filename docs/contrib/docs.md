# Docs

Developer Documentation for both users of and contributors to this project is maintained
alongside its code in [the `docs/` folder its git repo](https://github.com/google/android-fhir/tree/master/docs).

The <https://google.github.io/android-fhir/> website is automatically generated from this with Continous Integration.

This website is built using [Material](https://squidfunk.github.io/mkdocs-material/) for [MkDocs](https://www.mkdocs.org/).

## Contribute

Contributions to the documentation are very welcome, and should typically be made together with code contributions.

You can edit the `docs/**.md` files with any editor you like and include such changes with your regular Pull Requests.

New MD files needs to be added to `nav:` in [`mkdocs.yaml`](https://github.com/google/android-fhir/blob/master/mkdocs.yaml).

To generate the website, for simplicity we recommend using [GitHub Codespaces](codespaces.md); in a Terminal of such a Codespace:

1. `pipenv shell`: This enters a [Python "virtual environment" (`venv`)](https://docs.python.org/3/library/venv.html), using [`pipenv`](https://pipenv.pypa.io/)

1. `mkdocs serve`

1. Confirm Codespace's _"Port Forward"_ prompt to open `https://...app.github.dev`, et voilà!

