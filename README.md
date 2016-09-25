# ∫ Integral RestEasy

A replacement for `tapestry-resteasy` by [Tynamo](https://github.com/tynamo/tapestry-resteasy) supporting the latest RestEasy version (3.0.19.Final) and a nice way to provide custom headers in the pipeline.

## Why?

We love to use the latest and greatest software available. And we really love Tapestry 5.4, but the RestEasy plugin by Tynamo is a little outdated an we wanted to expand it for our usage. So we updated the dependencies and added some Java 8 and created an _almost_ drop-in replacement.

## Usage

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
| MAPPING_PREFIX        | resteasy.servlet.mapping-prefix         | /rest   | Maps the Resteasy servlet to an url prefix                                                                      |
| AUTOSCAN              | integral.resteasy.autoscan              | true    | Scan "InternalConstants.TAPESTRY_APP_PACKAGE_PARAM + ResteasySymbols.AUTOSCAN_PACKAGE_NAME" for REST resources. |
| AUTOSCAN_PACKAGE_NAME | integral.resteasy.autoscan-package-name | rest    | Package name for AUTOSCAN. Omit a dot-prefix.                                                                   |
| CORS_ENABLED          | integral.resteasy.cors-enabled          | false   | Add CORS-header to responses if Origin is available in request                                                  |


## Changes from tapestry-resteasy

Following general changes were done:

- Reformatting, a little renaming and some Java 8 goodness
- Removal of test (for now, will be provided later)
- Removal of JSAPI support
- Added HeaderProvider / HeaderService


## Contribute

If you want to contribute feel free to open issues or provide pull requests. Please read the additional info in the folder `_CONTRIBUTE`.


## License

This code is based on / mostly copied from Tynamo Tapestry [RestEasy](https://github.com/tynamo/tapestry-resteasy) by
[Tynamo](http://www.tynamo.org). It also has the Apache 2.0 license, see `LICENSE.txt` and `NOTICE.txt` for more details.
