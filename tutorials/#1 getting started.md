# Tutorial:
## #1 Getting Started [Video Format](./#1%20getting%20started.mp4)

Welcome to this tutorial! In this guide, we'll walk through the process of integrating the `NollyAPI` into your plugin using **Kotlin** and **Gradle**.

### Prerequisites:
- A basic understanding of **plugin** development.
- Kotlin setup in your project.
- Gradle build system in place.
- An existing plugin (or a new one) that you want to integrate `NollyAPI` into.

---

### Step 1: Setting Up Your Project

Ensure that you have a Kotlin-based plugin project set up in your IDE (e.g., IntelliJ IDEA). You should have a project structure like this:
```css
Example
│── src
│── build.gradle.kts
│── settings.gradle.kts
...
```
Your main Kotlin plugin file should extend `JavaPlugin`. here is an example structure of a basic plugin file:
```kt
package com.thenolle.plugin.example

import org.bukkit.plugin.java.JavaPlugin

class Example : JavaPlugin() {
    override fun onEnable() {
        logger.info("Example plugin has been enabled.")
    }

    override fun onDisable() {
        logger.info("Example plugin has been disabled.")
    }
}
```

---

### Step 2: Adding `NollyAPI` as a Dependency

#### Issue: Unresolved `NollyAPI` Reference

While coding, you try to import `com.thenolle.api` into your plugin but encounter the following error:
```yaml
Unresolved reference: api
```
This indicates that the `NollyAPI` dependency is missing or incorrectly configured in your project.

#### Solution: Adding `NollyAPI` to Gradle

To fix this, you need to add `NollyAPI` to your Gradle configuration.
 1. **Open `build.gradle.kts`**: This file is where you configure your project's dependencies and repositories.
 2. **Add the Repository for `NollyAPI`**: Since `NollyAPI` is hosted on a **custom Maven repository**, you need to add it to the `repositories` section of your Gradle file.
    Add the following Maven repository to the `repositories` block:
    ```kt
    repositories {
      maven("https://nexus.thenolle.com/repository/nollyapi/") { name = "nollyapi" }
      // Other repositories
      mavenCentral()
    }
    ```
 3. **Add the `NollyAPI` Dependency**: Next, add the dependency for `NollyAPI` to the `dependencies` block:
    ```kt
    dependencies {
      implementation("com.thenolle.plugin:nollyapi:0.0.1@jar")
    }
    ```
    Make sure to replace `0.0.1` with the correct version of `NollyAPI` that you are using.

---

### Step 3: Sync Gradle

After making these changes to your `build.gradle.kts` file, **sync your Gradle project** so that the required dependencies are downloaded and the project is updated accordingly.
You can do this from the **IDE** or by running the following command in your terminal:
```bash
./gradlew sync
```
This will ensure that `NollyAPI` is now available in your project.

---

### Step 4: Import `NollyAPI` in Your Plugin Code

Once Gradle has synced and the dependencies are resolved, you can now import `NollyAPI` into your Kotlin code.
```kotlin
import com.thenolle.api.SomeClass
```
Now you can use `NollyAPI` with your plugin's code.

---

### Step 5: Verifying in Nexus Repository

If you want to confirm that the `NollyAPI` artifact is available in your repository you can browse the **Nexus repository**.
Navigate to the following URL:
```ruby
https://nexus.thenolle.com/repository/nollyapi/
```
You should see the `nollyapi` artifact listed, and you can confirm the version you are trying to use.

---

### Step 6: Example Usage in Your Plugin

Now that `NollyAPI` is integrated, you can start using it in your plugin. Here's an example of how you might use it:
```kt
package com.thenolle.plugin.example

import com.thenolle.api.nollyapi.util.events.listen
import com.thenolle.api.nollyapi.util.msg.message
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class Example : JavaPlugin() {
    override fun onEnable() {
        listen<PlayerJoinEvent> {
            message(it.player) { chat("&7Welcome to Example!") }
        }

        logger.info("Example plugin has been enabled.")
    }

    override fun onDisable() {
        logger.info("Example plugin has been disabled.")
    }
}
```

---

### Step 7: Final Check

To finalize, ensure that:
- **Gradle sync completed successfully**
- **No unresolved references** in your IDE.
- **You can import and use classes from `NollyAPI`** in your plugin code.

Once everything is set up, you should be ready to develop your plugin with the power of `NollyAPI`

---

### Conclusion

In this tutorial, we covered how to:
- Set up a **plugin** using **Kotlin**.
- Integrate the `NollyAPI` by adding it as a Gradle dependency.
- Troubleshoot and resolve the **unresolved reference** issue.
- Use `NollyAPI` in your plugin code.

Now you are ready to take full advantage of `NollyAPI` in your projects. Happy coding! 🎉
