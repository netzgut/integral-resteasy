# ∫ Integral RestEasy

A replacement for `tapestry-resteasy` by [Tynamo](https://github.com/tynamo/tapestry-resteasy) supporting the latest RestEasy version (3.0.19.Final) and a nice way to provide custom headers in the pipeline.

## Why?

We love to use the latest and greatest software available. And we really love Tapestry 5.4, but the RestEasy plugin by Tynamo is a little outdated an we wanted to expand it for our usage. So we updated the dependencies and added some Java 8 and created an _almost_ drop-in replacement.

## Usage

NOTE: This library isn't released yet on jcenter etc.!

### `build.gradle`:
```groovy
respositories {
  jcenter()
}

dependencies {
    compile "net.netzgut.integral:integral-resteasy:0.0.1"
}

```

Now just import `ResteasyModule.class` in your app module and you're good to go.

### Available Symbols

| Symbol                | Value                                   | Default | Description                                                                                                     |
| --------------------- | --------------------------------------- | ------- | --------------------------------------------------------------------------------------------------------------- |
| MAPPING_PREFIX        | resteasy.servlet.mapping.prefix         | /rest   | Maps the Resteasy servlet to an url prefix                                                                      |
| AUTOSCAN              | integral.resteasy.autoscan              | true    | Scan "InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ResteasySymbols.AUTOSCAN_PACKAGE_NAME" for REST resources. |
| AUTOSCAN_PACKAGE_NAME | integral.resteasy.autoscan-package-name | rest    | Package name for AUTOSCAN. Omit a dot-prefix.                                                                   |
| CORS_ENABLED          | integral.resteasy.cors-enabled          | false   | Add CORS-header to responses if Origin is available in request                                                  |
| VERSIONING_ENABLED    | integral.resteasy.versioning-enabled    | false   | Adds version infos to the header via @Version annotation.

### Versioning

You can easily add an version headers to the response headers by enabling the Symbol
`ResteasySymbols.VERSIONING_ENABLED`. After that you need to annotate your resource
classes and/or methods with `@Version("<current version")` /
`@Version(value = "<current version>", deprecated = "<deprecated since>")` and the
following headers will be added to the response:
- `Api-Version: <current version>`
- `Api-Deprecated: <deprecated since>`

The deprecation header will be omitted if no version is present.

If versioning is enabled a warning will be logged if the `@Version` is missing.


## Gradle task uploadArchives

To upload the archives you need to set some project properties:

- snapshot_repository
- snapshot_repository_username
- snapshot_repository_password

The fallbacks are empty strings, so you can build etc. without gradle failing instantly.


## Changes from tapestry-resteasy

Following general changes were done:

- Reformatting, a little renaming and some Java 8 goodness
- Removal of test (for now, will be provided later)
- Removal of JSAPI support
- Added HeaderProvider / HeaderService


## Contribute

It's awesome that you want to contribute! Please see [this repository](https://github.com/netzgut/contribute)
for more details.


## License

This code is based on / mostly copied from Tynamo Tapestry [RestEasy](https://github.com/tynamo/tapestry-resteasy) by
[Tynamo](http://www.tynamo.org). It also has the Apache 2.0 license, see `LICENSE.txt` and `NOTICE.txt` for more details.
