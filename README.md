# DiscordUtils
<a alt="Version:"><img src="https://img.shields.io/github/v/release/JoshiCodes/DiscordUtils"></a><br>
<img src="https://repo.joshicodes.de/api/badge/latest/releases/de/joshicodes/discordutils?prefix=v">

Discord JDA Utilities

Some simple Utils for JDA Bots


## Installation

**You require Java 16 or higher to use this library.**

To install the utils as dependency in maven, you can use the following in your pom.xml:

```xml
<dependency>
    <groupId>de.joshicodes</groupId>
    <artifactId>discordutils</artifactId>
    <version>VERSION HERE</version>
</dependency>
```
**Tip:** *Replace the `VERSION HERE` with the current release (``release`` or ``joshicodes-de``).*


**If this does not work, try using my repo:**
```xml
<repository>
  <id>joshicodes-de-releases</id>
  <name>JoshiCodes.de Repository</name>
  <url>https://repo.joshicodes.de/releases</url>
</repository>
```


## Usage
There are different Things to use this library. Most of them require to enable the corresponding Module.
You can do this with
```
ModuleLoader.loadModules(JDABuilder, Module, @Nullable Module...)
```

### Configuration
One of the most important Features, are the Configurations. These do not require a Module and could be used Stand-alone.
There are a few different sort of Configurations, which are all listed below:
<br>
**Note:** There are far better implementations for Configuration Files. This implementation is old and only used for simple Configurations.
Personally, I would recommend using [boosted-yaml](https://github.com/dejvokep/boosted-yaml).
```
    - JsonConfig
    - YamlConfig
    - TextConfig
```
each of them extends the `FileConfig`.

To use any of the Configurations, just create a new Instance of it with the wanted File as parameter.
You then can use the `get(String)` and `#set(String key, Object value)` methods to get and set values.
To save your changes, use the `#save()` method.
If you have a template of the config stored in your resource, you can use the `#copyDefaults()` method to copy this template to the given File. If the file already exists, nothing will happen.

For example:
```
// Create new Config
JsonConfig config = new JsonConfig(new File("config.json"));
// Copy Template if File does not exists
config.copyDefaults();

// Get an already existing value
String asdf = config.getString("asdf"); // You could use #get(). #getString() casts this value to a String.
System.out.println(asdf); // Output: qwer

// Set a new value
config.set("asdf", "zxcv");
config.save(); // Save the changes to the file

```
config.json in the resources:
```json
{
  "asdf": "qwer"
}
```
config.json (output):
```json
{
  "asdf": "zxcv"
}
```



### Modules:
#### Reaction Roles (with Buttons)
To enable this Module use:
```
JDABuilder builder = [..]
ModuleLoader.loadModules(builder, new ReactionMessageModule());
```

You then can create a new ReactionMessage with the ReactionMessage.Builder.
With this Builder you can define the message and add the Role Buttons.
To change the message use `ReactionMessage.Builder#setEmbed`.
You then can add new Roles using `ReactionMessage.Builder#addOption(String label, String roleId)`
If you want, you can change the Type from MULTIPLE to SINGE, wich allows the user to only have one Role per message.
You can do that using `ReactionMessage.Builder#setType(ReactionMessage.Type)`.
There is also the option to use a SelectMenu instead of Buttons. To do that, just set the Type to `ReactionMessage.Type.SELECT`.


For example:
````
// Create new Builder
ReactionMessage.Builder builder = new ReactionMessage.Builder();

// Set the Embed
EmbedBuilder embedBuilder = new EmbedBuilder();
embedBuilder.setTitle("Reaction Roles");
embedBuilder.setDescription("Click a button to get a Role");
builder.setEmbed(embedBuilder); // You could also use #setMessage(MessageEmbed)

// Change the Type
builder.setType(ReactionMessage.Type.SINGLE); // Default is MULTIPLE, so no need to change it if you want to use MULTIPLE

// Add buttons
builder.addOption("Role 1", "1234567890");
builder.addOption("Role 2", "0987654321");
````

You then can build the ReactionMessage with `ReactionMessage.Builder#build()`. This will return a new ReactionMessage Element.
If you may already have a ReactionMessage in your channel, use ReactionMessage.Builder#detect(TextChannel) to create a new ReactionMessage Instance with your elements and the already existing Message.
To detect the message, the embed must be the same as the one you used to create the ReactionMessage.

You can send the ReactionMessage with `ReactionMessage#send(TextChannel)`. This will send the Message and add the Buttons to it.

For example:
````java
// send a new ReactionMessage
ReactionMessage message = builder.build();
message.send(channel);

// detect an existing ReactionMessage
ReactionMessage message = builder.detect(channel);
message.refresh(channel); // Refreshs the Message and edits the Buttons. Is called in #detect() already.
```
