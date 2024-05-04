# The Java SDK for the Abelian Blockchain

[![GitHub Release](https://img.shields.io/badge/Latest%20release-1.0.0-blue.svg)](https://github.com/pqabelian/abelian-sdk-java/releases/)
[![Made with Java](https://img.shields.io/badge/Powered%20by-Java-green.svg)](https://www.java.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-orange.svg)](https://opensource.org/licenses/MIT)

A Java library that allows developers to interact with the Abelian blockchain.

It provides a simple way to connect to the Abelian network, query the state of the blockchain, decode privacy-protected data of managed accounts, and send transactions.

## 1. System requirements
The Abelian Java SDK supports the following platforms:
- Linux x86
- Linux arm64
- MacOS x86
- MacOS arm64 (Apple Silicon)

The system requirements are as follows:
- Java 8 or later
- OpenSSL 1.1.1 or later (on Linux only)

On macOS x86/arm86 platforms, the SDK only requires Java 8 or later. On Linux platforms, the SDK also requires OpenSSL 3 or later to be installed.

## 2. Getting started with the SDK demo

Before using the SDK in your own project, you can try the SDK demos to get a sense of how the SDK works. The SDK demos is packed as a standalone package containing all jar dependencies. You can download the demos package `abel4j-demo-x.y.z.zip` from the [releases page](https://github.com/pqabelian/abelian-sdk-java/releases/).

Please refer to the the document [*Understanding the Abelian Java SDK by Demos*](abel4j-demo/README.md) for more details.

## 3. Using the SDK in your project

To use the Abelian Java SDK in your project, you need to add the single JAR file `abel4j-x.y.z.jar` along with the required dependencies to your project. The SDK JAR file can be either downloaded from the [releases page](../../releases) or built from the source code.

The SDK requires the following dependencies:
- `net.java.dev.jna:jna:5.13.0`
- `com.google.protobuf:protobuf-java:3.21.12`
- `org.apache.httpcomponents.client5:httpclient5-fluent:5.2.1`
- `com.fasterxml.jackson.core:jackson-databind:2.14.2`
- `org.slf4j:slf4j-api:2.0.6`
- `org.slf4j:slf4j-simple:2.0.6`

## 4. Building the SDK from source code

To build the SDK from source code, simply run `make` in the root directory of the repository. The distribution packages will be created in the `abel4j-demo/build` directory. To clean up the build, run `make clean`.