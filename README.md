XML Language Server (LemMinX)
===========================
This repository is forked from [Eclipse LemMinX](https://github.com/eclipse/lemminx)

**LemMinX** is a XML language specific implementation of the [Language Server Protocol](https://github.com/Microsoft/language-server-protocol)
and can be used with any editor that supports the protocol, to offer good support for the **XML Language**. The server is based on:

 * [Eclipse LSP4J](https://github.com/eclipse/lsp4j), the Java binding for the Language Server Protocol.
 * Xerces to manage XML Schema validation, completion and hover
 * Generates syntax tree for Synapse XML configurations.

Get started
--------------
* Clone this repository
* Open the folder in your terminal / command line
* Run `./mvnw clean verify` (OSX, Linux) or `mvnw.cmd clean verify` (Windows)
* After successful compilation you can find the resulting `org.eclipse.lemminx-uber.jar` in the folder `org.eclipse.lemminx/target`

Developer
--------------

To debug the XML LS you can use XMLServerSocketLauncher:

1. Run the XMLServerSocketLauncher in debug mode (e.g. in eclipse)
2. Connect your client via socket port. Default port is 5008, but you can change it with start argument `--port` in step 1

Client connection example using Theia and TypeScript:

```js
let socketPort = '5008'
console.log(`Connecting via port ${socketPort}`)
const socket = new net.Socket()
const serverConnection = createSocketConnection(socket,
    socket, () => {
        socket.destroy()
    });
this.forward(clientConnection, serverConnection)
socket.connect(socketPort)
```

Generating a native binary:
---------------------------------
To generate a native binary:
- [Install GraalVM 20.2.0](https://www.graalvm.org/docs/getting-started/#install-graalvm)
- In a terminal, run `gu install native-image`
- Execute a Maven build that sets the flag `native`: `./mvnw clean package -Dnative -DskipTests`
  - On Linux, compile with `./mvnw clean package -Dnative -DskipTests -Dgraalvm.static=--static`
    in order to support distributions that don't use `glibc`, such as Alpine Linux
- It will generate a native binary in `org.eclipse.lemminx/target/lemminx-{os.name}-{architecture}-{version}`

OS specific instructions:
- __Linux__:
  - Make sure that you have installed the static versions of the C++ standard library
    - For instance, on Fedora Linux, install `glibc-static`, `libstdc++-static`, and `zlib-static`
- __Windows__:
  - When installing native-image, please note that `gu` is an existing alias in PowerShell.
  Remove the alias with `Remove-Item alias:gu -Force`, refer to `gu` with the absolute path, or use `gu` under `cmd.exe`.
  - Make sure to run the Maven wrapper in the "Native Tools Command Prompt".
  This command prompt can be obtained through installing the Windows SDK or Visual Studio, as
  mentioned in the [GraalVM installation instructions](https://www.graalvm.org/docs/getting-started-with-graalvm/windows/).

`native-image` Development Instructions:
- Reflection:
  - If you need to use reflection to access a private field/method, simply register the field/methods that you access in `reflect-config.json`
  - If you need to parse some JSON using Gson, make sure to register the fields and methods of the class that you are parsing into in `reflect-config.json`
    - This needs to be done recursively, for all classes that it has member variables of, including `enum`s
    - Settings are all deserialized, so whenever a setting is added, make sure to register the classes
  - Manually test the binary and check the logs for reflection errors/NPEs


Extensions
----------

The XML Language Server can be extended to provide additional validation and assistance. Read the [LemMinX-Extensions docs](./docs/LemMinX-Extensions.md) for more information