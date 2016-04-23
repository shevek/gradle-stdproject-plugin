**Install Prerequisites**

sudo apt-get install git openjdk-8-jdk openjdk-8-source

**Install [NetBeans](http://netbeans.org/downloads/)**

- You only need the Java SE package.
- You don't need root.
- You must accept the JUnit license.
- As of this writing 8.1 did not work; use 8.0.2.

**Install required plugins**

- Go to Tools -> Plugins
- Select the "Available Plugins" tab (second from left).
- Type "gradle" into the search box at the top right.
- Select and install the gradle plugin(s).

**Optional: Install vi**

- From the Tools -> Plugins menu
- Select the "Available Plugins" tab (second from left).
- Type "jvi" into the search box at the top right.
- Select and install the 'jVi Update Center for NetBeans'.
- Press 'Check for Updates' to refresh the listing.
- Select and install 'jVi for NetBeans'.

**Set up formatting options:**

- Go to Tools -> Options (Mac OS: NetBeans "Preferences...")
- Select the "Editor" tab, "Formatting" sub-tab.
  - Select Language: "Java"
  - Select Category: "Braces"
    - if: leave-alone
    - for: leave-alone
    - while: leave-alone
  - Select Category: "Imports"
    - Unselect "Class Count To Use Star Import"
  - Select Category: "Comments"
    - Unselect "Enable Comments Formatting"

**Set up code hints:**

- Go to Tools -> Options (Mac OS: NetBeans "Preferences...")
- Select the "Editor" tab, "Hints" sub-tab.
  - Select Language: "Java"
  - In the tree, Unselect "braces" hints

Now the NetBeans autoformatter will generate our canonical code style.
Hint: Press alt-shift-f before saving any file.

**Set up templates:**

- Go to Tools -> Options (Mac OS: NetBeans "Preferences...")
- Select the "Editor" tab, "Code Templates" sub-tab.
  - Select 'New', and call your new template 'psfl'
  - Use the following expansion:
```
private static final ${loggerType type="org.slf4j.Logger" default="Logger" editable="false"} LOG = ${loggerFactoryType type="org.slf4j.LoggerFactory" editable="false"}.getLogger(${classVar editable="false" currClassName default="getClass()"}.class);
```
- Now, in a class, typing `psfl<tab>` will insert a logger.

**Building and Testing**

See the [cheatsheet](CHEATSHEET.md).
Do not build or test from within the IDE. It won't hurt much, but it
isn't really supported.

