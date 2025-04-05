# NollyAPI

NollyAPI is a versatile and robust API designed for Minecraft plugin developers, providing utilities and tools to streamline development. From custom item handling to WebSocket management, NollyAPI offers a range of features that integrate seamlessly with Minecraft servers. It also supports HTTP routes, middleware, and WebSocket services, enhancing the overall server functionality.

[Visit the NollyAPI Nexus Repository](https://nexus.thenolle.com/#browse/browse:nollyapi)

---

## Features

- **Custom Item Handling**: Easily create and manage custom items with a variety of attributes (e.g., tools, armor, food, etc.)
- **WebSocket Support**: Build and manage WebSocket sessions, broadcast messages, and handle events.
- **HTTP Routes**: Define routes with middleware support using Ktor to create web-based services.
- **Dynamic API Responses**: Handle API responses with dynamic data structures and detailed error reporting.
- **Nexus Integration**: Package and distribute your API via [Nolly's Nexus Repository](https://nexus.thenolle.com/#browse/browse:nollyapi).
- **Extensive KDoc Documentation**: Every function and class comes with detailed documentation to guide developers.

---

## Installation

To install NollyAPI in your project, add the following Maven dependency in your `pom.xml` or Gradle build script:

### Maven

```xml
<dependency>
    <groupId>com.thenolle.plugin</groupId>
    <artifactId>nollyapi</artifactId>
    <version>1.0.0@jar</version>
</dependency>
```

### Gradle

```gradle
dependencies {
    implementation 'com.thenolle.plugin:nollyapi:1.0.0@jar'
}
```

You can also download the JAR from the [Nexus Repository](https://nexus.thenolle.com/#browse/browse:nollyapi).

---

## Usage

### Creating Custom Items

NollyAPI provides an easy-to-use builder for creating custom items. For example:

```kotlin
val customItem = ArmorItem("Epic Helmet", Material.DIAMOND_HELMET)
    .armor(5.0) // Set armor points
    .lore("This helmet grants you the power of the gods.")
    .glow()
    .build("epic_helmet")
```

### Working with WebSockets

NollyAPI makes it simple to handle WebSocket connections. Here's an example of setting up a WebSocket server with event handlers:

```kotlin
webSocket("/chat") {
    onConnect {
        println("New WebSocket connection!")
    }
    onMessage { message ->
        println("Received message: $message")
    }
    onClose { reason ->
        println("Connection closed: $reason")
    }
}
```

### Defining HTTP Routes

Define your HTTP routes using the `WebRouter` class:

```kotlin
val router = WebRouter("/api")

router.get("/status") {
    json(mapOf("status" to "OK"))
}

router.post("/submit") {
    val data: MyRequestData = body() // Parse request body
    json(mapOf("received" to data))
}
```

---

## Contributing

We welcome contributions to NollyAPI! If you'd like to help improve the project, feel free to fork the repository and submit a pull request.

Here’s how you can contribute:
1. **Fork** the repository.
2. **Clone** your fork locally.
3. Make your changes in a new branch.
4. **Push** your changes to your fork.
5. **Create a pull request** from your fork to the main repository.

Before submitting a pull request, make sure to run the tests to verify everything is working correctly.

---

## License

NollyAPI is licensed under the `NFE-OSL v1` License. See the [LICENSE](./LICENSE) file for more details.

---

## Acknowledgments

- Special thanks to the [Ktor](https://ktor.io/) team for providing the underlying server framework.

---

## Documentation

The API is fully documented with [KDoc](https://kotlinlang.org/docs/kotlin-doc.html) for easy reference. You can find detailed documentation for each class, function, and property within the project files.

For more information on how to use the different features of NollyAPI, explore the source code and the Javadoc-style comments within the classes.

---

## Support

If you encounter any issues or need support, feel free to open an issue on the [GitHub repository](https://github.com/nollyscafe/nollyapi/issues). We will do our best to assist you.

---

## Support Me

If you enjoy my work and would like to support the development of **NollyAPI**, you can contribute in several ways:

- **Become a sponsor on GitHub!** [GitHub Sponsors](https://github.com/sponsors/TheNolle)
- **Support me on Patreon** [Patreon](https://www.patreon.com/_nolly) — Get exclusive content and perks!
- **Buy me a coffee** [Buy Me A Coffee](https://www.buymeacoffee.com/nolly.cafe) — Your support keeps me caffeinated!
- **Donate directly via PayPal** [PayPal](https://paypal.me/NollyCafe)

Every bit of support helps me to continue developing and maintaining the project. Thank you so much for your generosity!
