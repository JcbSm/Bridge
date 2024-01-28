# How to run & test the plugin.
This guide assumes you have an up-to-date fork of the project.

## Initial steps
1. Open the project in your preferred IDE.
2. JDK 18 is recommended.
3. Reload gradle.

## Run configurations & tasks
### Building the jar
The task `shadow > shadowJar` is used to compile the plugin. It will be output to the `build/` directory

![image](https://github.com/JcbSm/Bridge/assets/49797894/59a33305-c617-4ad4-b790-1c5b91dbacfc)

### Setting up a Minecraft Server
1. Inside the `/testsrv/` directory, there should be a file called `server.jar`. If not, run the task `server > downloadServerJar`
   - If this still does not work. Download the latest version of [paper](https://papermc.io/downloads/paper) and place that file in `/testsrv/`, and rename it to `server.jar`

![image](https://github.com/JcbSm/Bridge/assets/49797894/0495fd77-80a6-451a-aedf-d39282469d2f)

2. Now that you have a `server.jar`, you can run the tasks `server > run` which will run the Minecraft server.

> Note: The plugin will not load initially, as it has not been configured in `/testsrv/plugins/Bridge/config.yml`
> A guide on how to set up `config.yml` can be found [here](./configuration.md).
> - The `run` task will automatically run all prerequisite tasks beforehand, so there is no need to run `shadowJar`, `copyJar`, or `downloadServerJar`.

3. Modify `/testsrv/plugins/Bridge/config.yml` according to [this guide](./configuration.md).
4. Connect to the server using `localhost` as the server IP.
