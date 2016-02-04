![Updatifier](https://forums-cdn.spongepowered.org/uploads/default/original/2X/e/e4f8fb3c6890b9e4cc51f3b3c690da9074e60967.png)

Updatifier is a simple Sponge plugin that allows other plugins to easily check if they have an update available.

Updatifier is not required, meaning if you download a plugin that has support for Updatifier, and you do not have Updatifier installed, no update checking will be done and no errors will be sent to the console.

####How does Updatifier work?

When your server starts, Updatifier searches for plugins that have added support for Updatifier. When a plugin is found, Updatifier contacts GitHub and compares the versions. If a newer version has been found, then Updatifier will notify you in the server console. If a player joins and has the permission `updatifier.notify`, they too will notified of any updates that are available.

####Installation

To install Updatifier, simply download the latest release and place it in your `mods` folder. Restart/start your server and Updatifier will initialize! There are currently no configuration files.

####Plugins Using Updatifier

If you have added Updatifier support in your plugin, and want to be featured on this list, send me a PM on the forum.

####Planned Features

- Document and improve the API
- Implement support for automatic downloads
- Support for changelogs
- Configuration file

####Developers

Please view the wiki for a detailed list of instructions and examples on how to add Updatifier support in your plugin.

---

**Note:** Currently, Updatifier will check for updates to itself, an option to disable this will be added soon. 

:inbox_tray: [**Download Updatifier**][1]

:memo: [**Issue & Suggestions**][2]

:wrench: [**Source Code**][3]

:books: [**Wiki**][4]

:heavy_dollar_sign: [**Support Me**][5]

[1]: https://github.com/Flibio/Updatifier/releases
[2]: https://github.com/Flibio/Updatifier/issues
[3]: https://github.com/Flibio/Updatifier
[4]: https://github.com/Flibio/Updatifier/wiki
[5]: http://flibio.weebly.com/support-me.html
