# Tutorial:
## #2 Event Listeners with NollyAPI

In this tutorial, you'll learn how to use **NollyAPI**'s minimalist listener system to handle Bukkit events with just one function: `listen<T>()`.

---

### What You’ll Learn:
- How to register events using `NollyAPI`
- How to fix `EventListener.init() not called` errors
- How to structure cleaner, more expressive event handling code

---

### 🧠 Why Use `NollyAPI.listen`?

Instead of creating verbose listener classes, you can write clean, inline listeners like this:
```kt
listen<PlayerJoinEvent> {
    it.joinMessage = "Welcome to Civitas, ${it.player.name}!"
}
```

Behind the scenes, `NollyAPI` uses a DSL and Kotlin reified types to register events dynamically with Bukkit — no need to mess with annotations or listener classes.

---

### 🧰 Prerequisites:
- You’ve followed [Tutorial #1](./%231%20getting%20started.md)
- You have `NollyAPI` working and Gradle is synced

---

### Step 1: Call `EventListener.init(this)`

Before using `listen<T>()`, you **must initialize** the NollyAPI event system at the top of `onEnable`:
```kt
override fun onEnable() {
    EventListener.init(this) // always do this first
}
```

If you skip this step, your plugin will crash with:
```log
IllegalStateException: EventListener.init(plugin) was not called!
```

---

### Step 2: Add a Listener

After calling `EventListener.init(this)`, you're free to register any Bukkit event like so:
```kt
listen<PlayerJoinEvent> {
    it.joinMessage = "Welcome to Civitas, ${it.player.name}!"
    it.player.inventory.addItem(ItemStack(Material.GRASS_BLOCK))
}
```

This example:
- Overrides the join message
- Gives the player 1 grass block

---

### Step 3: Full Example

Here’s a complete plugin file:
```kt
package com.thenolle.plugin.example

import com.thenolle.api.nollyapi.util.events.EventListener
import com.thenolle.api.nollyapi.util.events.listen
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class Example : JavaPlugin() {
    override fun onEnable() {
        EventListener.init(this)

        listen<PlayerJoinEvent> {
            it.joinMessage = "Welcome to Example, ${it.player.name}!"
            it.player.inventory.addItem(ItemStack(Material.GRASS_BLOCK))
        }

        logger.info("Example plugin has been enabled.")
    }

    override fun onDisable() {
        logger.info("Example plugin has been disabled.")
    }
}
```

---

### 🛠️ Advanced: Multiple Listeners

You can register as many listeners as you want — just call `listen<T>()` for each event type:
```kt
listen<PlayerQuitEvent> {
    it.quitMessage = "Goodbye, ${it.player.name}!"
}

listen<BlockBreakEvent> {
    it.isCancelled = it.player.hasPermission("civitas.protect").not()
}
```

---

### 🧯 Troubleshooting

| Problem                          | Solution                                                                |
| -------------------------------- | ----------------------------------------------------------------------- |
| `EventListener.init not called`  | Make sure `EventListener.init(this)` is at the top of `onEnable()`      |
| `listen<>()` doesn’t register    | Double-check the imports and make sure NollyAPI is in your dependencies |
| Plugin loads but nothing happens | Rebuild your `.jar`, restart the server, and check the console          |

---

### ✅ Summary

You now know how to:
- Register events with a one-liner
- Handle event logic inline
- Avoid common pitfalls when using `NollyAPI.listen`
