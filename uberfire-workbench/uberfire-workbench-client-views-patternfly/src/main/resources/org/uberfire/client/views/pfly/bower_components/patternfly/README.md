# [PatternFly](https://www.patternfly.org) reference implementation

This reference implementation of PatternFly is based on [Bootstrap v3](http://getbootstrap.com/).  Think of PatternFly as a "skinned" version of Bootstrap with additional components and customizations.

## Install with Bower

PatternFly can be installed and managed through [Bower](http://bower.io/). To do so, either add `patternfly` as a dependency in your `bower.json` or run the following:

```
bower install patternfly
```

## RPM

PatternFly is also available as an RPM.  See https://copr.fedoraproject.org/coprs/patternfly/patternfly1/.

## Dependencies

PatternFly incorporates other libraries and components; therefore, in addition to the contents of `dist`, the contents of `components` are also required for a complete installation of PatternFly.

## Development

Development setup requires nodejs. If you do not already have nodejs and npm installed on your system, please see https://github.com/joyent/node/wiki/Installing-Node.js-via-package-manager for how to install on your distribution.

### Install Bower

After ensuring nodejs and npm are available, install [Bower](http://bower.io/) globally:

    npm install -g bower

Bower is used to install and update PatternFly's dependencies.

### Install Development Dependencies

The development includes the use of a number of helpful tasks. In order to setup your development environment to allow running of these tasks, you need to install the local nodejs packages declared in `package.json`. To do this run:

    npm install

This will install all necessary development packages into `node_modules/`. At this point, the gruntjs tasks are available for use such as starting a local development server or building the master CSS file.

Additionally you may need to install the grunt command line utility.  To do this run:

    npm install -g grunt-cli

### Live Reload Server

A local development server can be quickly fired up by using the Gruntjs server task:

    grunt server

This local static asset server (i.e., [http://localhost:9000](http://localhost:9000)) has the advantage of having livereload integration. Thus, if you start the Gruntjs server, any changes you make to `.html` or `.less` files will be automatically reloaded into your browser and the changes reflected almost immediately. This has the obvious benefit of not having to refresh your browser and still be able to see the changes as you add or remove them from your development files.

### Coding Style

* Indentation
    * Use spaces (not tabs)
    * Indentation size is 2 spaces
* Filenames
    * All filenames will use a lowercase-hyphenated naming convention (e.g., single-select-dropdown.less)
* LESSCSS
    * CSS class names use lowercase-hyphenated naming convention (e.g., .navbar-nav)
    * Alphabetize rules by selector
    * Alphabetize properties by declaration
    * Define or override variables centrally in less/variables.less
    * Define or override mixins centrally in less/mixins.less

## Build

### CSS

In development, styling is written and managed through multiple lesscss files. In order to generate a CSS file of all styling, run the build Gruntjs task:

    grunt build

This task will compile and minify the lesscss files into a single CSS file located at `dist/css/patternfly.min.css`.

### PatternFlyIcons Font

PatternFlyIcons font is generated using [IcoMoon](http://icomoon.io/app).  [Load](http://icomoon.io/#docs/save-load) `PatternFlyIcons-webfont.json` as a new project in IcoMoon and update as necessary.  Please commit the updated `PatternFlyIcons-webfont.json` file in addition to the updated font files and supporting LESS/CSS changes.

## Tests

The `tests/` directory contains HTML pages with component and pattern examples in order to facilitate development.  Please consult the official documentation (see below) for full details on how to use PatternFly.

## Release

PatternFly is released through the Bower package manager and RPM. 

### Bower

The Bower package manager determines available versions and installs based upon git tags. To release a new version version of PatternFly, edit `bower.json`, `package.json`, and `MAKEFILE` accordingly.

Update the version listed in `bower.json` by editing the file and changing the line:

```
"version": "<new_version>"
```

Update the version listed in `package.json` by editing the file and changing the line:

```
"version": "<new_version>"
```

Update the `MAKEFILE` by editing the file and changing the following lines:

```
VERSION=<new_version>
MILESTONE=
# PACKAGE_RPM_RELEASE=0.0.$(MILESTONE)
PACKAGE_RPM_RELEASE=1
```

Commit the version bump:

```
git commit -a -m "Version bump to <new_version>"
```

Tag and push upstream (assuming you have commit access):

```
git tag <new_version>
git push && git push --tags
```

The new version will now be automatically available via Bower.

### RPM

RPMs of PatternFly Bower releases are built using Fedora or RHEL and rpm-build.

Verify `MAKEFILE` is properly configured.

Make the dist:

```
make dist
```

Copy the resulting tarball from the previous step to your rpmbuild/SOURCES directly.

e.g., `cp patternfly-1.1.1.tar.gz ~/rpmbuild/SOURCES`

Build the RPM:

```
rpmbuild -ba patternfly.spec
```

Upload the source RPM [1] to a public web server.

[1] e.g., ~/rpmbuild/SRPMS/patternfly1-1.1.1-1.fc20.src.rpm

Ask @rhamilto or @EmilyDirsh to add a new build on [Fedora Copr](https://copr.fedoraproject.org/coprs/patternfly/patternfly1/add_build/) using the URL created in the previous step.

Edit `MAKEFILE` as follows and commit the change:

```
VERSION=<new_version + 1>
MILESTONE=master
PACKAGE_RPM_RELEASE=0.0.$(MILESTONE)
# PACKAGE_RPM_RELEASE=1
```

## Documentation

See [https://www.patternfly.org](https://www.patternfly.org) and [http://getbootstrap.com/](http://getbootstrap.com/).

### Product Backlog

See [https://trello.com/b/Hz3Nmwk4/patternfly-reference-implementation](https://trello.com/b/Hz3Nmwk4/patternfly-reference-implementation).

## License

Modifications to Bootstrap are copyright 2013 Red Hat, Inc. and licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
